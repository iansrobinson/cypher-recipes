package org.neo4j.cypher.recipes.shortest_weighted_path;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.GraphDatabaseService;

public class ShortestWeightedPath
{
    private final ExecutionEngine executionEngine;

    public ShortestWeightedPath( GraphDatabaseService db )
    {
        this.executionEngine = new ExecutionEngine( db );
    }

    public Iterator<Map<String, Object>> findShortestPath( String start, String end )
    {
        String cypher = "START  startNode=node:node_auto_index(name={startNode}),\n" +
                "       endNode=node:node_auto_index(name={endNode})\n" +
                "MATCH  p=(startNode)-[rel:CONNECTED_TO*1..4]->(endNode)\n" +
                "RETURN p AS shortestPath,\n" +
                "       reduce(weight=0, r in rel | weight+r.weight) AS totalWeight\n" +
                "       ORDER BY totalWeight ASC\n" +
                "       LIMIT 1";

        Map<String, Object> params = new HashMap<>();
        params.put( "startNode", start );
        params.put( "endNode", end );

        ExecutionResult result = executionEngine.execute( cypher, params );

        return result.iterator();
    }
}
