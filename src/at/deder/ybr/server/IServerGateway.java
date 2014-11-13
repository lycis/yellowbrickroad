package at.deder.ybr.server;

import at.deder.ybr.configuration.ServerManifest;
import at.deder.ybr.repository.RepositoryEntry;
import java.util.Map;

/**
 * This interface represents a gateway to a remote YBR server. The complete
 * implementation has to be designed stateless (or at least that is what this
 * interface aimes for). The actual protocol is hidden from a user of this
 * interface so that abstract access is possible.
 * 
 * @author lycis
 */
public interface IServerGateway {
    
    /**
     * Provides the manifest of the server.
     * @return manifest bean
     */
    public ServerManifest getManifest() throws ProtocolViolationException;
    
    /**
     * Gives the welcome banner of the server as defined in index.html
     *     */
    public Banner getBanner() throws ProtocolViolationException;
    
    public Map<String, byte[]> getFilesOfPackage(String pkgName) throws ProtocolViolationException;
    
     /**
     * Searches for a package entry in the repository with the given name.
     *
     * @param name name of the package in DNS-like anootation (e.g. .org.junit)
     * @return
     */
    public RepositoryEntry getPackage(String name) throws ProtocolViolationException;
}
