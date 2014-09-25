package at.deder.ybr.beans;

/**
 * This bean represents one entry in the repository.
 * @author lycis
 */
public class RepositoryEntry {
    private String name        = "";
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
    
    
}
