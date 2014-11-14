package at.deder.ybr.test.configuration;

import at.deder.ybr.configuration.ClientConfiguration;
import static org.assertj.core.api.BDDAssertions.then;
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
    
    @Test
    public void test_set_defaults() {
        ClientConfiguration instance = ClientConfiguration.getDefaultConfiguration();
        then(instance.getServerAddress()).isEqualTo("hostname:80");
        then(instance.getTargetPath()).isEqualTo("."); 
        then(instance.getPackages()).hasSize(1);
        then(instance.getPackages().get(0)).isEqualTo("some.package.here");
    }
    
}
