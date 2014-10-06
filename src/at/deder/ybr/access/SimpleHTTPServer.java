package at.deder.ybr.access;

import at.deder.ybr.beans.ServerManifest;

/**
 * This implementation of the IServerGateway interface gives access to a remote
 * server of type 'simple'. This means that the remote server is serving only
 * and does not execute any actions on its own.
 * 
 * @author lycis
 */
public class SimpleHTTPServer implements IServerGateway {

    @Override
    public ServerManifest getManifest() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
