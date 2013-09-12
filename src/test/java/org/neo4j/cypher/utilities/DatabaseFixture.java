package org.neo4j.cypher.utilities;

import java.util.Map;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseSettings;
import org.neo4j.test.TestGraphDatabaseFactory;

public class DatabaseFixture
{
    private final GraphDatabaseService db;
    private final ExecutionEngine executionEngine;

    public DatabaseFixture( String initialContents )
    {
        db = new TestGraphDatabaseFactory()
                .newImpermanentDatabaseBuilder().
                        setConfig( GraphDatabaseSettings.node_keys_indexable, "name" ).
                        setConfig( GraphDatabaseSettings.node_auto_indexing, "true" )
                .newGraphDatabase();
        executionEngine = new ExecutionEngine( db );
        execute( initialContents );
    }

    public GraphDatabaseService graphDatabaseService()
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
        return executionEngine.execute( cypher );
    }

    public void shutdown()
    {
        db.shutdown();
    }
}
