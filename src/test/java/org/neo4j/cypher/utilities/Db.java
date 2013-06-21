package org.neo4j.cypher.utilities;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseSettings;
import org.neo4j.test.TestGraphDatabaseFactory;

public final class Db
{
    private Db()
    {
    }

    public static GraphDatabaseService impermanentDatabase()
    {
        return new TestGraphDatabaseFactory()
                .newImpermanentDatabaseBuilder().
                        setConfig( GraphDatabaseSettings.node_keys_indexable, "name" ).
                        setConfig( GraphDatabaseSettings.node_auto_indexing, "true" )
                .newGraphDatabase();
    }
}
