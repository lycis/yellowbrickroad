/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.deder.ybr.test;

import at.deder.ybr.beans.RepositoryEntry;
import java.util.HashMap;
import java.util.Map;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author lycis
 */
public class RepositoryEntryTest {
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getName method, of class RepositoryEntry.
     */
    @Test
    public void testGetName() {
        RepositoryEntry instance = new RepositoryEntry();
        String expResult = "test-entry-name";
        instance.setName(expResult);
        String result = instance.getName();
        assertEquals(expResult, result);
    }

    /**
     * Test of getDescription method, of class RepositoryEntry.
     */
    @Test
    public void testGetDescription() {
        RepositoryEntry instance = new RepositoryEntry();
        String expResult = "test-entry-description";
        instance.setDescription(expResult);
        String result = instance.getDescription();
        assertEquals(expResult, result);
    }
  
    /**
     * Test of fold method, of class RepositoryEntry.
     */
    @Test
    public void testFold() {
        // build entry tree
        RepositoryEntry repository = new RepositoryEntry();
        repository.setName("repository");
        RepositoryEntry com = new RepositoryEntry();
        com.setName("com");
        RepositoryEntry org = new RepositoryEntry();
        org.setName("org");
        
        repository.addChild(com);
        repository.addChild(org);
        
        // build expected result
        Map expResult = new HashMap();
        Map<String, String> repositoryNodeInfo = new HashMap<>();
        repositoryNodeInfo.put("name", "repository");
        expResult.put("nodeInformation", repositoryNodeInfo);
        
        Map<String, String> comNodeInfo = new HashMap<>();
        comNodeInfo.put("name", "com");
        Map comMap = new HashMap();
        comMap.put("nodeInformation", comNodeInfo);
        expResult.put("com", comMap);
        
        Map<String, String> orgNodeInfo = new HashMap<>();
        orgNodeInfo.put("name", "org");
        Map orgMap = new HashMap();
        orgMap.put("nodeInformation", orgNodeInfo);
        expResult.put("org", orgMap);
        
        
        Map result = repository.fold();
        assertEquals(expResult, result);
    }

    /**
     * Test of unfold method, of class RepositoryEntry.
     */
    @Test
    public void testUnfold() {
        // build map for 
        Map unfoldMap = new HashMap();
        Map<String, String> repositoryNodeInfo = new HashMap<>();
        repositoryNodeInfo.put("name", "repository");
        unfoldMap.put("nodeInformation", repositoryNodeInfo);
        
        Map<String, String> comNodeInfo = new HashMap<>();
        comNodeInfo.put("name", "com");
        Map comMap = new HashMap();
        comMap.put("nodeInformation", comNodeInfo);
        unfoldMap.put("com", comMap);
        
        Map<String, String> orgNodeInfo = new HashMap<>();
        orgNodeInfo.put("name", "org");
        Map orgMap = new HashMap();
        orgMap.put("nodeInformation", orgNodeInfo);
        unfoldMap.put("org", orgMap);
        
        // build entry tree
        RepositoryEntry repository = new RepositoryEntry();
        repository.setName("repository");
        RepositoryEntry com = new RepositoryEntry();
        com.setName("com");
        RepositoryEntry org = new RepositoryEntry();
        org.setName("org");
        
        repository.addChild(com);
        repository.addChild(org);
        
        // unfold
        RepositoryEntry checkNode = RepositoryEntry.unfold(unfoldMap);
        
        // check
        assertTrue(repository.equals(checkNode));
    }
    
    @Test
    public void testEqualsSelf() {
        RepositoryEntry entry = new RepositoryEntry();
        entry.setName("entry");
        assertTrue(entry.equals(entry));
    }
    
    @Test
    public void testEqualsNull() {
        RepositoryEntry entry = new RepositoryEntry();
        entry.setName("entry");
        assertFalse(entry.equals(null));
    }
    
    @Test
    public void testEqualsTrue() {
        RepositoryEntry entry = new RepositoryEntry();
        entry.setName("entry");
        entry.setDescription("foo");
        
        RepositoryEntry other = new RepositoryEntry();
        other.setName("entry");
        other.setDescription("foo");
        assertTrue(entry.equals(other));
    }
    
    @Test
    public void testEqualsFalseName() {
        RepositoryEntry entry = new RepositoryEntry();
        entry.setName("entry");
        entry.setDescription("foo");
        
        RepositoryEntry other = new RepositoryEntry();
        other.setName("another entry");
        other.setDescription("foo");
        assertFalse(entry.equals(other));
    }
    
    @Test
    public void testEqualsFalseDescription() {
        RepositoryEntry entry = new RepositoryEntry();
        entry.setName("entry");
        entry.setDescription("foo");
        
        RepositoryEntry other = new RepositoryEntry();
        other.setName("entry");
        other.setDescription("bar");
        assertFalse(entry.equals(other));
    }
    
    @Test
    public void testEqualsFalseNameAndDescription() {
        RepositoryEntry entry = new RepositoryEntry();
        entry.setName("entry");
        entry.setDescription("foo");
        
        RepositoryEntry other = new RepositoryEntry();
        other.setName("another entry");
        other.setDescription("bar");
        assertFalse(entry.equals(other));
    }

    @Test
    public void testEqualsTrueWithChildren() {
       // build entry tree A
        RepositoryEntry repositoryA = new RepositoryEntry();
        repositoryA.setName("repository");
        RepositoryEntry comA = new RepositoryEntry();
        comA.setName("com");
        RepositoryEntry orgA = new RepositoryEntry();
        orgA.setName("org");
        
        repositoryA.addChild(comA);
        repositoryA.addChild(orgA);
        
        // build entry tree B
        RepositoryEntry repositoryB = new RepositoryEntry();
        repositoryB.setName("repository");
        RepositoryEntry comB = new RepositoryEntry();
        comB.setName("com");
        RepositoryEntry orgB = new RepositoryEntry();
        orgB.setName("org");
        
        repositoryB.addChild(comB);
        repositoryB.addChild(orgB);
        
        // test
        assertTrue(repositoryA.equals(repositoryB));
    }
    
    @Test
    public void testEqualsFalseNameWithChildren() {
       // build entry tree A
        RepositoryEntry repositoryA = new RepositoryEntry();
        repositoryA.setName("repository");
        RepositoryEntry comA = new RepositoryEntry();
        comA.setName("com");
        RepositoryEntry orgA = new RepositoryEntry();
        orgA.setName("org");
        
        repositoryA.addChild(comA);
        repositoryA.addChild(orgA);
        
        // build entry tree B
        RepositoryEntry repositoryB = new RepositoryEntry();
        repositoryB.setName("repository");
        RepositoryEntry comB = new RepositoryEntry();
        comB.setName("other com");
        RepositoryEntry orgB = new RepositoryEntry();
        orgB.setName("org");
        
        repositoryB.addChild(comB);
        repositoryB.addChild(orgB);
        
        // test
        assertFalse(repositoryA.equals(repositoryB));
    }
    
    @Test
    public void testEqualsFalseDescriptionWithChildren() {
       // build entry tree A
        RepositoryEntry repositoryA = new RepositoryEntry();
        repositoryA.setName("repository");
        RepositoryEntry comA = new RepositoryEntry();
        comA.setName("com");
        RepositoryEntry orgA = new RepositoryEntry();
        orgA.setName("org");
        
        repositoryA.addChild(comA);
        repositoryA.addChild(orgA);
        
        // build entry tree B
        RepositoryEntry repositoryB = new RepositoryEntry();
        repositoryB.setName("repository");
        RepositoryEntry comB = new RepositoryEntry();
        comB.setName("com");
        comB.setDescription("error here");
        RepositoryEntry orgB = new RepositoryEntry();
        orgB.setName("org");
        
        repositoryB.addChild(comB);
        repositoryB.addChild(orgB);
        
        // test
        assertFalse(repositoryA.equals(repositoryB));
    }
    
    @Test
    public void testEqualsWrongStructure() {
       // build entry tree A
        RepositoryEntry repositoryA = new RepositoryEntry();
        repositoryA.setName("repository");
        RepositoryEntry comA = new RepositoryEntry();
        comA.setName("com");
        RepositoryEntry orgA = new RepositoryEntry();
        orgA.setName("org");
        
        repositoryA.addChild(comA);
        repositoryA.addChild(orgA);
        
        // build entry tree B
        RepositoryEntry repositoryB = new RepositoryEntry();
        repositoryB.setName("repository");
        RepositoryEntry comB = new RepositoryEntry();
        comB.setName("com");
        RepositoryEntry orgB = new RepositoryEntry();
        orgB.setName("org");
        
        repositoryB.addChild(comB);
        orgB.addChild(repositoryB);
        
        // test
        assertFalse(repositoryA.equals(repositoryB));
    }
    
}
