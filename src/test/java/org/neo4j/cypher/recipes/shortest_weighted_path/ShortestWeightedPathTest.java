package org.neo4j.cypher.recipes.shortest_weighted_path;

import java.util.Iterator;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.utilities.Db;
import org.neo4j.graphdb.GraphDatabaseService;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class ShortestWeightedPathTest
{
    private GraphDatabaseService db;
    private ExecutionEngine executionEngine;

    @Before
    public void setup()
    {
        db = Db.impermanentDatabase();
        executionEngine = new ExecutionEngine( db );

        populateDb();
    }

    @After
    public void teardown()
    {
        db.shutdown();
    }

    @Test
    public void shouldFindShortestPath() throws Exception
    {
        // given
        ShortestWeightedPath shortestWeightedPath = new ShortestWeightedPath( executionEngine );

        // when
        Iterator<Map<String,Object>> results = shortestWeightedPath.findShortestPath( "A", "I" );

        // then
        Map<String, Object> result = results.next();

        assertEquals(8L, result.get( "totalWeight" ));

        assertFalse(results.hasNext());
    }

    private void populateDb()
    {
        String cypher = "CREATE\n" +
                "(a{name:'A'}),\n" +
                "(b{name:'B'}),\n" +
                "(c{name:'C'}),\n" +
                "(d{name:'D'}),\n" +
                "(e{name:'E'}),\n" +
                "(f{name:'F'}),\n" +
                "(g{name:'G'}),\n" +
                "(h{name:'H'}),\n" +
                "(i{name:'I'}),\n" +
                "(a)-[:CONNECTED_TO{weight:5}]->(b)-[:CONNECTED_TO{weight:6}]->(c)-[:CONNECTED_TO{weight:4}]->(i),\n" +
                "(a)-[:CONNECTED_TO{weight:3}]->(d)-[:CONNECTED_TO{weight:4}]->(e)-[:CONNECTED_TO{weight:5}]->(i),\n" +
                "(a)-[:CONNECTED_TO{weight:2}]->(f)-[:CONNECTED_TO{weight:3}]->(g)-[:CONNECTED_TO{weight:2}]->(h)\n" +
                "-[:CONNECTED_TO{weight:1}]->(i)";
        executionEngine.execute( cypher );
    }
}
