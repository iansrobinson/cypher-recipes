package org.neo4j.cypher.recipes.decaying_likes;

import java.util.HashMap;
import java.util.Map;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.ResourceIterator;

public class DecayingLikes
{
    private final ExecutionEngine executionEngine;

        public DecayingLikes( GraphDatabaseService db )
        {
            this.executionEngine = new ExecutionEngine( db );
        }

    public ResourceIterator<Map<String, Object>> getScoreForSite( String site, long currentMillis, long intervalMillis )
    {
        String cypher = "MATCH (:Person)-[c:LIKES]->(site:Site)\n" +
                "WHERE site.name = {site}\n" +
                "RETURN REDUCE(score = 0, rel IN COLLECT(c) | score +\n" +
                "       (CASE ({currentMillis}-rel.datetime)/{intervalMillis}\n" +
                "            WHEN 0 THEN 10\n" +
                "            WHEN 1 THEN 5\n" +
                "            WHEN 2 THEN 2\n" +
                "            WHEN 3 THEN 1\n" +
                "            ELSE 0\n" +
                "       END) * rel.v) AS score";

               Map<String, Object> params = new HashMap<String, Object>();
               params.put( "site", site );
               params.put( "currentMillis", currentMillis );
               params.put( "intervalMillis", intervalMillis );

        return executionEngine.execute( cypher, params ).iterator();
    }

}
