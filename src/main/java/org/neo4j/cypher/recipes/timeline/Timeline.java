package org.neo4j.cypher.recipes.timeline;

import java.util.HashMap;
import java.util.Map;

import org.joda.time.DateTime;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.graphdb.ResourceIterator;

public class Timeline
{
    private final ExecutionEngine executionEngine;

    public Timeline( ExecutionEngine executionEngine )
    {
        this.executionEngine = executionEngine;
    }

    public void addNewNode( String timelineName, Map<String, Object> newNode, DateTime date )
    {
        String cypher = "MERGE (timeline:Timeline{name:{timelineName}})\n" +
                "WITH timeline\n" +
                "CREATE UNIQUE (timeline)-[:YEAR]->(year{value:{year}})\n" +
                "  -[:MONTH]->(month{value:{month}})\n" +
                "  -[:DAY]->(day{value:{day}, millis:{millis}})\n" +
                "  <-[:OCCURRED]-(n{newNode})";

        Map<String, Object> params = new HashMap<>();
        params.put( "timelineName", timelineName );
        params.put( "year", date.getYear() );
        params.put( "month", date.getMonthOfYear() );
        params.put( "day", date.getDayOfMonth() );
        params.put( "millis", date.getMillis());
        params.put( "newNode", newNode );

        executionEngine.execute( cypher, params );
    }

    public ResourceIterator<Map<String, Object>> findAllEventsBetween(
            DateTime startDate, DateTime endDate, String timelineName )
    {
        String cypher = "MATCH (timeline:Timeline)-[:YEAR]->(year)-[:MONTH]->(month)-[:DAY]->(day)<-[:OCCURRED]-(n)\n" +
                "WHERE timeline.name = {timelineName}\n" +
                "AND day.millis >= {startMillis} AND day.millis < {endMillis}\n" +
                "RETURN n";

        Map<String, Object> params = new HashMap<>();
        params.put( "timelineName", timelineName );
        params.put( "startMillis", startDate.getMillis() );
        params.put( "endMillis", endDate.getMillis() );

        return executionEngine.execute( cypher, params ).iterator();
    }
}
