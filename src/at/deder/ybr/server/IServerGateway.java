package at.deder.ybr.server;

import at.deder.ybr.server.Banner;
import at.deder.ybr.configuration.ServerManifest;

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
    public ServerManifest getManifest();
    
    /**
     * Gives the welcome banner of the server as defined in index.html
     *     */
    public Banner getBanner();
}
