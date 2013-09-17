package org.neo4j.cypher.recipes.property_values;

import java.util.HashMap;
import java.util.Map;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.ResourceIterator;

public class PropertyValues
{
    private final ExecutionEngine executionEngine;

    public PropertyValues( GraphDatabaseService db )
    {
        executionEngine = new ExecutionEngine( db );
    }

    public ResourceIterator<Map<String, Object>> findProjectsWithSimilarLanguagesFor( String username )
    {
        String cypher = "MATCH (user:User)-[:WROTE]->(project:Project),\n" +
                "      (contributor:User)-[:CONTRIBUTED_TO]->(project),\n" +
                "      (contributor:User)-[:WROTE]->(otherProject:Project)\n" +
                "WHERE user.username = {username} \n" +
                "      AND ANY (otherLanguage IN otherProject.language \n" +
                "        WHERE ANY (language IN project.language \n" +
                "          WHERE language = otherLanguage))\n" +
                "RETURN contributor.username AS username,\n" +
                "       otherProject.name AS project,\n" +
                "       otherProject.language AS languages";

        Map<String, Object> params = new HashMap<>();
                params.put( "username", username );

        ExecutionResult result = executionEngine.execute( cypher, params );

        return result.iterator();
    }
}
