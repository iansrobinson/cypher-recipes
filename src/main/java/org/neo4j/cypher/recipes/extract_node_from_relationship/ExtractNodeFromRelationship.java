package org.neo4j.cypher.recipes.extract_node_from_relationship;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;

public class ExtractNodeFromRelationship
{
    private final ExecutionEngine executionEngine;

    public ExtractNodeFromRelationship( ExecutionEngine executionEngine )
    {
        this.executionEngine = executionEngine;
    }

    public void extractNodes()
    {
        long numberRelsDeleted = extractNodesInBatch();
        while ( numberRelsDeleted > 0L )
        {
            numberRelsDeleted = extractNodesInBatch();
        }

        long numberRIDsRemoved = removeRIDsInBatch();
        while ( numberRIDsRemoved > 0L )
        {
            numberRIDsRemoved = removeRIDsInBatch();
        }
    }

    private long removeRIDsInBatch()
    {
        String cypher = "MATCH (n:Email)\n" +
                "WHERE has(n._rid)\n" +
                "WITH n LIMIT 2\n" +
                "SET n._rid = null\n" +
                "RETURN count(n) AS numberRemoved";
        ExecutionResult result = executionEngine.execute( cypher );
        return (long) result.iterator().next().get( "numberRemoved" );
    }

    private long extractNodesInBatch()
    {
        String cypher = "MATCH (a:User)-[r:EMAILED]->(b:User)\n" +
                "WITH a, r, b LIMIT 2\n" +
                "CREATE UNIQUE (a)-[:SENT]->(:Email{content:r.content, _rid:id(r)})-[:TO]->(b)\n" +
                "DELETE r\n" +
                "RETURN count(r) AS numberDeleted";
        ExecutionResult result = executionEngine.execute( cypher );
        return (long) result.iterator().next().get( "numberDeleted" );
    }

}
