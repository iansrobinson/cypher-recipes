package org.neo4j.cypher.recipes.shortest_weighted_path;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;

public class ShortestWeightedPath
{
    private final ExecutionEngine executionEngine;

    public ShortestWeightedPath( ExecutionEngine executionEngine )
    {
        this.executionEngine = executionEngine;
    }

    public Iterator<Map<String,Object>> findShortestPath(String start, String end)
    {
        String cypher = "START startNode=node:node_auto_index(name={startNode}),\n" +
                "      endNode=node:node_auto_index(name={endNode})\n" +
                "MATCH p=(startNode)-[:CONNECTED_TO*1..4]->(endNode)\n" +
                "WITH  p, reduce(weight=0, r in relationships(p) : weight+r.weight) AS totalWeight\n" +
                "      ORDER BY totalWeight ASC\n" +
                "      LIMIT 1\n" +
                "RETURN p AS shortestPath, totalWeight";

        Map<String, Object> params = new HashMap<String, Object>();
        params.put( "startNode", start );
        params.put( "endNode", end );

        ExecutionResult result = executionEngine.execute( cypher, params );

        return result.iterator();
    }

}
