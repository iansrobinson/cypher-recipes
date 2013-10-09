package org.neo4j.cypher.recipes.refactoring.extract_node_from_array_property;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.neo4j.cypher.utilities.DatabaseFixture;

import static org.junit.Assert.assertEquals;

public class ExtractNodeFromArrayPropertyTest
{
    private DatabaseFixture dbFixture;
    private ExtractNodeFromArrayProperty extractNodeFromArrayProperty;

    @Before
    public void setup()
    {
        String cypher = "CREATE (dsfiggs:User {username:'dsfiggs'}),\n" +
                "       (glgregg:User {username:'glgregg'}),\n" +
                "       (larden:User {username:'larden'}),\n" +
                "       (trbaker:User {username:'trbaker'}),\n" +
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
                "       (larden)-[:WROTE]->(rup4j),\n" +
                "       (trbaker)-[:CONTRIBUTED_TO]->(autopop)";

        dbFixture = DatabaseFixture
                .createDatabase()
                .populateWith( cypher )
                .noMigrations();
        extractNodeFromArrayProperty = new ExtractNodeFromArrayProperty();
    }

    @After
    public void teardown()
    {
        dbFixture.shutdown();
    }

    @Test
    public void shouldConvertEachElementInArrayToUniqueNode() throws Exception
    {
        // given
        assertEquals( 14L, dbFixture.totalNodeCount() );

        // when
        extractNodeFromArrayProperty.apply( dbFixture.database() );

        // then
        assertEquals( 20L, dbFixture.totalNodeCount() );
        assertEquals( 6L, dbFixture.labelledNodesWithProperty( "Language", "value" ) );
        assertEquals( 10L, dbFixture.relCount( "LANGUAGE" ) );

        assertEquals( 0L, dbFixture.labelledNodesWithProperty( "Temp", "refactoringId" ) );
        assertEquals( 0L, dbFixture.labelledNodesWithProperty( "Project", "language" ) );
        assertEquals( 0L, dbFixture.relCount( "TEMP" ) );
    }

}
