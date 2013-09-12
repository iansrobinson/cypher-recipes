package org.neo4j.cypher.recipes.employment_events;

import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.neo4j.cypher.utilities.DatabaseFixture;
import org.neo4j.graphdb.ResourceIterator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class EmploymentEventsTest
{
    private DatabaseFixture dbFixture;
    private EmploymentEvents employmentEvents;

    @Before
    public void setup()
    {
        String cypher = "CREATE\n" +
                      "(patrick:User{name:'Patrick'}),\n" +
                      "(cindy:User{name:'Cindy'}),\n" +
                      "(david:User{name:'David'}),\n" +
                      "(alice:User{name:'Alice'}),\n" +
                      "(acme:Company{name:'Acme'}),\n" +
                      "(startup:Company{name:'Startup'}),\n" +
                      "(dev:Role{title:'Software Developer'}),\n" +
                      "(qa:Role{title:'QA'}),\n" +
                      "(e1{from:2001,to:2005}),\n" +
                      "(e2{from:2005,to:2009}),\n" +
                      "(e3{from:1999,to:2003}),\n" +
                      "(e4{from:2004,to:2008}),\n" +
                      "(e5{from:2008,to:2010}),\n" +
                      "(patrick)-[:EMPLOYMENT]->(e1)-[:ROLE]->(dev),\n" +
                      "(e1)-[:COMPANY]->(acme),\n" +
                      "(patrick)-[:EMPLOYMENT]->(e2)-[:ROLE]->(dev),\n" +
                      "(e2)-[:COMPANY]->(startup),\n" +
                      "(cindy)-[:EMPLOYMENT]->(e3)-[:ROLE]->(qa),\n" +
                      "(e3)-[:COMPANY]->(acme),\n" +
                      "(david)-[:EMPLOYMENT]->(e4)-[:ROLE]->(qa),\n" +
                      "(e4)-[:COMPANY]->(startup),\n" +
                      "(alice)-[:EMPLOYMENT]->(e5)-[:ROLE]->(qa),\n" +
                      "(e5)-[:COMPANY]->(acme)";

        dbFixture = new DatabaseFixture( cypher );
        employmentEvents = new EmploymentEvents( dbFixture.graphDatabaseService() );
    }

    @After
    public void teardown()
    {
        dbFixture.shutdown();
    }


    @Test
    public void shouldFindAllInstancesOfEmployment() throws Exception
    {
        // when
        ResourceIterator<Map<String, Object>> results = employmentEvents.employmentFor( "Patrick" );

        // then
        Map<String, Object> result = results.next();
        assertEquals( "Software Developer", result.get( "Role" ) );
        assertEquals( "Acme", result.get( "Company" ) );
        assertEquals( 2001L, result.get( "From" ) );
        assertEquals( 2005L, result.get( "To" ) );

        result = results.next();
        assertEquals( "Software Developer", result.get( "Role" ) );
        assertEquals( "Startup", result.get( "Company" ) );
        assertEquals( 2005L, result.get( "From" ) );
        assertEquals( 2009L, result.get( "To" ) );

        assertFalse( results.hasNext() );
    }

    @Test
    public void shouldFindAllAllPeopleInRole() throws Exception
    {
        // when
        ResourceIterator<Map<String, Object>> results =
                employmentEvents.allPeopleInRoleBetweenDates( "QA", 1999L, 2007L );

        // then
        Map<String, Object> result = results.next();
        assertEquals( "Cindy", result.get( "Name" ) );
        assertEquals( "Acme", result.get( "Company" ) );
        assertEquals( 1999L, result.get( "From" ) );
        assertEquals( 2003L, result.get( "To" ) );

        result = results.next();
        assertEquals( "David", result.get( "Name" ) );
        assertEquals( "Startup", result.get( "Company" ) );
        assertEquals( 2004L, result.get( "From" ) );
        assertEquals( 2008L, result.get( "To" ) );

        assertFalse( results.hasNext() );
    }

    @Test
    public void shouldFindAllPeopleWhoWorkedForCompany() throws Exception
    {
        // when

        ResourceIterator<Map<String, Object>> results =
                employmentEvents.allPeopleWhoWorkedForCompanyBetweenDates( "Acme", 1999L, 2007L );

        // then
        Map<String, Object> result = results.next();
        assertEquals( "Cindy", result.get( "Name" ) );
        assertEquals( "QA", result.get( "Role" ) );
        assertEquals( 1999L, result.get( "From" ) );
        assertEquals( 2003L, result.get( "To" ) );

        result = results.next();
        assertEquals( "Patrick", result.get( "Name" ) );
        assertEquals( "Software Developer", result.get( "Role" ) );
        assertEquals( 2001L, result.get( "From" ) );
        assertEquals( 2005L, result.get( "To" ) );

        assertFalse( results.hasNext() );
    }
}
