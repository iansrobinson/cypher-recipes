package org.neo4j.cypher.recipes.dr_who_season;

import java.util.HashMap;
import java.util.Map;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.ResourceIterator;

public class DrWhoSeason
{
    private final ExecutionEngine executionEngine;

    public DrWhoSeason( GraphDatabaseService db )
    {
        this.executionEngine = new ExecutionEngine( db );
    }

    public void addStory( Long seasonNumber, Map<String, Object> story )
    {
        String cypher = "MATCH (season:Season) WHERE season.season = {seasonNumber}\n" +
                "CREATE UNIQUE (season)-[:LAST]->(newStory:Story{story})\n" +
                "WITH season, newStory\n" +

                "// Determine whether first story already exists\n" +
                "WITH season, newStory, \n" +
                "     CASE WHEN NOT ((season)-[:FIRST]->()) THEN [1] ELSE []\n" +
                "     END \n" +
                "  AS firstExists" +

                "// Create FIRST rel newStory is first story\n" +
                "FOREACH (i IN firstExists | CREATE UNIQUE (season)-[:FIRST]->(newStory))\n" +
                "WITH season, newStory\n" +

                "// Delete old LAST relationship\n" +
                "MATCH (newStory)<-[:LAST]-(season)-[oldRel:LAST]->(oldLast)\n" +
                "DELETE oldRel\n" +
                "CREATE (oldLast)-[:NEXT]->(newStory)";

        Map<String, Object> params = new HashMap<>();
        params.put( "seasonNumber", seasonNumber );
        params.put( "story", story );

        executionEngine.execute( cypher, params );
    }

    public ResourceIterator<Map<String, Object>> findBroadcastStories( Long seasonNumber )
    {
        String cypher = "MATCH (season:Season)-[:FIRST]->(firstStory)-[:NEXT*0..]->(nextStory)\n" +
                "WHERE season.season = {seasonNumber}\n" +
                "RETURN nextStory.title AS nextStory";

                Map<String, Object> params = new HashMap<>();
                params.put( "seasonNumber", seasonNumber );

                return executionEngine.execute( cypher, params ).iterator();
    }

    public ResourceIterator<Map<String, Object>> findLastStoryToBeBroadcast( Long seasonNumber )
       {
           String cypher = "MATCH (season:Season)-[:LAST]->(lastStory)\n" +
                   "WHERE season.season = {seasonNumber}\n" +
                   "RETURN lastStory.title AS lastStory";

                   Map<String, Object> params = new HashMap<>();
                   params.put( "seasonNumber", seasonNumber );

                   return executionEngine.execute( cypher, params ).iterator();
       }
}
