package org.neo4j.cypher.recipes.property_values;

import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.neo4j.cypher.utilities.DatabaseFixture;
import org.neo4j.graphdb.ResourceIterator;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class PropertyValuesTest
{
    private DatabaseFixture dbFixture;
    private PropertyValues propertyValues;

    @Before
    public void setup()
    {
        String cypher = "CREATE (dsfiggs:User {username:'dsfiggs'}),\n" +
                "       (glgregg:User {username:'glgregg'}),\n" +
                "       (larden:User {username:'larden'}),\n" +
                "       (trbaker:Company {username:'trbaker'}),\n" +
                "       (aksmith:User {username:'aksmith'}),\n" +
                "       (hjones:User {username:'hjones'}),\n" +
                "       (autopop:Project {name:'autopop', language:['ruby']}),\n" +
                "       (cmdBatch:Project {name:'cmd-batch', language:['java']}),\n" +
                "       (dotGrowl:Project {name:'dotGrowl', language:['c#','f#']}),\n" +
                "       (boint:Project {name:'boint', language:['ruby']}),\n" +
                "       (rfish:Project {name:'rfish', language:['ruby']}),\n" +
                "       (rup4j:Project {name:'rup4j', language:['java']}),\n" +
                "       (polyphony:Project {name:'polyphony', language:['java','scala']}),\n" +
                "       (bezl:Project {name:'bezl', language:['javascript']}),\n" +
                "       (dsfiggs)-[:WROTE]->(autopop),\n" +
                "       (dsfiggs)-[:WROTE]->(cmdBatch),\n" +
                "       (aksmith)-[:CONTRIBUTED_TO]->(cmdBatch),\n" +
                "       (hjones)-[:CONTRIBUTED_TO]->(cmdBatch),\n" +
                "       (hjones)-[:WROTE]->(polyphony),\n" +
                "       (hjones)-[:WROTE]->(bezl),\n" +
                "       (glgregg)-[:CONTRIBUTED_TO]->(autopop),\n" +
                "       (glgregg)-[:WROTE]->(dotGrowl),\n" +
                "       (larden)-[:CONTRIBUTED_TO]->(autopop),\n" +
                "       (larden)-[:WROTE]->(boint),\n" +
                "       (larden)-[:WROTE]->(rfish),\n" +
                "       (larden)-[:WROTE]->(rup4j)";

        dbFixture = new DatabaseFixture( cypher );
        propertyValues = new PropertyValues( dbFixture.graphDatabaseService() );
    }

    @After
    public void teardown()
    {
        dbFixture.shutdown();
    }

    @Test
    public void shouldFindProjectsWithSimilarLanguages() throws Exception
    {
        // when
        ResourceIterator<Map<String, Object>> results =
                propertyValues.findProjectsWithSimilarLanguagesFor( "dsfiggs" );

        // then
        Map<String, Object> result = results.next();
        assertEquals( "larden", result.get( "username" ) );
        assertEquals( "boint", result.get( "project" ) );
        assertArrayEquals( new String[]{"ruby"}, (Object[]) result.get( "languages" ) );

        result = results.next();
        assertEquals( "larden", result.get( "username" ) );
        assertEquals( "rfish", result.get( "project" ) );
        assertArrayEquals( new String[]{"ruby"}, (Object[]) result.get( "languages" ) );

        result = results.next();
        assertEquals( "hjones", result.get( "username" ) );
        assertEquals( "polyphony", result.get( "project" ) );
        assertArrayEquals( new String[]{"java", "scala"}, (Object[]) result.get( "languages" ) );

        assertFalse( results.hasNext() );

    }
}