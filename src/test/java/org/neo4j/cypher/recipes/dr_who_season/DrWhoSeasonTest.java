package org.neo4j.cypher.recipes.dr_who_season;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.cypher.utilities.DatabaseFixture;
import org.neo4j.graphdb.ResourceIterator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class DrWhoSeasonTest
{
    private DatabaseFixture db;
    private DrWhoSeason season;

    @Before
    public void setup()
    {
        db = new DatabaseFixture( "CREATE (season:Season{season:12})" );
        season = new DrWhoSeason( db.graphDatabaseService() );
    }

    @After
    public void teardown()
    {
        db.shutdown();
    }

    @Test
    public void shouldAddStoryToEmptySeason() throws Exception
    {
        // when
        season.addStory( 12L, newStory( "Robot" ) );

        // then
        String cypher = "MATCH p=(season:Season)-[:FIRST]->(story:Story)<-[:LAST]-(season)\n" +
                "WHERE season.season=12 AND story.title='Robot'\n" +
                "RETURN COUNT(p) AS result";
        ExecutionResult result = db.execute( cypher );
        assertEquals( 1L, result.iterator().next().get( "result" ) );
    }

    @Test
    public void shouldRelateNewStoryToPrevious() throws Exception
    {
        // when
        season.addStory( 12L, newStory( "Robot" ) );
        season.addStory( 12L, newStory( "Ark in Space" ) );

        // then
        String cypher = "MATCH (season:Season)-[:FIRST]->(story)\n" +
                "WHERE season.season=12 \n" +
                "RETURN story.title AS story";
        ResourceIterator<Map<String, Object>> result = db.execute( cypher ).iterator();
        assertEquals( "Robot", result.next().get( "story" ) );
        assertFalse( result.hasNext() );

        cypher = "MATCH (season:Season)-[:LAST]->(story)\n" +
                "WHERE season.season=12 \n" +
                "RETURN story.title AS story";
        result = db.execute( cypher ).iterator();
        assertEquals( "Ark in Space", result.next().get( "story" ) );
        assertFalse( result.hasNext() );
    }

    @Test
    public void shouldFindAllStoriesBroadcastSoFarInOrder() throws Exception
    {
        // given
        season.addStory( 12L, newStory( "Robot" ) );
        season.addStory( 12L, newStory( "Ark in Space" ) );
        season.addStory( 12L, newStory( "Genesis of the Daleks" ) );

        // when
        ResourceIterator<Map<String, Object>> result = season.findBroadcastStories( 12L );

        // then
        assertEquals( "Robot", result.next().get( "nextStory" ) );
        assertEquals( "Ark in Space", result.next().get( "nextStory" ) );
        assertEquals( "Genesis of the Daleks", result.next().get( "nextStory" ) );
        assertFalse( result.hasNext() );
    }

    @Test
    public void shouldFindLastStoryToBeBroadcast() throws Exception
    {
        // given
        season.addStory( 12L, newStory( "Robot" ) );
        season.addStory( 12L, newStory( "Ark in Space" ) );
        season.addStory( 12L, newStory( "Genesis of the Daleks" ) );

        // when
        ResourceIterator<Map<String, Object>> result = season.findLastStoryToBeBroadcast( 12L );

        // then
        assertEquals( "Genesis of the Daleks", result.next().get( "lastStory" ) );
        assertFalse( result.hasNext() );
    }

    private Map<String, Object> newStory( String title )
    {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put( "title", title );
        return map;
    }
}
