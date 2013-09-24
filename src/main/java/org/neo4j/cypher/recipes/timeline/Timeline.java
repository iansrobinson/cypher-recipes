package org.neo4j.cypher.recipes.timeline;

import java.util.HashMap;
import java.util.Map;

import org.joda.time.LocalDate;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.graphdb.ResourceIterator;

public class Timeline
{
    private final ExecutionEngine executionEngine;

    public Timeline( ExecutionEngine executionEngine )
    {
        this.executionEngine = executionEngine;
    }

    public void addNewNode( String timelineName, Map<String, Object> newNode, LocalDate date )
    {
        String cypher = "MERGE (timeline:Timeline{name:{timelineName}})\n" +
                "WITH timeline\n" +
                "CREATE UNIQUE (timeline)-[:YEAR]->(year{value:{year}})\n" +
                "  -[:MONTH]->(month{value:{month}})\n" +
                "  -[:DAY]->(day{value:{day}})\n" +
                "  -[:EVENT]->(n{newNode})";

        Map<String, Object> params = new HashMap<>();
        params.put( "timelineName", timelineName );
        params.put( "year", date.getYear() );
        params.put( "month", date.getMonthOfYear() );
        params.put( "day", date.getDayOfMonth() );
        params.put( "newNode", newNode );

        executionEngine.execute( cypher, params );
    }

    public ResourceIterator<Map<String, Object>> findAllEventsBetween(
            LocalDate startDate, LocalDate endDate, String timelineName )
    {
        String cypher = "MATCH (timeline:Timeline)-[:YEAR]->(year)-[:MONTH]->(month)-[:DAY]->(day)-[:EVENT]->(n)\n" +
                "WHERE timeline.name = {timelineName}\n" +
                "AND ((year.value > {startYear} AND year.value < {endYear})\n" +
                "     OR ({startYear} = {endYear} AND {startMonth} = {endMonth}\n" +
                "         AND year.value = {startYear} AND month.value = {startMonth}\n" +
                "         AND day.value >= {startDay} AND day.value < {endDay})\n" +
                "     OR ({startYear} = {endYear} AND {startMonth} < {endMonth}\n" +
                "         AND year.value = {startYear}\n" +
                "         AND ((month.value = {startMonth} AND day.value >= {startDay})\n" +
                "              OR (month.value > {startMonth} AND month.value < {endMonth})\n" +
                "              OR (month.value = {endMonth} AND day.value < {endDay}))) \n" +
                "     OR ({startYear} < {endYear}\n" +
                "         AND year.value = {startYear}\n" +
                "         AND ((month.value > {startMonth})\n" +
                "              OR (month.value = {startMonth} AND day.value >= {startDay})))\n" +
                "     OR ({startYear} < {endYear}\n" +
                "         AND year.value = {endYear}\n" +
                "         AND ((month.value < {endMonth})\n" +
                "              OR (month.value = {endMonth} AND day.value < {endDay}))))\n" +
                "RETURN n";

        Map<String, Object> params = new HashMap<>();
        params.put( "timelineName", timelineName );
        params.put( "startYear", startDate.getYear() );
        params.put( "startMonth", startDate.getMonthOfYear() );
        params.put( "startDay", startDate.getDayOfMonth() );
        params.put( "endYear", endDate.getYear() );
        params.put( "endMonth", endDate.getMonthOfYear() );
        params.put( "endDay", endDate.getDayOfMonth() );

        return executionEngine.execute( cypher, params ).iterator();
    }
}
