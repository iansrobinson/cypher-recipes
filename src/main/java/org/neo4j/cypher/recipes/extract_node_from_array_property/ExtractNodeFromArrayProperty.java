package org.neo4j.cypher.recipes.extract_node_from_array_property;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.neo4j.cypher.javacompat.ExecutionEngine;

public class ExtractNodeFromArrayProperty
{
    private static final int BATCH_SIZE = 2;
    private final ExecutionEngine executionEngine;

    public ExtractNodeFromArrayProperty( ExecutionEngine executionEngine )
    {
        this.executionEngine = executionEngine;
    }

    public void apply()
    {
        String refactoringId = UUID.randomUUID().toString();

        System.out.println(refactoringId);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put( "refactoringId", refactoringId );
        params.put( "batchSize", BATCH_SIZE );

        long numberPropertiesRemoved = extractArrayPropertiesInBatch( params );
        while ( numberPropertiesRemoved > 0 )
        {
            numberPropertiesRemoved = extractArrayPropertiesInBatch( params );
        }

        deleteRefactoringNodeAndRelationships( params );
    }

    private void deleteRefactoringNodeAndRelationships( Map<String, Object> params )
    {
        String cypher = "MATCH (temp:Temp)<-[r:TEMP]-()\n" +
                "WHERE temp.`*` = {refactoringId}\n" +
                "DELETE temp, r";

        executionEngine.execute( cypher, params );
    }

    private long extractArrayPropertiesInBatch( Map<String, Object> params )
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
