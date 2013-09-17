package org.neo4j.cypher.recipes.refactoring.extract_node_from_relationship;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.neo4j.cypher.utilities.DatabaseFixture;

import static org.junit.Assert.assertEquals;

public class ExtractNodeFromRelationshipTest
{
    private DatabaseFixture dbFixture;
    private ExtractNodeFromRelationship extractNodeFromRelationship;

    @Before
    public void setup()
    {
        String cypher = "CREATE (bill:User{name:'Bill'}),\n" +
                "       (ben:User{name:'Ben'}),\n" +
                "       (sarah:User{name:'Sarah'}),\n" +
                "       (lucy:User{name:'Lucy'}),\n" +
                "       (toby:User{name:'Toby'}),\n" +
                "       (bill)-[:EMAILED{content:'Email 1'}]->(ben),\n" +
                "       (bill)-[:EMAILED{content:'Email 2'}]->(lucy),\n" +
                "       (lucy)-[:EMAILED{content:'Email 3'}]->(sarah),\n" +
                "       (lucy)-[:EMAILED{content:'Email 4'}]->(bill),\n" +
                "       (sarah)-[:EMAILED{content:'Email 5'}]->(toby),\n" +
                "       (toby)-[:EMAILED{content:'Email 6'}]->(ben),\n" +
                "       (ben)-[:EMAILED{content:'Email 7'}]->(bill),\n" +
                "       (ben)-[:EMAILED{content:'Email 8'}]->(lucy)";

        dbFixture = new DatabaseFixture( cypher );
        extractNodeFromRelationship = new ExtractNodeFromRelationship( dbFixture.executionEngine() );
    }

    @Test
    public void shouldCreateNewNodeAndRelsForEachEmailedRel() throws Exception
    {
        // given
        assertEquals( 5L, dbFixture.totalNodeCount() );
        assertEquals( 8L, dbFixture.relCount( "EMAILED" ) );
        assertEquals( 0L, dbFixture.relCount( "SENT" ) );
        assertEquals( 0L, dbFixture.relCount( "TO" ) );

        // when
        extractNodeFromRelationship.apply();

        // then
        assertEquals( 13L, dbFixture.totalNodeCount() );
        assertEquals( 8L, dbFixture.relCount( "SENT" ) );
        assertEquals( 8L, dbFixture.relCount( "TO" ) );
        assertEquals( 0L, dbFixture.relCount( "EMAILED" ) );
        assertEquals( 0L, dbFixture.labelledNodesWithProperty( "Email", "_rid" ) );
    }

    @After
    public void teardown()
    {
        dbFixture.shutdown();
    }
}
