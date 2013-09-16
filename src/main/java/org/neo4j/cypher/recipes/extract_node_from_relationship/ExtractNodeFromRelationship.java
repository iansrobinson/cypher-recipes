package org.neo4j.cypher.recipes.extract_node_from_relationship;

import java.util.HashMap;
import java.util.Map;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;

public class ExtractNodeFromRelationship
{
    private static final int BATCH_SIZE = 2;
    private final ExecutionEngine executionEngine;

    public ExtractNodeFromRelationship( ExecutionEngine executionEngine )
    {
        this.executionEngine = executionEngine;
    }

    public void apply()
    {
        Map<String, Object> params = new HashMap<String, Object>(  );
        params.put( "batchSize", BATCH_SIZE );

        long numberRelsDeleted = extractNodesInBatch(params);
        while ( numberRelsDeleted > 0L )
        {
            numberRelsDeleted = extractNodesInBatch(params);
        }

        long numberRIDsRemoved = removeRIDsInBatch(params);
        while ( numberRIDsRemoved > 0L )
        {
            numberRIDsRemoved = removeRIDsInBatch(params);
        }
    }

    private long removeRIDsInBatch(Map<String, Object> params)
    {
        String cypher = "MATCH (n:Email)\n" +
                "WHERE has(n._rid)\n" +
                "WITH n LIMIT {batchSize}\n" +
                "SET n._rid = null\n" +
                "RETURN count(n) AS numberRemoved";
        ExecutionResult result = executionEngine.execute( cypher, params );
        return (long) result.iterator().next().get( "numberRemoved" );
    }

    private long extractNodesInBatch(Map<String, Object> params)
    {
        String cypher = "MATCH (a:User)-[r:EMAILED]->(b:User)\n" +
                "WITH a, r, b LIMIT {batchSize}\n" +
                "CREATE UNIQUE (a)-[:SENT]->(:Email{content:r.content, _rid:id(r)})-[:TO]->(b)\n" +
                "DELETE r\n" +
                "RETURN count(r) AS numberDeleted";
        ExecutionResult result = executionEngine.execute( cypher, params );
        return (long) result.iterator().next().get( "numberDeleted" );
    }

}
