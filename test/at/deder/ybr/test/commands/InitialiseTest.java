package at.deder.ybr.test.commands;

import at.deder.ybr.Constants;
import at.deder.ybr.commands.Initialise;
import at.deder.ybr.configuration.ClientConfiguration;
import at.deder.ybr.test.mocks.CheckableSilentOutputChannel;
import at.deder.ybr.test.mocks.MockFileSystemAccessor;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import static org.assertj.core.api.BDDAssertions.then;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

/**
 *
 * @author lycis
 */
public class InitialiseTest {

    private static CheckableSilentOutputChannel mockOut = null;
    private static MockFileSystemAccessor mockFSA = null;
    private Initialise cmd = null;

    /**
     * prepare all necessary objects
     */
    @Before
    public void initTest() {
        // mock for file system access
        mockFSA = new MockFileSystemAccessor();
        mockOut = new CheckableSilentOutputChannel();
        cmd = new Initialise();

        cmd.setFileSystemAccessor(mockFSA);
        cmd.setOutputAccessor(mockOut);
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

    @Test
    public void initialise_without_args() {
        // given
        cmd.setData(new ArrayList<String>());

        // when
        cmd.execute();

        // then
        then(mockFSA.getFile(Constants.CLIENT_CONFIG_FILE)).exists();
        then(mockFSA.getFile(Constants.CLIENT_CONFIG_FILE)).hasContent(ClientConfiguration.getDefaultConfiguration().toString());
    }

    @Test
    public void initialise_in_workdir() {
        // given
        ArrayList<String> data = new ArrayList<>();
        data.add(".");
        cmd.setData(data);

        // when
        cmd.execute();

        // then
        then(mockFSA.getFile(Constants.CLIENT_CONFIG_FILE)).exists();
        then(mockFSA.getFile(Constants.CLIENT_CONFIG_FILE)).hasContent(ClientConfiguration.getDefaultConfiguration().toString());
    }
    
   @Test
    public void initialise_in_specific_dir() {
        // given
        mockFSA.createFile(mockFSA.getRoot(), "subdir", true);
        File subsubdir = mockFSA.createFile(mockFSA.getFile("subdir"), "subsubdir", true);
        ArrayList<String> data = new ArrayList<>();
        data.add("subdir/subsubdir/");
        cmd.setData(data);

        // when
        cmd.execute();

        // then
        then(mockFSA.getFileInDir(subsubdir, Constants.CLIENT_CONFIG_FILE)).exists();
        then(mockFSA.getFileInDir(subsubdir, Constants.CLIENT_CONFIG_FILE)).hasContent(ClientConfiguration.getDefaultConfiguration().toString());
    }
}
