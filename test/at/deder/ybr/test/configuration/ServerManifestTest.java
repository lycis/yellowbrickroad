package at.deder.ybr.test.configuration;

import at.deder.ybr.configuration.ServerManifest;
import at.deder.ybr.repository.RepositoryEntry;
import at.deder.ybr.test.mocks.MockUtils;
import static org.assertj.core.api.BDDAssertions.then;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;

/**
 *
 * @author lycis
 */
public class ServerManifestTest {

    /**
     * Test of getType method, of class ServerManifest.
     */
    @Test
    public void testGetType() {
        ServerManifest instance = new ServerManifest();
        String expResult = "testType";
        instance.setType(expResult);
        String result = instance.getType();
        assertEquals(expResult, result);
    }

    /**
     * Test of getName method, of class ServerManifest.
     */
    @Test
    public void testGetName() {
        ServerManifest instance = new ServerManifest();
        String expResult = "testName";
        instance.setName(expResult);
        String result = instance.getName();
        assertEquals(expResult, result);
    }

    /**
     * Test of getAdmin method, of class ServerManifest.
     */
    @Test
    public void testGetAdmin() {
        ServerManifest instance = new ServerManifest();
        String expResult = "admin@example.com";
        instance.setAdmin(expResult);
        String result = instance.getAdmin();
        assertEquals(expResult, result);
    }

    /**
     * Test of getRepository method, of class ServerManifest.
     */
    @Test
    public void testGetRepository() {
        ServerManifest instance = new ServerManifest();
        RepositoryEntry expResult = MockUtils.generateComplexRepository();
        instance.setRepository(expResult);
        RepositoryEntry result = instance.getRepository();
        assertEquals(expResult, result);
    }
    
    /**
     * Test of initDefaults method, of class ServerManifest.
     */
    @Test
    public void testInitDefaults() {
        // when
        ServerManifest sm = new ServerManifest();
        sm.initDefaults();
        
        then(sm.getType()).isEqualTo("simple");
        then(sm.getName()).isEqualTo("yellow-brick-road");
        then(sm.getAdmin()).isEqualTo("admin@example.com");
    }

    /**
     * Test of equals method, of class ServerManifest.
     */
    @Test
    public void testEqualsSelf() {
        System.out.println("equals");
        ServerManifest instance = new ServerManifest();
        assertEquals(instance, instance);
    }    
}
