package at.deder.ybr.server;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import org.apache.http.client.utils.URIBuilder;

import at.deder.ybr.configuration.ClientConfiguration;
import at.deder.ybr.configuration.InvalidConfigurationException;

/**
 * Generate a server according to the configuration and the server response. You
 * do not need to worry about the specifics of the server implementation when
 * using this class as it will always return IServerGateways to use.
 * 
 * Currently only generates SimpleHTTPServers and has to be extended for new
 * server types.
 * 
 * @author lycis
 */
public class ServerFactory {

	private static IServerGateway injectedServer = null;

	public static final String TYPE_SIMPLE = "simple";
	public static final String TYPE_NEXUS = "nexus";

	// hide constructor
	private ServerFactory() {
	};

	/**
	 * Return an interface to the correct server implementation based on the
	 * given configuration and server identification.
	 * 
	 * @param config
	 * @return
	 */
	public static IServerGateway createServer(ClientConfiguration config)
			throws UnknownServerTypeException, InvalidConfigurationException {
		if (injectedServer != null) {
			return injectedServer;
		}

		if (ServerFactory.TYPE_SIMPLE.equalsIgnoreCase(config.getType())) {
			return createSimpleServer(config);
		} else if (ServerFactory.TYPE_NEXUS.equalsIgnoreCase(config.getType())) {
			return createNexusGateway(config);
		}

		throw new UnknownServerTypeException(config.getType());
	}

	/**
	 * Instantiates a server that provides access to a Nexus repository.
	 * 
	 * @param config
	 * @return
	 */
	private static IServerGateway createNexusGateway(ClientConfiguration config)
			throws InvalidConfigurationException {
		Map details = config.getServerDetails();
		if (details == null) {
			throw new InvalidConfigurationException("missing required details");
		}
		if (!details.containsKey("repository")) {
			throw new InvalidConfigurationException("missing required details");
		}

		String serverAddress = config.getServerAddress();
		URI parseUri = null;
		try {
			parseUri = new URIBuilder(serverAddress).build();
		} catch (URISyntaxException ex) {
			throw new InvalidConfigurationException(
					"malformed Nexus server address");
		}
		
		if(parseUri.getHost() == null) {
			throw new InvalidConfigurationException("malformed server address");
		}

		NexusServer serverImpl = new NexusServer(parseUri.getHost(), 
				                                 parseUri.getPath(),
				                                 parseUri.getPort(), 
				                                 (String) details.get("repository"));

		if (parseUri.getScheme() != null && !parseUri.getScheme().isEmpty()) {
			serverImpl.setScheme(parseUri.getScheme());
		}

		if (details.containsKey("trust")) {
			if ("all".equals(details.get("trust"))) {
				// trust every certificate
				serverImpl.setTrustEveryone(true);
			} else {
				serverImpl.setTrustEveryone(false);
			}
		} else {
			serverImpl.setTrustEveryone(false);
		}

		return serverImpl;
	}

	/**
	 * Instantiates a simple server from the given configuration.
	 * 
	 * @param config
	 * @return
	 */
	private static IServerGateway createSimpleServer(ClientConfiguration config) 
			throws InvalidConfigurationException {
		
		String serverAddress = config.getServerAddress();
		URI parseUri = null;
		try {
			parseUri = new URIBuilder(serverAddress).build();
		} catch (URISyntaxException ex) {
			throw new InvalidConfigurationException("malformed server address");
		}
		
		if(parseUri.getHost() == null) {
			throw new InvalidConfigurationException("malformed server address");
		}

		SimpleHttpServer serverImpl = new SimpleHttpServer(parseUri.getHost(), 
				                                           parseUri.getPath(),
				                                           parseUri.getPort());
		// set details
		Map details = config.getServerDetails();
		if (details != null) {
			if (details.containsKey("trust")) {
				if ("all".equals(details.get("trust"))) {
					// trust every certificate
					serverImpl.setTrustEveryone(true);
				}
			}
		}

		if (parseUri.getScheme() != null && !parseUri.getScheme().isEmpty()) {
			serverImpl.setScheme(parseUri.getScheme());
		}
		return serverImpl;
	}

	/**
	 * This method is for testing purposes only. It allows the injection of a
	 * server gateway that will always be returned by the createServer(...)
	 * method.
	 * 
	 * It can be used to inject mocks when testing.
	 * 
	 * @param server
	 */
	public static void injectServer(IServerGateway server) {
		injectedServer = server;
	}
}
