package at.deder.ybr.test.cukes;

import at.deder.ybr.repository.RepositoryEntry;
import at.deder.ybr.server.ProtocolViolationException;
import at.deder.ybr.server.ServerFactory;
import at.deder.ybr.server.SimpleHttpServer;
import at.deder.ybr.test.mocks.MockUtils;
import cucumber.api.java.After;
import cucumber.api.java.en.Given;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willReturn;
import org.mockito.Matchers;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

/**
 * These steps handle everything regarding the mocking of remote server
 * behaviour.
 *
 * @author lycis
 */
public class ServerMocking {

    SimpleHttpServer mockGateway = null;

    @Given("^the remote server is a mocked simple server$")
    public void the_remote_server_is_a_mocked_simple_server() {
        mockGateway = spy(new SimpleHttpServer("none"));
        ServerFactory.injectServer(mockGateway);
    }

    @Given("^the remote server returns a mocked manifest$")
    public void the_remote_server_returns_a_mocked_manifest() throws ProtocolViolationException {
        willReturn(MockUtils.getMockManifest()).given(mockGateway).getManifest();
    }

    @After
    public void clean_up_after_scenario() {
        ServerFactory.injectServer(null);
    }

    @Given("^the remote server is a mock of a complete simple server$")
    public void the_remote_server_is_a_mock_of_a_complete_simple_server() throws ProtocolViolationException, IOException {
        the_remote_server_is_a_mocked_simple_server();
        the_remote_server_returns_a_mocked_manifest();

        // redirect server to internal instance so we can intercept all calls
        HttpClient mockHttpClient = mock(HttpClient.class);
        HttpServerSimulator simulator = new HttpServerSimulator();
        provideGeneratedResource(simulator, MockUtils.getMockManifest().getRepository(), "/");
        given(mockHttpClient.execute(Matchers.any(HttpGet.class))).willAnswer(simulator);
        mockGateway.setHttpClient(mockHttpClient);
    }

    private void provideGeneratedResource(HttpServerSimulator simulator, RepositoryEntry currentEntry, String currentPath) {
        if (!currentEntry.getDescription().isEmpty()) { // description file
            simulator.addResource(currentPath + currentEntry.getName(), "description", ContentType.TEXT_HTML, currentEntry.getDescription());
        }

        if (currentEntry.isLeaf()) { // data only for packages
            simulator.addResource(currentPath + currentEntry.getName(), "index", ContentType.TEXT_HTML,
                    currentEntry.getName() + ".txt\n" + currentEntry.getName() + ".bin");
            simulator.addResource(currentPath + currentEntry.getName(), currentEntry.getName() + ".txt", ContentType.TEXT_HTML,
                    "text data for " + currentEntry.getName());

            try {
                simulator.addResource(currentPath + currentEntry.getName(), currentEntry.getName() + ".bin", HttpStatus.SC_OK, "OK",
                        ContentType.APPLICATION_OCTET_STREAM, currentEntry.getName().getBytes("UTF-8"));
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(ServerMocking.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            currentEntry.getChildren().stream()
                    .map(t -> (RepositoryEntry) t)
                    .forEach((child) -> {
                        provideGeneratedResource(simulator, child, currentPath + currentEntry.getName()+"/");
                    });
        }
    }
}
