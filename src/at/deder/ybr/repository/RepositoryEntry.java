package at.deder.ybr.repository;

import at.deder.ybr.structures.Tree;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * This bean represents one entry in the repository.
 *
 * @author lycis
 */
public class RepositoryEntry extends Tree {

    private String name = "";
    private String description = "";
    private PackageHash packageHash = null;

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
    
    public PackageHash getPackageHash() {
        return packageHash;
    }
    
    public void setPackageHash(PackageHash hash) {
        this.packageHash = hash;
    }

    /**
     * Returns the path from the root note to this node.
     *
     * @return
     */
    public String getAbsolutePath() {
        return buildAbsolutePath("");

    }

    private String buildAbsolutePath(String path) {
        if (parent != null) {
            return ((RepositoryEntry) parent).buildAbsolutePath("/" + getName() + path);
        } else {
            return "/" + getName() + path;
        }
    }

    /**
     * Fold this entry and all subentries into a Map.
     *
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
        
        if(packageHash != null) {
            nodeInformation.put("hash", packageHash.toString());
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
     *
     * @param m
     * @return
     */
    public static RepositoryEntry unfold(Map m) {
        RepositoryEntry entry = new RepositoryEntry();

        m.forEach((k, v) -> {
            if (!k.equals("nodeInformation") && v instanceof Map) {
                entry.addChild(RepositoryEntry.unfold((Map) v));
            }
        });

        // process nodeInformation
        Map<String, String> nodeInformation = (Map<String, String>) m.get("nodeInformation");

        if (nodeInformation.containsKey("name")) {
            entry.setName(nodeInformation.get("name"));
        }

        if (nodeInformation.containsKey("description")) {
            entry.setDescription(nodeInformation.get("description"));
        }
        
        if(nodeInformation.containsKey("hash")) {
            entry.setPackageHash(new PackageHash(nodeInformation.get("hash")));
        }

        return entry;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + Objects.hashCode(this.children);
        hash = 7 * hash + Objects.hashCode(this.name);
        hash = 7 * hash + Objects.hashCode(this.description);
        hash = 4 * hash + Objects.hashCode(this.packageHash);
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
        if (!Objects.equals(this.packageHash, other.packageHash)) {
            return false;
        }
        
        if (!this.children.stream().map((mT) -> (RepositoryEntry) mT).map((myChild) -> {
            RepositoryEntry otherChild = null;
            for(Tree oT: other.children) {
                if(myChild.equals(((RepositoryEntry) oT))) {
                    otherChild = (RepositoryEntry) oT;
                }
            }
            return otherChild;
        }).noneMatch((otherChild) -> (otherChild == null))) {
            return false;
        }
        
        /*if (!Objects.equals(this.children, other.children)) {
            return false;
        }*/
        return true;
    }

    public RepositoryEntry getChildByName(String name) {
        if (name == null) {
            return null;
        }

        RepositoryEntry result;
        try {
            result = (RepositoryEntry) children.stream()
                    .filter(e -> name.equals(((RepositoryEntry) e).getName()))
                    .findFirst()
                    .get();
        } catch (NoSuchElementException ex) {
            result = null;
        }
        return result;
    }
    
}
