package org.neo4j.cypher.recipes.linked_list;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.cypher.utilities.DatabaseFixture;

import static org.junit.Assert.assertTrue;

public class LinkedListTest
{
    private DatabaseFixture dbFixture;
    private LinkedList list;

    @Before
    public void setup()
    {
        dbFixture = DatabaseFixture
                .createDatabase()
                .populateWith( "CREATE (list{name:'my-list'})" )
                .noMigrations();
        list = new LinkedList( dbFixture.database() );
    }

    @After
    public void teardown()
    {
        dbFixture.shutdown();
    }

    @Test
    public void shouldAddNewElementToHeadOfList() throws Exception
    {
        // when
        list.addElement( "my-list", newElement( "a" ) );
        list.addElement( "my-list", newElement( "b" ) );
        list.addElement( "my-list", newElement( "c" ) );

        // then
        String cypher = "START owner=node:node_auto_index(name='my-list')\n" +
                "MATCH p=(owner)-[:HEAD]->(c)-[:PREV]->(b)-[:PREV]->(a)\n" +
                "WHERE c.value = 'c' AND b.value = 'b' AND a.value = 'a'\n" +
                "RETURN p";

        ExecutionResult result = dbFixture.execute( cypher );
        assertTrue( result.iterator().hasNext() );
    }

    private Map<String, Object> newElement( String value )
    {
        Map<String, Object> map = new HashMap<>();
        map.put( "value", value );
        return map;
    }
}
