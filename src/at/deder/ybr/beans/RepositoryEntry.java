package at.deder.ybr.beans;

import at.deder.ybr.structures.Tree;
import java.util.HashMap;
import java.util.Map;

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

    public Map fold() {
        Map m = new HashMap();

        if (name != null && !name.isEmpty()) {
            m.put("name", name);
        }

        if (description != null && !description.isEmpty()) {
            m.put("description", description);
        }

        getChildren().stream().map((Tree t) -> (RepositoryEntry) t)
                .forEach((child) -> {
                    m.put(child.getName(), child.fold());
                });

        return m;
    }
}
