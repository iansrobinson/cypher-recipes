package org.neo4j.cypher.recipes.refactoring.extract_node_from_property;

import java.util.HashMap;
import java.util.Map;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.utilities.Migration;
import org.neo4j.graphdb.GraphDatabaseService;

public class ExtractNodeFromProperty implements Migration
{
    private static final int BATCH_SIZE = 2;

    @Override
    public void apply( GraphDatabaseService db )
    {
        ExecutionEngine executionEngine = new ExecutionEngine( db );

        Map<String, Object> params = new HashMap<>();
        params.put( "batchSize", BATCH_SIZE );

        long numberPropertiesRemoved = extractPropertiesInBatch( executionEngine, params );
        while ( numberPropertiesRemoved > 0 )
        {
            numberPropertiesRemoved = extractPropertiesInBatch( executionEngine, params );
        }
    }

    private long extractPropertiesInBatch( ExecutionEngine executionEngine, Map<String, Object> params )
    {
        String cypher = "MATCH (t:Trade) WHERE has(t.currency)\n" +
                "WITH t LIMIT {batchSize}\n" +
                "MERGE (c:Currency{code:t.currency})\n" +
                "CREATE UNIQUE (t)-[:CURRENCY]->(c)\n" +
                "SET t.currency=null\n" +
                "RETURN count(t) AS numberRemoved";

        return (long) executionEngine.execute( cypher, params ).iterator().next().get( "numberRemoved" );
    }
}
