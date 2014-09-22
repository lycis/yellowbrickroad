package at.deder.ybr.test;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import at.deder.ybr.commands.PrepareServer;
import at.deder.ybr.test.mocks.MockFileSystemAccessor;

public class PrepareServerTest {
	
	MockFileSystemAccessor mockFSA = null;
	PrepareServer          cmd     = null;
	
	/**
	 * prepare all necessary objects
	 */
	@Before
	public void initTest() {
		// mock for file system access
		mockFSA = new MockFileSystemAccessor();
		cmd     = new PrepareServer();
				
		cmd.setFileSystemAccessor(mockFSA);
	}

	/**
	 * Check if skeleton folder structure for yellow brick road server is created correctly.
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
	
	// TODO test for structure of manifest
	
	@After
	public void cleanUp() {
		mockFSA.destroy();
	}

}
