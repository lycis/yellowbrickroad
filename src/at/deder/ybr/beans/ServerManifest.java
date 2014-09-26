package at.deder.ybr.beans;

import at.deder.ybr.structures.Tree;
import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;
import com.esotericsoftware.yamlbeans.YamlWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.util.Objects;

// TODO implement repository structure

/**
 * This bean represents the YBR server manifest file.
 *
 * @author lycis
 */
public class ServerManifest implements Serializable {

    private String type = "";
    private String name = "";
    private String admin = "";
    
    private RepositoryEntry repository = null;
    
    private static final String YAML_TAG = "server-manifest";

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getAdmin() {
        return admin;
    }

    public RepositoryEntry getRepository() {
        return repository;
    }

    public void setType(String type) {
        if(type == null)
            type = "";
        this.type = type;
    }

    public void setName(String name) {
        if(name == null)
            name = "";
        this.name = name;
    }

    public void setAdmin(String admin) {
        if(admin == null)
            admin = "";
        this.admin = admin;
    }

    public void setRepository(RepositoryEntry repository) {
        this.repository = repository;
    }
    
    

    /**
     * Write a YAML file to the given writer. Used for serialisation of the
     * manifest.
     * @param w target writer
     * @return <code>true</code> if no error occurred
     */
    public boolean writeYaml(Writer w) {
        YamlWriter writer = new YamlWriter(w);
        writer.getConfig().writeConfig.setAlwaysWriteClassname(false);
        writer.getConfig().setClassTag(YAML_TAG, ServerManifest.class);

        try {
            writer.write(this);
            writer.close();
        } catch (YamlException ex) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Deserialises a server manifest from a given reader that reads a YAML
     * file.
     * 
     * @param r reader that points to the YAML file
     * @return instance with properties set according to the file
     */
    public static ServerManifest readYaml(Reader r) {
        YamlReader reader = new YamlReader(r);
        reader.getConfig().setClassTag(YAML_TAG, ServerManifest.class);
        
        ServerManifest manifest;
        try{
           manifest = reader.read(ServerManifest.class);
        } catch (YamlException ex) {
            return null;
        }
        
        try{
            reader.close();
        } catch (IOException ex) {
            // TODO error handling
        }
        
        return manifest;
    }
    
    /**
     * sets all values to their expected defaults
     */
    public void initDefaults() {
        type = "simple";
        name = "yellow-brick-road";
        admin  = "admin@example.com";
    }
    
    @Override
    public boolean equals(Object o) {
        if(o instanceof ServerManifest) {
            ServerManifest other = (ServerManifest) o;
            if(this.admin.equals(other.admin) &&
               this.name.equals(other.name) &&
               this.type.equals(other.type)) {
                return true;
            }
        }
        
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 83 * hash + Objects.hashCode(this.type);
        hash = 83 * hash + Objects.hashCode(this.name);
        hash = 83 * hash + Objects.hashCode(this.admin);
        return hash;
    }
}
