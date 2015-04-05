package at.deder.ybr.test.server;

import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.Mockito.mock;
import static com.googlecode.catchexception.CatchException.caughtException;
import static com.googlecode.catchexception.apis.BDDCatchException.when;

import org.junit.Assert;
import org.junit.Test;

import at.deder.ybr.configuration.ClientConfiguration;
import at.deder.ybr.configuration.InvalidConfigurationException;
import at.deder.ybr.server.IServerGateway;
import at.deder.ybr.server.ServerFactory;
import at.deder.ybr.server.SimpleHttpServer;
import at.deder.ybr.server.UnknownServerTypeException;

public class ServerFactoryTest {

	@Test
	public void testCreateServerSimpleWithPort() throws UnknownServerTypeException, InvalidConfigurationException {
		ClientConfiguration clientConf = mock(ClientConfiguration.class);
		given(clientConf.getType()).willReturn(ServerFactory.TYPE_SIMPLE);
		given(clientConf.getServerAddress()).willReturn("localhost:80");

		IServerGateway gw = ServerFactory.createServer(clientConf);
		then(gw).isInstanceOf(SimpleHttpServer.class);
	}

	@Test
	public void testUnknownServerType() throws InvalidConfigurationException {
		ClientConfiguration clientConf = mock(ClientConfiguration.class);
		given(clientConf.getType()).willReturn("unknown server type");

		boolean exOc = false;
		try {
			IServerGateway gw = ServerFactory.createServer(clientConf);
		} catch (UnknownServerTypeException e) {
			exOc = true;
		} 
		Assert.assertTrue("expected exception did not occur", exOc);
	}
	
	@Test
	public void testCreateSimpleServerWithoutPort() throws UnknownServerTypeException, InvalidConfigurationException {
		ClientConfiguration clientConf = mock(ClientConfiguration.class);
		given(clientConf.getType()).willReturn(ServerFactory.TYPE_SIMPLE);
		given(clientConf.getServerAddress()).willReturn("localhost");

		IServerGateway gw = ServerFactory.createServer(clientConf);
		then(gw).isInstanceOf(SimpleHttpServer.class);
	}
}
