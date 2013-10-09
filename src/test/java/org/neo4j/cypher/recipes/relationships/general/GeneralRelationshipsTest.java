package org.neo4j.cypher.recipes.relationships.general;

import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.neo4j.cypher.utilities.DatabaseFixture;
import org.neo4j.graphdb.ResourceIterator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class GeneralRelationshipsTest
{
    private DatabaseFixture dbFixture;
    private GeneralRelationships generalRelationships;

    @Before
    public void setup()
    {
        String cypher = "CREATE (peter:Person{name:'Peter'}),\n" +
                "       (a1:Address{firstline:'178 Bag End'}),\n" +
                "       (a2:Address{firstline:'Suite 4'}),\n" +
                "       (peter)-[:ADDRESS{type:'home'}]->(a1),\n" +
                "       (peter)-[:ADDRESS{type:'work'}]->(a2)";

        dbFixture = DatabaseFixture
                .createDatabase()
                .populateWith( cypher )
                .noMigrations();
        generalRelationships = new GeneralRelationships( dbFixture.database() );
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
        ResourceIterator<Map<String, Object>> results = generalRelationships.findAllAddressesFor( "Peter" );

        // then
        Map<String, Object> result = results.next();
        assertEquals( "home", result.get( "type" ) );
        assertEquals( "178 Bag End", result.get( "firstline" ) );

        result = results.next();
        assertEquals( "work", result.get( "type" ) );
        assertEquals( "Suite 4", result.get( "firstline" ) );

        assertFalse( results.hasNext() );
    }

    @Test
    public void shouldFindSpecificAddress() throws Exception
    {
        // when
        ResourceIterator<Map<String, Object>> results = generalRelationships.findSpecificAddressFor( "Peter", "home" );

        // then
        Map<String, Object> result = results.next();
        assertEquals( "178 Bag End", result.get( "firstline" ) );

        assertFalse( results.hasNext() );
    }
}
