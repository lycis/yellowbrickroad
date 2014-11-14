package at.deder.ybr.test.commands;

import at.deder.ybr.channels.OutputChannelFactory;
import at.deder.ybr.commands.Describe;
import at.deder.ybr.configuration.ClientConfiguration;
import at.deder.ybr.filesystem.FileSystem;
import at.deder.ybr.server.IServerGateway;
import at.deder.ybr.server.ProtocolViolationException;
import at.deder.ybr.server.ServerFactory;
import at.deder.ybr.server.SimpleHttpServer;
import at.deder.ybr.test.mocks.CheckableSilentOutputChannel;
import at.deder.ybr.test.mocks.MockFileSystemAccessor;
import at.deder.ybr.test.mocks.MockUtils;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import static org.assertj.core.api.BDDAssertions.then;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
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
        mockFSA = spy(new MockFileSystemAccessor());
        mockOut = new CheckableSilentOutputChannel();
        cmd = new Describe();

        FileSystem.injectAccessor(mockFSA);
        OutputChannelFactory.setOutputChannel(mockOut);
    }
    
    @After
    public void cleanUp() {
        ServerFactory.injectServer(null); // reset injected server
        FileSystem.injectAccessor(null);
    }

    @AfterClass
    public static void endCleanUp() {
        FileSystem.injectAccessor(null);
    }

    /**
     * Basic test for the execute method with all happy :)
     */
    @Test
    public void execute() throws ProtocolViolationException, IOException{
         // given
        SimpleHttpServer spyServer  = spy(new SimpleHttpServer("none"));
        willReturn(MockUtils.getMockManifest()).given(spyServer).getManifest();
        ServerFactory.injectServer(spyServer);
        
        File configFile = mockFSA.createFile(mockFSA.getWorkingDirectory(), "ybr-config.yml", false);
        BufferedWriter writer = new BufferedWriter(new FileWriter(configFile));
        writer.write("!" + ClientConfiguration.YAML_TAG + "\n");
        writer.close();
        
        ArrayList<String> parameters = new ArrayList<>();
        parameters.add(".com.cpp.util.x32");
        cmd.setData(parameters);
        
        // when
        cmd.execute();
        
        // then
        then(mockOut.getError()).isEmpty();
        then(mockOut.getOutput()).isEqualTo(".com.cpp.util.x32\nc++ utilities for 32bit architecture\n");
    }
    
    @Test
    public void test_execute_without_leading_dot() throws ProtocolViolationException, IOException{
        // given
        SimpleHttpServer spyServer  = spy(new SimpleHttpServer("none"));
        willReturn(MockUtils.getMockManifest()).given(spyServer).getManifest();
        ServerFactory.injectServer(spyServer);
        
        File configFile = mockFSA.createFile(mockFSA.getWorkingDirectory(), "ybr-config.yml", false);
        BufferedWriter writer = new BufferedWriter(new FileWriter(configFile));
        writer.write("!" + ClientConfiguration.YAML_TAG + "\n");
        writer.close();
        
        ArrayList<String> parameters = new ArrayList<>();
        parameters.add("org.junit");
        cmd.setData(parameters);
        
        // when
        cmd.execute();
        
        // then
        assertTrue("output log does not match expectation", 
                mockOut.outputEquals(".org.junit\nJUnit unit test suite\n"));
        assertTrue("error log does not match expectation",
                mockOut.errorEquals(""));
    }
    
    /**
     * Check if describing a not existing package works correctly.
     */
    @Test
    public void testExecuteNotExistingPackage() throws ProtocolViolationException, IOException {
        // prepare mocks
        IServerGateway mockServer = mock(IServerGateway.class);
        when(mockServer.getManifest()).thenReturn(MockUtils.getMockManifest());
        ServerFactory.injectServer(mockServer);
        
        File configFile = mockFSA.createFile(mockFSA.getWorkingDirectory(), "ybr-config.yml", false);
        BufferedWriter writer = new BufferedWriter(new FileWriter(configFile));
        writer.write("!" + ClientConfiguration.YAML_TAG + "\n");
        writer.close();
        
        // execute command
        ArrayList<String> parameters = new ArrayList<>();
        parameters.add("does.not.exist");
        cmd.setData(parameters);
        cmd.execute();
        
        assertTrue("output log does not match expectation", 
                mockOut.outputEquals(".does.not.exist\n<not found>\n"));
        assertTrue("error log does not match expectation",
                mockOut.errorEquals(""));
    }
}
