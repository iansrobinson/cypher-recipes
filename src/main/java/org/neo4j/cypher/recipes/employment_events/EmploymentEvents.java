package org.neo4j.cypher.recipes.employment_events;

import java.util.HashMap;
import java.util.Map;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.ResourceIterator;

public class EmploymentEvents
{
    private final ExecutionEngine executionEngine;

    public EmploymentEvents( GraphDatabaseService db )
    {
        this.executionEngine = new ExecutionEngine( db );
    }

    public ResourceIterator<Map<String, Object>> employmentFor( String name )
    {
        String cypher =
                "MATCH (user:User)-[:EMPLOYMENT]->(employment),\n" +
                        "      (employment)-[:ROLE]->(role),\n" +
                        "      (employment)-[:COMPANY]->(company)\n" +
                        "WHERE user.name={employeeName}\n" +
                        "RETURN role.title AS Role, company.name AS Company,\n" +
                        "       employment.from AS From, employment.to AS To\n" +
                        "ORDER BY From ASC";

        Map<String, Object> params = new HashMap<String, Object>();
        params.put( "employeeName", name );

        return executionEngine.execute( cypher, params ).iterator();
    }

    public ResourceIterator<Map<String, Object>> allPeopleInRoleBetweenDates( String role, Long from, Long to )
    {
        String cypher =
                "MATCH (user)-[:EMPLOYMENT]->(employment),\n" +
                        "      (employment)-[:ROLE]->(role:Role),\n" +
                        "      (employment)-[:COMPANY]->(company)\n" +
                        "WHERE role.title={role}\n" +
                        "      AND employment.from < {to} AND employment.to > {from}\n" +
                        "RETURN user.name AS Name, company.name AS Company,\n" +
                        "       employment.from AS From, employment.to AS To\n" +
                        "ORDER BY From ASC";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put( "role", role );
        params.put( "from", from );
        params.put( "to", to );

        return executionEngine.execute( cypher, params ).iterator();
    }

    public ResourceIterator<Map<String, Object>> allPeopleWhoWorkedForCompanyBetweenDates( String company,
                                                                                           Long from, Long to )
    {
        String cypher =
                "MATCH (user)-[:EMPLOYMENT]->(employment),\n" +
                        "      (employment)-[:ROLE]->(role),\n" +
                        "      (employment)-[:COMPANY]->(company:Company)\n" +
                        "WHERE company.name={company}\n" +
                        "      AND employment.from < {to} AND employment.to > {from}\n" +
                        "RETURN user.name AS Name, role.title AS Role,\n" +
                        "       employment.from AS From, employment.to AS To\n" +
                        "ORDER BY From ASC";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put( "company", company );
        params.put( "from", from );
        params.put( "to", to );

        return executionEngine.execute( cypher, params ).iterator();
    }
}
