package org.neo4j.cypher.recipes.relationships.general;

import java.util.HashMap;
import java.util.Map;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.ResourceIterator;

public class GeneralRelationships
{
    private final ExecutionEngine executionEngine;

    public GeneralRelationships( GraphDatabaseService db )
    {
        executionEngine = new ExecutionEngine( db );
    }

    public ResourceIterator<Map<String, Object>> findAllAddressesFor( String name )
    {
        String cypher = "MATCH (person)-[a:ADDRESS]->(address)\n" +
                "WHERE person.name = {name}\n" +
                "RETURN a.type AS type, address.firstline AS firstline";

        Map<String, Object> params = new HashMap<>();
        params.put( "name", name );

        return executionEngine.execute( cypher, params ).iterator();
    }

    public ResourceIterator<Map<String, Object>> findSpecificAddressFor( String name, String type )
    {
        String cypher = "MATCH (person)-[a:ADDRESS]->(address)\n" +
                "WHERE person.name = {name} AND a.type = {type}\n" +
                "RETURN address.firstline AS firstline";

        Map<String, Object> params = new HashMap<>();
        params.put( "name", name );
        params.put( "type", type );

        return executionEngine.execute( cypher, params ).iterator();
    }
}
