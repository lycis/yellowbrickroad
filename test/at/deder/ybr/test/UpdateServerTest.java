/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.deder.ybr.test;

import at.deder.ybr.commands.UpdateServer;
import at.deder.ybr.test.mocks.MockFileSystemAccessor;
import org.junit.Assert;
import org.junit.After;
import org.junit.AfterClass;
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
    public void test() {
        // TODO implement
        // - create folder structure to test
        // - execute command
        // - check if manifest is correct
        Assert.fail("not yet implemented");
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
