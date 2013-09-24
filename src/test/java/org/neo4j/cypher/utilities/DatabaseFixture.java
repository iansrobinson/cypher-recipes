package org.neo4j.cypher.utilities;

import java.util.Map;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseSettings;
import org.neo4j.test.TestGraphDatabaseFactory;

public class DatabaseFixture
{
    private final GraphDatabaseService dbFixture;
    private final ExecutionEngine executionEngine;

    public DatabaseFixture()
    {
        this( null );
    }

    public DatabaseFixture( String initialContents )
    {
        dbFixture = new TestGraphDatabaseFactory()
                .newImpermanentDatabaseBuilder().
                        setConfig( GraphDatabaseSettings.node_keys_indexable, "name" ).
                        setConfig( GraphDatabaseSettings.node_auto_indexing, "true" )
                .newGraphDatabase();
        executionEngine = new ExecutionEngine( dbFixture );
        execute( deleteReferenceNode() );
        if ( initialContents != null )
        {
            execute( initialContents );
        }
    }

    private String deleteReferenceNode()
    {
        return "START n=node(0) DELETE n";
    }

    public GraphDatabaseService graphDatabaseService()
    {
        return dbFixture;
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
        dbFixture.shutdown();
    }
}
