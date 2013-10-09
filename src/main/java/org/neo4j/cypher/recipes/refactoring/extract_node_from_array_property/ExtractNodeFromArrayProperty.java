package org.neo4j.cypher.recipes.refactoring.extract_node_from_array_property;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.utilities.Migration;
import org.neo4j.graphdb.GraphDatabaseService;

public class ExtractNodeFromArrayProperty implements Migration
{
    private static final int BATCH_SIZE = 2;

    @Override
    public void apply( GraphDatabaseService db )
    {
        ExecutionEngine executionEngine = new ExecutionEngine( db );
        String refactoringId = UUID.randomUUID().toString();

        Map<String, Object> params = new HashMap<>();
        params.put( "refactoringId", refactoringId );
        params.put( "batchSize", BATCH_SIZE );

        long numberPropertiesRemoved = extractArrayPropertiesInBatch( executionEngine, params );
        while ( numberPropertiesRemoved > 0 )
        {
            numberPropertiesRemoved = extractArrayPropertiesInBatch( executionEngine, params );
        }

        deleteRefactoringNodeAndRelationships( executionEngine, params );
    }

    private void deleteRefactoringNodeAndRelationships( ExecutionEngine executionEngine, Map<String, Object> params )
    {
        String cypher = "MATCH (temp:Temp)<-[r:TEMP]-()\n" +
                "WHERE temp.`*` = {refactoringId}\n" +
                "DELETE temp, r";

        executionEngine.execute( cypher, params );
    }

    private long extractArrayPropertiesInBatch( ExecutionEngine executionEngine, Map<String, Object> params )
    {
        String cypher = "MERGE (temp:Temp{refactoringId})\n" +
                "WITH temp\n" +
                "MATCH (project:Project) \n" +
                "WHERE has(project.language)\n" +
                "WITH project, temp LIMIT {batchSize}\n" +
                "FOREACH (l IN project.language | \n" +
                "  CREATE UNIQUE (language:Language{value:l})-[:TEMP]->(temp)\n" +
                "\t)\n" +
                "WITH project, temp\n" +
                "MATCH (language:Language) \n" +
                "WHERE language.value IN (project.language)\n" +
                "CREATE UNIQUE (project)-[:LANGUAGE]->(language)\n" +
                "SET project.language = null\n" +
                "RETURN count(project) AS numberRemoved";

        return (long) executionEngine.execute( cypher, params ).iterator().next().get( "numberRemoved" );
    }
}
