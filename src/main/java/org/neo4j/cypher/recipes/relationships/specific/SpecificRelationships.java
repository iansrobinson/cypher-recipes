package org.neo4j.cypher.recipes.relationships.specific;

import java.util.HashMap;
import java.util.Map;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.ResourceIterator;

public class SpecificRelationships
{
    private final ExecutionEngine executionEngine;

    public SpecificRelationships( GraphDatabaseService db )
    {
        executionEngine = new ExecutionEngine( db );
    }

    public ResourceIterator<Map<String, Object>> findAllAddressesFor( String name )
        {
            String cypher = "MATCH (person)-[a:HOME_ADDRESS|WORK_ADDRESS]->(address)\n" +
                    "WHERE person.name = {name}\n" +
                    "RETURN type(a) AS type, address.firstline AS firstline";

            Map<String, Object> params = new HashMap<>();
            params.put( "name", name );

            return executionEngine.execute( cypher, params ).iterator();
        }

        public ResourceIterator<Map<String, Object>> findHomeAddressFor( String name )
        {
            String cypher = "MATCH (person)-[:HOME_ADDRESS]->(address)\n" +
                    "WHERE person.name = {name}\n" +
                    "RETURN address.firstline AS firstline";

            Map<String, Object> params = new HashMap<>();
            params.put( "name", name );

            return executionEngine.execute( cypher, params ).iterator();
        }
}
