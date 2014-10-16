package at.deder.ybr.server;

import at.deder.ybr.configuration.ClientConfiguration;

/**
 * Generate a server according to the configuration and the server response.
 * You do not need to worry about the specifics of the server implementation when
 * using this class as it will always return IServerGateways to use.
 * 
 * Currently only generates SimpleHTTPServers and has to be extended for new
 * server types.
 * 
 * @author lycis
 */
public class ServerFactory {
    
    private static IServerGateway injectedServer = null;
    
    // hide constructor
    private ServerFactory() {};
    
    /**
     * Return an interface to the correct server implementation based on the
     * given configuration and server identification.
     * @param config
     * @return 
     */
    public static IServerGateway createServer(ClientConfiguration config) {
        if(injectedServer != null) {
            return injectedServer;
        }
        
       return createSimpleServer(config);
    }
    
    /**
     * Instantiates a simple server from the given configuration.
     * @param config
     * @return 
     */
    private static IServerGateway createSimpleServer(ClientConfiguration config) {
        String serverAddress = config.getServerAddress();
        int    port          = 80;
        if(serverAddress.contains(":")) {
            String[] parts = serverAddress.split(":");
            if(parts.length != 2) {
                serverAddress = parts[0];
            }else {
                serverAddress = parts[0];
                port          = Integer.parseInt(parts[1]);
            }
        }
        
        SimpleHTTPServer serverImpl = new SimpleHTTPServer(serverAddress, port);
        return serverImpl;
    }
    
    /**
     * This method is for testing purposes only. It allows the injection of a 
     * server gateway that will always be returned by the createServer(...) method.
     * 
     * It can be used to inject mocks when testing.
     * 
     * @param server 
     */
    public static void injectServer(IServerGateway server) {
        injectedServer = server;
    }
}
