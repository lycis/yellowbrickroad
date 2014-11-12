package at.deder.ybr.test.mocks;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.BDDAssertions.then;

public class MockFileSystemAccessorTest {

    MockFileSystemAccessor mfsa = null;

    @Before
    public void prepare() {
        mfsa = new MockFileSystemAccessor();
    }

    @Test
    public void testCreate() {
        Assert.assertTrue("file not created", mfsa.createFile(mfsa.getFile("/"), "test-file", false) != null);
    }

    @Test
    public void testExists() {
        mfsa.createFile(mfsa.getFile("/"), "test-file", false);
        Assert.assertTrue("file does not exist", mfsa.exists("/test-file"));
    }

    @Test
    public void testGetFile() {
        mfsa.createFile(mfsa.getFile("/"), "test-file", false);
        mfsa.exists("/test-file");
        Assert.assertTrue("file is null", mfsa.getFile("/test-file") != null);
    }
    
    @Test
    public void dot_is_current_dir() {
        then(mfsa.getFile(".")).isEqualTo(mfsa.getRoot());
    }

    @After
    public void cleanUp() {
        mfsa.destroy();
    }
}
