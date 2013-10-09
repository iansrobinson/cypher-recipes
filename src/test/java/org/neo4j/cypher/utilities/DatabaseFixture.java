package org.neo4j.cypher.utilities;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.test.TestGraphDatabaseFactory;

public class DatabaseFixture
{
    public static DatabaseFixtureBuilder createDatabase()
    {
        Map<String, String> config = new HashMap<>();
        config.put( "node_auto_indexing", "true" );
        config.put( "node_keys_indexable", "name" );

        return new DatabaseFixtureBuilder( new TestGraphDatabaseFactory()
                .newImpermanentDatabaseBuilder()
                .setConfig( config )
                .newGraphDatabase() );
    }

    public static DatabaseFixtureBuilder useExistingDatabase( GraphDatabaseService db )
    {
        return new DatabaseFixtureBuilder( db );
    }

    private final GraphDatabaseService db;
    private final ExecutionEngine executionEngine;

    private DatabaseFixture( GraphDatabaseService db, String initialContents, Iterable<Migration> migrations )
    {
        this.db = db;
        this.executionEngine = new ExecutionEngine( db );

        deleteReferenceNode();
        populateWith( initialContents );
        applyMigrations( migrations );
    }

    public GraphDatabaseService database()
    {
        return db;
    }

    public ExecutionEngine executionEngine()
    {
        return executionEngine;
    }

    public ExecutionResult execute( String cypher )
    {
        return executionEngine.execute( cypher );
    }

    public ExecutionResult execute( String cypher, Map<String, Object> params )
    {
        return executionEngine.execute( cypher, params );
    }

    public long labelledNodesWithProperty( String label, String property )
    {
        ExecutionResult result = execute(
                "MATCH (n:" + label + ") WHERE has(n." + property + ") RETURN count(n) AS totalNodes" );
        return (long) result.iterator().next().get( "totalNodes" );
    }

    public long totalNodeCount()
    {
        ExecutionResult result = execute(
                "MATCH (n) RETURN count(n) AS totalNodes" );
        return (long) result.iterator().next().get( "totalNodes" );
    }

    public long relCount( String relName )
    {
        ExecutionResult result = execute(
                "MATCH ()-[r:" + relName + "]->() RETURN count(r) AS relCount" );
        return (long) result.iterator().next().get( "relCount" );
    }

    public void shutdown()
    {
        db.shutdown();
    }

    private void deleteReferenceNode()
    {
        populateWith( "START n=node(0) DELETE n" );
    }

    private ExecutionResult populateWith( String cypher )
    {
        return execute( cypher );
    }

    private void applyMigrations( Iterable<Migration> migrations )
    {
        for ( Migration migration : migrations )
        {
            migration.apply( db );
        }
    }

    public static class DatabaseFixtureBuilder
    {
        private final GraphDatabaseService db;
        private String initialContents;

        private DatabaseFixtureBuilder( GraphDatabaseService db )
        {
            this.db = db;
        }

        public DatabaseFixtureBuilder populateWith( String cypher )
        {
            initialContents = cypher;
            return this;
        }

        public DatabaseFixtureBuilder empty()
        {
            initialContents = "MATCH n RETURN n LIMIT 1";
            return this;
        }

        public DatabaseFixture applyMigrations( Iterable<Migration> migrations )
        {
            return new DatabaseFixture( db, initialContents, migrations );
        }

        public DatabaseFixture noMigrations()
        {
            return new DatabaseFixture( db, initialContents, Collections.<Migration>emptyList() );
        }
    }
}