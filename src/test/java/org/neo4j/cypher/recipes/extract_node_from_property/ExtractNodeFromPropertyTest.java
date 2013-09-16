package org.neo4j.cypher.recipes.extract_node_from_property;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.neo4j.cypher.utilities.DatabaseFixture;

import static org.junit.Assert.assertEquals;

public class ExtractNodeFromPropertyTest
{
    private DatabaseFixture dbFixture;
    private ExtractNodeFromProperty extractNodeFromProperty;

    @Before
    public void setup()
    {
        String cypher = "CREATE \n" +
                "(p1:Party{name:'Party 1'}),\n" +
                "(p2:Party{name:'Party 2'}),\n" +
                "(t1:Trade{amount:100.00,currency:'GBP'}),\n" +
                "(t2:Trade{amount:25.00,currency:'USD'}),\n" +
                "(t3:Trade{amount:325.00,currency:'GBP'}),\n" +
                "(p1)<-[:BUYER]-(t1)-[:SELLER]->(p2),\n" +
                "(p1)<-[:BUYER]-(t2)-[:SELLER]->(p2),\n" +
                "(p2)<-[:BUYER]-(t3)-[:SELLER]->(p1)";

        dbFixture = new DatabaseFixture( cypher );
        extractNodeFromProperty = new ExtractNodeFromProperty(dbFixture.executionEngine());
    }

    @After
    public void teardown()
    {
        dbFixture.shutdown();
    }

    @Test
    public void shouldConvertPropertiesToNodes() throws Exception
    {
        // given
        assertEquals(5L, dbFixture.totalNodeCount());

        // when
        extractNodeFromProperty.apply();

        // then
        assertEquals( 7L, dbFixture.totalNodeCount() );
        assertEquals( 2L, dbFixture.labelledNodesWithProperty("Currency", "code") );
        assertEquals( 0L, dbFixture.labelledNodesWithProperty("Trade", "currency") );
    }
}
