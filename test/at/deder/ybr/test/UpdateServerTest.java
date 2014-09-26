/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.deder.ybr.test;

import at.deder.ybr.beans.RepositoryEntry;
import at.deder.ybr.beans.ServerManifest;
import at.deder.ybr.commands.ICliCommand;
import at.deder.ybr.commands.PrepareServer;
import at.deder.ybr.commands.UpdateServer;
import at.deder.ybr.structures.Tree;
import at.deder.ybr.test.mocks.MockFileSystemAccessor;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author lycis
 */
public class UpdateServerTest {

    private static MockFileSystemAccessor mockFSA = null;
    private UpdateServer cmd = null;

    /**
     * prepare all necessary objects
     */
    @Before
    public void initTest() {
        // mock for file system access
        mockFSA = new MockFileSystemAccessor();
        cmd = new UpdateServer();

        cmd.setFileSystemAccessor(mockFSA);
    }

    @Test
    public void testDefaultManifest() {
        // TODO implement
        // create folder structure to test
        executeCommand(new PrepareServer(), ".");
        
        // execute command
        executeCommand(new UpdateServer(), ".");
        
        // build expected repository structure
        RepositoryEntry rootEntry = new RepositoryEntry();
        rootEntry.setName("repository");
        RepositoryEntry comEntry = new RepositoryEntry();
        comEntry.setName("com");
        rootEntry.addChild(comEntry);
        RepositoryEntry orgEntry = new RepositoryEntry();
        comEntry.setName("org");
        rootEntry.addChild(orgEntry);
        
        // check if manifest is correct
        File manifest = mockFSA.getFile("/manifest.yml");
         ServerManifest sm = null;
        try {
            sm = ServerManifest.readYaml(new FileReader(manifest));
        } catch (FileNotFoundException ex) {
            Assert.fail("manifest file not found");
        }
        
        Assert.assertEquals("default repository is incorrectly created", 
                rootEntry, sm.getRepository());
    }
    
    @After
    public void cleanUp() {
        if (mockFSA != null) {
            mockFSA.destroy();
            mockFSA = null;
        }
    }

    @AfterClass
    public static void endCleanUp() {
        if (mockFSA != null) {
            mockFSA.destroy();
        }
    }
    
    private void executeCommand(ICliCommand cmd, String target) {
        ArrayList<String> cliData = new ArrayList<>();
        cliData.add(target);
        cmd.setData(cliData);
        cmd.setFileSystemAccessor(mockFSA);
        cmd.execute();
    }
}
