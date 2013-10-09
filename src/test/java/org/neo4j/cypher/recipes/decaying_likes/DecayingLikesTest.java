package org.neo4j.cypher.recipes.decaying_likes;

import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.neo4j.cypher.utilities.DatabaseFixture;
import org.neo4j.graphdb.ResourceIterator;

import static org.junit.Assert.assertEquals;

public class DecayingLikesTest
{
    private DatabaseFixture dbFixture;
    private DecayingLikes decayingLikes;

    @Before
    public void setup()
    {
        String cypher = "CREATE (ian:Person {name:'Ian'}),\n" +
                "       (bill:Person {name:'Bill'}),\n" +
                "       (lucy:Person {name:'Lucy'}),\n" +
                "       (sarah:Person {name:'Sarah'}),\n" +
                "       (odeon:Site {name:'Odeon'}),\n" +
                "       (ian)-[:LIKES{datetime:1378422000000, v:1}]->(odeon),\n" +
                "       (bill)-[:LIKES{datetime:1374879600000, v:1}]->(odeon),\n" +
                "       (lucy)-[:LIKES{datetime:1369609200000, v:1}]->(odeon),\n" +
                "       (sarah)-[:LIKES{datetime:1374879600000, v:-1}]->(odeon)";

        dbFixture = DatabaseFixture
                .createDatabase()
                .populateWith( cypher )
                .noMigrations();
        decayingLikes = new DecayingLikes( dbFixture.database() );
    }

    @After
    public void teardown()
    {
        dbFixture.shutdown();
    }

    @Test
    public void shouldCalculateScoreBasedOnDecayingLikes() throws Exception
    {
        // when
        // today in millis 1380409200000
        // 56days in millis 4838400000
        ResourceIterator<Map<String, Object>> results =
                decayingLikes.getScoreForSite( "Odeon", 1380409200000L, 4838400000L );

        // then
        assertEquals( 12L, results.next().get( "score" ) );
    }
}
