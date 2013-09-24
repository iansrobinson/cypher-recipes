package org.neo4j.cypher.recipes.relationships.specific;

import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.neo4j.cypher.utilities.DatabaseFixture;
import org.neo4j.graphdb.ResourceIterator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class SpecificRelationshipsTest
{
    private DatabaseFixture dbFixture;
    private SpecificRelationships specificRelationships;

    @Before
    public void setup()
    {
        String cypher = "CREATE (peter:Person{name:'Peter'}),\n" +
                "       (a1:Address{firstline:'178 Bag End'}),\n" +
                "       (a2:Address{firstline:'Suite 4'}),\n" +
                "       (peter)-[:HOME_ADDRESS]->(a1),\n" +
                "       (peter)-[:WORK_ADDRESS]->(a2)";

        dbFixture = new DatabaseFixture( cypher );
        specificRelationships = new SpecificRelationships( dbFixture.graphDatabaseService() );
    }

    @After
    public void teardown()
    {
        dbFixture.shutdown();
    }

    @Test
    public void shouldFindAllAddresses() throws Exception
    {
        // when
        ResourceIterator<Map<String, Object>> results = specificRelationships.findAllAddressesFor( "Peter" );

        // then
        Map<String, Object> result = results.next();
        assertEquals( "HOME_ADDRESS", result.get( "type" ) );
        assertEquals( "178 Bag End", result.get( "firstline" ) );

        result = results.next();
        assertEquals( "WORK_ADDRESS", result.get( "type" ) );
        assertEquals( "Suite 4", result.get( "firstline" ) );

        assertFalse( results.hasNext() );
    }

    @Test
    public void shouldFindSpecificAddress() throws Exception
    {
        // when
        ResourceIterator<Map<String, Object>> results = specificRelationships.findHomeAddressFor( "Peter" );

        // then
        Map<String, Object> result = results.next();
        assertEquals( "178 Bag End", result.get( "firstline" ) );

        assertFalse( results.hasNext() );
    }
}
