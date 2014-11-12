package at.deder.ybr.configuration;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;
import com.esotericsoftware.yamlbeans.YamlWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

/**
 * This bean provides data of the local client configuration
 *
 * @author lyics
 */
public class ClientConfiguration {

    private static final String YAML_TAG = "client-configuration";

    private String serverAddress = "";

    public ClientConfiguration() {

    }

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public boolean writeYaml(Writer w) {
        YamlWriter writer = new YamlWriter(w);
        writer.getConfig().writeConfig.setAlwaysWriteClassname(false);
        writer.getConfig().setClassTag(YAML_TAG, ClientConfiguration.class);
        writer.getConfig().writeConfig.setWriteDefaultValues(false);

        try {
            writer.write(this);
            writer.close();
        } catch (YamlException ex) {
            return false;
        }

        return true;
    }

    public static ClientConfiguration readYaml(Reader r) {
        YamlReader reader = new YamlReader(r);
        reader.getConfig().setClassTag(YAML_TAG, ClientConfiguration.class);

        ClientConfiguration config;
        try {
            config = reader.read(ClientConfiguration.class);
        } catch (YamlException ex) {
            return null;
        }

        try {
            reader.close();
        } catch (IOException ex) {
            System.err.println("io ex: " + ex.getMessage());
        }

        return config;
    }
    
    /**
     * Set default values.
     */
    public static ClientConfiguration getDefaultConfiguration() {
        ClientConfiguration cc = new ClientConfiguration();
        cc.setServerAddress("hostname:80");
        return cc;
    }
    
    public String toString() {
        StringWriter sw = new StringWriter();
        writeYaml(sw);
        return sw.toString();
    }
}
