package at.deder.ybr.test;

import at.deder.ybr.configuration.ClientConfiguration;
import java.io.Reader;
import java.io.Writer;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ederda
 */
public class ClientConfigurationTest {

    /**
     * Test of getServerAddress method, of class ClientConfiguration.
     */
    @Test
    public void testGetServerAddress() {
        ClientConfiguration instance = new ClientConfiguration();
        String expResult = "test.server:8080";
        instance.setServerAddress(expResult);
        String result = instance.getServerAddress();
        assertEquals(expResult, result);
    }

    /**
     * Test of writeYaml method, of class ClientConfiguration.
     */
    @Test
    public void testWriteYaml() {
        System.out.println("writeYaml");
        Writer w = null;
        ClientConfiguration instance = new ClientConfiguration();
        boolean expResult = false;
        boolean result = instance.writeYaml(w);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of readYaml method, of class ClientConfiguration.
     */
    @Test
    public void testReadYaml() {
        System.out.println("readYaml");
        Reader r = null;
        ClientConfiguration expResult = null;
        ClientConfiguration result = ClientConfiguration.readYaml(r);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
