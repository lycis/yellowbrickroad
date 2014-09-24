package at.deder.ybr.test;

import at.deder.ybr.beans.ServerManifest;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import at.deder.ybr.commands.PrepareServer;
import at.deder.ybr.test.mocks.MockFileSystemAccessor;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import org.junit.AfterClass;

public class PrepareServerTest {

    private static MockFileSystemAccessor mockFSA = null;
    private PrepareServer cmd = null;

    /**
     * prepare all necessary objects
     */
    @Before
    public void initTest() {
        // mock for file system access
        mockFSA = new MockFileSystemAccessor();
        cmd = new PrepareServer();

        cmd.setFileSystemAccessor(mockFSA);
    }

    /**
     * Check if skeleton folder structure for yellow brick road server is
     * created correctly.
     */
    @Test
    public void testCreateFolderStructure() {

        // execute command
        ArrayList<String> cliData = new ArrayList<String>();
        cmd.setData(cliData);
        cmd.execute();

        // check folder structure
        Assert.assertTrue("repository not created", mockFSA.exists("/repository/"));
        Assert.assertTrue("company repository not created", mockFSA.exists("/repository/com/"));
        Assert.assertTrue("orgnisations repository not created", mockFSA.exists("/repository/org/"));
        Assert.assertTrue("manifest not created", mockFSA.exists("/manifest.yml"));
        Assert.assertTrue("index.html not created", mockFSA.exists("/index.html"));
    }

    /**
     * Check if content of the empty manifest is correct.
     */
    @Test
    public void testManifestContent() {
        // execute command
        ArrayList<String> cliData = new ArrayList<String>();
        cmd.setData(cliData);
        cmd.execute();

        // create default object for comparison
        ServerManifest defaultSm = new ServerManifest();
        defaultSm.initDefaults();

        // read created manifest
        File manifest = mockFSA.getFile("/manifest.yml");
        Assert.assertFalse("manifest was not written", manifest == null);

        ServerManifest sm = null;
        try {
            sm = ServerManifest.readYaml(new FileReader(manifest));
        } catch (FileNotFoundException ex) {
            Assert.fail("manifest file not found");
        }

        Assert.assertEquals("written default manifest is not correct",
                defaultSm, sm);
    }

    @Test
    public void testIndexHtml() {
        // execute command
        ArrayList<String> cliData = new ArrayList<String>();
        cmd.setData(cliData);
        cmd.execute();

        // read index.html
        File index = mockFSA.getFile("index.html");
        Assert.assertFalse("index.html not written", index == null);

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(index));
        } catch (FileNotFoundException ex) {
            Assert.fail("manifest file not found");
        }

        String content = "";
        try {
            String line = "";
            while ((line = reader.readLine()) != null) {
                content += line + "\n";
            }
        } catch (IOException ex) {
            Assert.fail("reading content of index.html failed");
        }finally{
            try{reader.close();}catch(IOException ex){};
        }
        
        Assert.assertEquals("default index.html contains wrong text", 
                PrepareServer.INDEX_DEFAULT_TEXT, content);
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

}
