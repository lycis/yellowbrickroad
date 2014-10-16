package at.deder.ybr.test;

import at.deder.ybr.channels.ConsoleOutputChannel;
import at.deder.ybr.commands.Describe;
import at.deder.ybr.server.IServerGateway;
import at.deder.ybr.server.ServerFactory;
import at.deder.ybr.test.mocks.MockFileSystemAccessor;
import at.deder.ybr.test.mocks.TestUtils;
import java.util.ArrayList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 * @author lycis
 */
public class DescribeTest {
    
    private static ConsoleOutputChannel  mockOut = null;
    private static MockFileSystemAccessor mockFSA = null;
    private Describe cmd = null;
    
     /**
     * prepare all necessary objects
     */
    @Before
    public void initTest() {
        // mock for file system access
        mockFSA = new MockFileSystemAccessor();
        mockOut = new ConsoleOutputChannel();
        cmd = new Describe();

        cmd.setFileSystemAccessor(mockFSA);
        cmd.setOutputAccessor(mockOut);
    }
    
    @After
    public void cleanUp() {
        if (mockFSA != null) {
            mockFSA.destroy();
            mockFSA = null;
        }
        ServerFactory.injectServer(null); // reset injected server
    }

    @AfterClass
    public static void endCleanUp() {
        if (mockFSA != null) {
            mockFSA.destroy();
        }
    }

    /**
     * Basic test for the execute method with all happy :)
     */
    @Test
    public void testExecute() {
        // prepare mocks
        IServerGateway mockServer = mock(IServerGateway.class);
        when(mockServer.getManifest()).thenReturn(TestUtils.getMockManifest());
        ServerFactory.injectServer(mockServer);
        mockFSA.createFile(null, "ybr-config.yml", false);
        
        // execute command
        ArrayList<String> parameters = new ArrayList<>();
        parameters.add(".com.cpp.util.x32");
        cmd.setData(parameters);
        cmd.execute();
        
        // TODO check if output is correct
    }
}
