package org.neo4j.cypher.recipes.linked_list;

import java.util.HashMap;
import java.util.Map;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.graphdb.GraphDatabaseService;

public class LinkedList
{
    private final ExecutionEngine executionEngine;

    public LinkedList( GraphDatabaseService db )
    {
        this.executionEngine = new ExecutionEngine( db );
    }

    public void addElement( String listName, Map<String, Object> newElement )
    {
        String cypher = "START owner=node:node_auto_index(name={listName})\n" +
                "CREATE UNIQUE (owner)-[:HEAD]->(newHead{newElement})\n" +
                "WITH owner, newHead\n" +
                "MATCH (newHead)<-[:HEAD]-(owner)-[oldRel:HEAD]->(oldHead)\n" +
                "DELETE oldRel\n" +
                "CREATE (newHead)-[:PREV]->(oldHead)";

        Map<String, Object> params = new HashMap<String, Object>();
        params.put( "listName", listName );
        params.put( "newElement", newElement );

        executionEngine.execute( cypher, params );
    }
}
