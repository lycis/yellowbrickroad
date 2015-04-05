package at.deder.ybr.configuration;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

import at.deder.ybr.server.ServerFactory;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;
import com.esotericsoftware.yamlbeans.YamlWriter;

/**
 * This bean provides data of the local client configuration
 *
 * @author lyics
 */
public class ClientConfiguration {

    public static final String YAML_TAG = "ybr-client-configuration";

    private String             serverAddress = "";
    private String             targetPath    = "";
    private ArrayList<String>  packages;
    private String             type = "";
    private Map<String,String> serverDetails;

    public ClientConfiguration() {
        this.packages = new ArrayList<>();

    }
    
    public Map<String,String> getServerDetails() {
    	return serverDetails;
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public void setServerDetails(Map serverDetails) {
    	this.serverDetails = serverDetails;
    }
    
    public String getType() {
    	if(type == null) {
    		return ServerFactory.TYPE_SIMPLE;
    	}
    	
    	if(type.isEmpty()) {
    		return ServerFactory.TYPE_SIMPLE;
    	}
    	
    	return type;
    }
    
    public void setType(String type) {
    	this.type = type;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }
    
    public String getTargetPath() {
        return targetPath;
    }
    
    public void setTargetPath(String path) {
        targetPath = path;
    }
    
    public ArrayList<String> getPackages() {
        return packages;
    }
    
    public void addPackage(String pkgName) {
        if(!packages.contains(pkgName)) {
            packages.add(pkgName);
        }
    }
    
    public void setPackages(ArrayList<String> packages) {
        this.packages = packages;
    }
    
    public void removePackage(String pkgName) {
        if(packages.contains(pkgName)) {
            packages.remove(pkgName);
        }
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
        cc.setTargetPath(".");
        cc.addPackage("some.package.here");
        return cc;
    }
    
    public String toString() {
        StringWriter sw = new StringWriter();
        writeYaml(sw);
        return sw.toString();
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + Objects.hashCode(this.serverAddress);
        hash = 29 * hash + Objects.hashCode(this.targetPath);
        hash = 29 * hash + Objects.hashCode(this.packages);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ClientConfiguration other = (ClientConfiguration) obj;
        if (!Objects.equals(this.serverAddress, other.serverAddress)) {
            return false;
        }
        if (!Objects.equals(this.targetPath, other.targetPath)) {
            return false;
        }
        if (!Objects.equals(this.packages, other.packages)) {
            return false;
        }
        return true;
    }
    
    
}
