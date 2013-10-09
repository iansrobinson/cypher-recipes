package org.neo4j.cypher.recipes.refactoring.extract_node_from_relationship;

import java.util.HashMap;
import java.util.Map;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.cypher.utilities.Migration;
import org.neo4j.graphdb.GraphDatabaseService;

public class ExtractNodeFromRelationship implements Migration
{
    private static final int BATCH_SIZE = 2;

    @Override
    public void apply( GraphDatabaseService db )
    {
        ExecutionEngine executionEngine = new ExecutionEngine( db );

        Map<String, Object> params = new HashMap<>();
        params.put( "batchSize", BATCH_SIZE );

        long numberRelsDeleted = extractNodesInBatch( executionEngine, params );
        while ( numberRelsDeleted > 0L )
        {
            numberRelsDeleted = extractNodesInBatch( executionEngine, params );
        }

        long numberRIDsRemoved = removeRIDsInBatch( executionEngine, params );
        while ( numberRIDsRemoved > 0L )
        {
            numberRIDsRemoved = removeRIDsInBatch( executionEngine, params );
        }
    }

    private long removeRIDsInBatch( ExecutionEngine executionEngine, Map<String, Object> params )
    {
        String cypher = "MATCH (n:Email)\n" +
                "WHERE has(n._rid)\n" +
                "WITH n LIMIT {batchSize}\n" +
                "SET n._rid = null\n" +
                "RETURN count(n) AS numberRemoved";
        ExecutionResult result = executionEngine.execute( cypher, params );
        return (long) result.iterator().next().get( "numberRemoved" );
    }

    private long extractNodesInBatch( ExecutionEngine executionEngine, Map<String, Object> params )
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
