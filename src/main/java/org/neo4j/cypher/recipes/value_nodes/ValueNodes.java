package org.neo4j.cypher.recipes.value_nodes;

import java.util.HashMap;
import java.util.Map;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.ResourceIterator;

public class ValueNodes
{
    private final ExecutionEngine executionEngine;

    public ValueNodes( GraphDatabaseService db )
    {
        executionEngine = new ExecutionEngine( db );
    }

    public ResourceIterator<Map<String, Object>> findUsersWhoShareSkillsWith( String name, String skillLevel )
    {
        String cypher = "MATCH (user:Person)-[:HAS_SKILL]->(skill:Skill),\n" +
                "(user)-[:WORKS_FOR]->(company:Company),\n" +
                "(colleague:Person)-[:WORKS_FOR]->(company),\n" +
                "(colleague)-[r:HAS_SKILL]->(skill)\n" +
                "WHERE user.name = {name} AND r.level = {skillLevel}\n" +
                "RETURN colleague.name AS name, skill.name AS skill";

        Map<String, Object> params = new HashMap<String, Object>();
        params.put( "name", name );
        params.put( "skillLevel", skillLevel );

        return executionEngine.execute( cypher, params ).iterator();
    }
}
