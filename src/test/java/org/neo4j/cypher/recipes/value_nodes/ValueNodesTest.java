package org.neo4j.cypher.recipes.value_nodes;

import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.neo4j.cypher.utilities.DatabaseFixture;
import org.neo4j.graphdb.ResourceIterator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class ValueNodesTest
{
    private DatabaseFixture dbFixture;
    ValueNodes valueNodes;


    @Before
    public void setup()
    {
        String cypher = "CREATE (ian:Person {name:'Ian'}),\n" +
                        "       (bill:Person {name:'Bill'}),\n" +
                        "       (lucy:Person {name:'Lucy'}),\n" +
                        "       (acme:Company {name:'Acme'}),\n" +
                        "       (java:Skill {name:'Java'}),\n" +
                        "       (csharp:Skill {name:'C#'}),\n" +
                        "       (neo4j:Skill {name:'Neo4j'}),\n" +
                        "       (ruby:Skill {name:'Ruby'}),\n" +
                        "       (ian)-[:WORKS_FOR]->(acme),\n" +
                        "       (bill)-[:WORKS_FOR]->(acme),\n" +
                        "       (lucy)-[:WORKS_FOR]->(acme),\n" +
                        "       (ian)-[:HAS_SKILL{level:'expert'}]->(java),\n" +
                        "       (ian)-[:HAS_SKILL{level:'beginner'}]->(csharp),\n" +
                        "       (ian)-[:HAS_SKILL{level:'advanced'}]->(neo4j),\n" +
                        "       (bill)-[:HAS_SKILL{level:'advanced'}]->(neo4j),\n" +
                        "       (bill)-[:HAS_SKILL{level:'expert'}]->(ruby),\n" +
                        "       (lucy)-[:HAS_SKILL{level:'advanced'}]->(java),\n" +
                        "       (lucy)-[:HAS_SKILL{level:'expert'}]->(neo4j)";

        dbFixture = DatabaseFixture
                        .createDatabase()
                        .populateWith( cypher )
                        .noMigrations();
        valueNodes = new ValueNodes( dbFixture.database() );
    }

    @After
    public void teardown()
    {
        dbFixture.shutdown();
    }

    @Test
    public void shouldFindExpertsWhoShareSubjectsSkills() throws Exception
    {
        // when
        ResourceIterator<Map<String, Object>> results = valueNodes.findUsersWhoShareSkillsWith( "Ian", "expert" );
        
        // then
        Map<String, Object> result = results.next();
        assertEquals( "Lucy", result.get( "name" ) );
        assertEquals( "Neo4j", result.get( "skill" ) );
        assertFalse(results.hasNext());
    }
}
