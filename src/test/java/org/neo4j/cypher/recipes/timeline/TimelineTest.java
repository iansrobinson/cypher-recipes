package org.neo4j.cypher.recipes.timeline;

import java.util.HashMap;
import java.util.Map;

import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.cypher.utilities.DatabaseFixture;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Transaction;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class TimelineTest
{
    private DatabaseFixture dbFixture;
    private Timeline timeline;

    @Before
    public void setup()
    {
        dbFixture = new DatabaseFixture();
        timeline = new Timeline( dbFixture.executionEngine() );
    }

    @After
    public void teardown()
    {
        dbFixture.shutdown();
    }

    @Test
    public void shouldAddNewNodeToTimeline() throws Exception
    {
        // given
        Map<String, Object> newNode = new HashMap<>();
        newNode.put( "name", "my-new-node" );

        // when
        timeline.addNewNode( "my-timeline", newNode, new LocalDate( 2007, 1, 14 ) );

        // then
        String cypher = "MATCH (t:Timeline)-[:YEAR]->(y)-[:MONTH]->(m)-[:DAY]->(d)-[:EVENT]->(n)\n" +
                "WHERE t.name = {timelineName} AND y.value = {year} AND m.value = {month} AND d.value = {day}\n" +
                "RETURN n.name AS name";

        Map<String, Object> params = new HashMap<>();
        params.put( "timelineName", "my-timeline" );
        params.put( "year", 2007 );
        params.put( "month", 1 );
        params.put( "day", 14 );

        ExecutionResult results = dbFixture.execute( cypher, params );

        assertEquals( "my-new-node", results.iterator().next().get( "name" ) );
    }

    @Test
    public void shouldGetNodesOnTimelineBetweenTwoDatesStartDateInclusive() throws Exception
    {
        Map<String, Object> node1 = new HashMap<>();
        node1.put( "name", "node1" );

        Map<String, Object> node2 = new HashMap<>();
        node2.put( "name", "node2" );

        Map<String, Object> node3 = new HashMap<>();
        node3.put( "name", "node3" );

        Map<String, Object> node4 = new HashMap<>();
        node4.put( "name", "node4" );

        Map<String, Object> node5 = new HashMap<>();
        node5.put( "name", "node5" );

        Map<String, Object> node6 = new HashMap<>();
        node6.put( "name", "node6" );

        timeline.addNewNode( "my-timeline", node1, new LocalDate( 2007, 1, 14 ) );
        timeline.addNewNode( "my-timeline", node2, new LocalDate( 2007, 1, 24 ) );
        timeline.addNewNode( "my-timeline", node3, new LocalDate( 2007, 2, 1 ) );
        timeline.addNewNode( "my-timeline", node4, new LocalDate( 2008, 1, 1 ) );
        timeline.addNewNode( "my-timeline", node5, new LocalDate( 2008, 1, 14 ) );
        timeline.addNewNode( "my-timeline", node6, new LocalDate( 2008, 1, 28 ) );

        // when
        try (Transaction tx = dbFixture.graphDatabaseService().beginTx())
        {
            ResourceIterator<Map<String, Object>> results = timeline.findAllEventsBetween(
                    new LocalDate( 2007, 1, 24 ), new LocalDate( 2008, 1, 14 ), "my-timeline" );

            // then
            assertEquals( "node2", ((Node) results.next().get( "n" )).getProperty( "name" ) );
            assertEquals( "node3", ((Node) results.next().get( "n" )).getProperty( "name" ) );
            assertEquals( "node4", ((Node) results.next().get( "n" )).getProperty( "name" ) );
            assertFalse( results.hasNext() );
            tx.success();
        }
    }
}
