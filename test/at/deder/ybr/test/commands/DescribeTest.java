package at.deder.ybr.test.commands;

import at.deder.ybr.commands.Describe;
import at.deder.ybr.server.IServerGateway;
import at.deder.ybr.server.ServerFactory;
import at.deder.ybr.test.mocks.CheckableSilentOutputChannel;
import at.deder.ybr.test.mocks.MockFileSystemAccessor;
import at.deder.ybr.test.mocks.MockUtils;
import java.util.ArrayList;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 * @author lycis
 */
public class DescribeTest {
    
    private static CheckableSilentOutputChannel  mockOut = null;
    private static MockFileSystemAccessor mockFSA = null;
    private Describe cmd = null;
    
     /**
     * prepare all necessary objects
     */
    @Before
    public void initTest() {
        // mock for file system access
        mockFSA = new MockFileSystemAccessor();
        mockOut = new CheckableSilentOutputChannel();
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
        when(mockServer.getManifest()).thenReturn(MockUtils.getMockManifest());
        ServerFactory.injectServer(mockServer);
        mockFSA.createFile(null, "ybr-config.yml", false);
        
        // execute command
        ArrayList<String> parameters = new ArrayList<>();
        parameters.add(".com.cpp.util.x32");
        cmd.setData(parameters);
        cmd.execute();
        
        assertTrue("output log does not match expectation", 
                mockOut.outputEquals(".com.cpp.util.x32\nc++ utilities for 32bit architecture\n"));
        assertTrue("error log does not match expectation",
                mockOut.errorEquals(""));
    }
    
    /**
     * Check if describing a not existing package works correctly.
     */
    @Test
    public void testExecuteNotExistingPackage() {
        // prepare mocks
        IServerGateway mockServer = mock(IServerGateway.class);
        when(mockServer.getManifest()).thenReturn(MockUtils.getMockManifest());
        ServerFactory.injectServer(mockServer);
        mockFSA.createFile(null, "ybr-config.yml", false);
        
        // execute command
        ArrayList<String> parameters = new ArrayList<>();
        parameters.add("does.not.exist");
        cmd.setData(parameters);
        cmd.execute();
        
        assertTrue("output log does not match expectation", 
                mockOut.outputEquals("does.not.exist\n<not found>\n"));
        assertTrue("error log does not match expectation",
                mockOut.errorEquals(""));
    }
}
