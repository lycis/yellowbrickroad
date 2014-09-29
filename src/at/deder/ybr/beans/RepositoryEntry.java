package at.deder.ybr.beans;

import at.deder.ybr.structures.Tree;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * This bean represents one entry in the repository.
 *
 * @author lycis
 */
public class RepositoryEntry extends Tree {

    private String name = "";
    private String description = "";

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Fold this entry and all subentries into a Map.
     * @return 
     */
    public Map fold() {
        Map m = new HashMap();

        // write node information
        Map<String, String> nodeInformation = new HashMap<>();
        if (name != null && !name.isEmpty()) {
            nodeInformation.put("name", name);
        }

        if (description != null && !description.isEmpty()) {
            nodeInformation.put("description", description);
        }
        
        m.put("nodeInformation", nodeInformation);

        getChildren().stream().map((Tree t) -> (RepositoryEntry) t)
                .forEach((child) -> {
                    m.put(child.getName(), child.fold());
                });

        return m;
    }
    
    /**
     * Create a tree of entries baed on a Map.
     * @param m
     * @return 
     */
    public static RepositoryEntry unfold(Map m){
        RepositoryEntry entry = new RepositoryEntry();
        
        m.forEach((k, v) -> {
            if(!k.equals("nodeInformation") && v instanceof Map) {
                entry.addChild(RepositoryEntry.unfold((Map) v));
            }
        });
        
        // process nodeInformation
        Map<String, String> nodeInformation = (Map<String,String>) m.get("nodeInformation");
        
        if(nodeInformation.containsKey("name")) {
            entry.setName(nodeInformation.get("name"));
        }
        
        if(nodeInformation.containsKey("description")) {
            entry.setName(nodeInformation.get("description"));
        }
        
        return entry;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + Objects.hashCode(this.children);
        hash = 7 * hash + Objects.hashCode(this.name);
        hash = 7 * hash + Objects.hashCode(this.description);
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
        final RepositoryEntry other = (RepositoryEntry) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.description, other.description)) {
            return false;
        }
        if (!Objects.equals(this.children, other.children)) {
            return false;
        }
        return true;
    }
    
    
}
