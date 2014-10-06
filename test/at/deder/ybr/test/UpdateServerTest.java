/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.deder.ybr.test;

import at.deder.ybr.channels.ConsoleOutputChannel;
import at.deder.ybr.beans.RepositoryEntry;
import at.deder.ybr.beans.ServerManifest;
import at.deder.ybr.commands.ICliCommand;
import at.deder.ybr.commands.PrepareServer;
import at.deder.ybr.commands.UpdateServer;
import at.deder.ybr.test.mocks.MockFileSystemAccessor;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author lycis
 */
public class UpdateServerTest {

    private static ConsoleOutputChannel  mockOut = null;
    private static MockFileSystemAccessor mockFSA = null;
    private UpdateServer cmd = null;

    /**
     * prepare all necessary objects
     */
    @Before
    public void initTest() {
        // mock for file system access
        mockFSA = new MockFileSystemAccessor();
        mockOut = new ConsoleOutputChannel();
        cmd = new UpdateServer();

        cmd.setFileSystemAccessor(mockFSA);
        cmd.setOutputAccessor(mockOut);
    }

    @Test
    public void testDefaultManifest() {
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
        orgEntry.setName("org");
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
    
     @Test
    public void testDescriptionManifest() {
        // create folder structure to test
        executeCommand(new PrepareServer(), ".");
        
        File comDir   = mockFSA.getFile("/repository/com");
        File comDescr = mockFSA.createFile(comDir, "description", false);
        try {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(comDescr))) {
                writer.write("commercial libraries");
            }
        } catch (IOException ex) {
            fail("exception: "+ex.getMessage());
        }
        
        File orgDir   = mockFSA.getFile("/repository/org");
        File orgDescr = mockFSA.createFile(orgDir, "description", false);
        try {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(orgDescr))) {
                writer.write("libraries by FOSS organisations");
            }
        } catch (IOException ex) {
            fail("exception: "+ex.getMessage());
        }
        
        
        // execute command
        executeCommand(new UpdateServer(), ".");
        
        // build expected repository structure
        RepositoryEntry rootEntry = new RepositoryEntry();
        rootEntry.setName("repository");
        RepositoryEntry comEntry = new RepositoryEntry();
        comEntry.setName("com");
        comEntry.setDescription("commercial libraries");
        rootEntry.addChild(comEntry);
        RepositoryEntry orgEntry = new RepositoryEntry();
        orgEntry.setName("org");
        orgEntry.setDescription("libraries by FOSS organisations");
        rootEntry.addChild(orgEntry);
        
        // check if manifest is correct
        File manifest = mockFSA.getFile("/manifest.yml");
        ServerManifest sm = null;
        try {
            sm = ServerManifest.readYaml(new FileReader(manifest));
        } catch (FileNotFoundException ex) {
            Assert.fail("manifest file not found");
        }
        
        Assert.assertEquals("descriptions in repository are not set correctly", 
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
        cmd.setOutputAccessor(mockOut);
        cmd.execute();
    }
}
