package at.deder.ybr.repository;

import at.deder.ybr.server.IServerGateway;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Grants access to the repository and provides all methods necessary to operate
 * on the repository.
 *
 * @author lycis
 */
public class Repository {

    private RepositoryEntry root = null;
    private IServerGateway serverGateway = null;

    public Repository(RepositoryEntry root, IServerGateway serverGateway) {
        this.root = root;
        this.serverGateway = serverGateway;
    }

    /**
     * Searches for a package entry in the repository with the given name.
     *
     * @param name name of the package in DNS-like anootation (e.g. .org.junit)
     * @return
     */
    public RepositoryEntry getPackage(String name) {
        if (name.startsWith(".")) {
            name = name.substring(1);
        }

        return getPackageRecursion(root, name);
    }

    /**
     * Recursively walk through the repository tree and resolve a given path.
     *
     * @param root
     * @param name
     * @return null if not found
     */
    private RepositoryEntry getPackageRecursion(RepositoryEntry root, String name) {
        List<String> path = Arrays.asList(name.split("\\."));
        if (path.size() < 1) {
            return null;
        }

        String target = path.get(0);
        RepositoryEntry nextEntry;
        try {
            nextEntry = (RepositoryEntry) root.getChildren().stream()
                    .filter(entry -> target.equals(((RepositoryEntry) entry).getName()))
                    .findFirst().get();
        } catch (NoSuchElementException e) {
            nextEntry = null;
        }

        if (!name.contains(".")) {
            return nextEntry;
        } else {
            String remainingPath = name.substring(name.indexOf(".") + 1);
            if (nextEntry != null && remainingPath.length() > 1) {
                return getPackageRecursion(nextEntry, remainingPath);
            }
        }
        return nextEntry;
    }

}
