package at.deder.ybr.test.configuration;

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
    
}
