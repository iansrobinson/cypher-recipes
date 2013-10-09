/*
 * Copyright (C) 2012 Neo Technology
 * All rights reserved
 */
package org.neo4j.cypher.utilities;

import org.neo4j.graphdb.GraphDatabaseService;

public interface Migration
{
    void apply(GraphDatabaseService db);
}
