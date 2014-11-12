package at.deder.ybr.repository;

import at.deder.ybr.server.IServerGateway;
import at.deder.ybr.configuration.ServerManifest;
import at.deder.ybr.server.ProtocolViolationException;
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

    private IServerGateway serverGateway = null;

    public Repository(IServerGateway serverGateway) {
        this.serverGateway = serverGateway;
    }

    /**
     * Searches for a package entry in the repository with the given name.
     *
     * @param name name of the package in DNS-like anootation (e.g. .org.junit)
     * @return
     */
    public RepositoryEntry getPackage(String name) throws ProtocolViolationException {
        if (name.startsWith(".")) {
            name = name.substring(1);
        }
        
        // get repository information from manifest
        ServerManifest manifest = serverGateway.getManifest();
        if(manifest == null)
            return null;

        return getPackageRecursion(manifest.getRepository(), name);
    }

    /**
     * Recursively walk through the repository tree and resolve a given path.
     *
     * @param root
     * @param name
     * @return null if not found
     */
    private RepositoryEntry getPackageRecursion(RepositoryEntry root, String name) {
        if(root == null) {
            return null;
        }
        
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
    
    // TODO get files for package

}
