package at.deder.ybr.test.cukes.steps;

import at.deder.ybr.channels.OutputChannelFactory;
import at.deder.ybr.commands.Update;
import at.deder.ybr.filesystem.FileSystem;
import at.deder.ybr.test.mocks.CheckableSilentOutputChannel;
import at.deder.ybr.test.mocks.MockFileSystemAccessor;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import java.io.File;
import java.util.ArrayList;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Steps for Cucumber tests.
 *
 * @author lycis
 */
public class CukeSteps {
    MockFileSystemAccessor filesystem   = new MockFileSystemAccessor();
    CheckableSilentOutputChannel output = new CheckableSilentOutputChannel();
    
    public CukeSteps() {
        // set default accessors and channels
        FileSystem.injectAccessor(filesystem);
        OutputChannelFactory.setOutputChannel(output);
    }
    
    @Given("^there is no config file")
    public void there_is_no_config_file() throws Throwable {
        File configFile = filesystem.getClientConfigFile(".");
        if(configFile != null) {
            configFile.delete();
        }
    }

    @When("^I update the project$")
    public void i_update_the_project() throws Throwable {
        Update cmd = new Update();
        cmd.setData(new ArrayList<String>()); // no args
        cmd.execute();
        
    }

    @Then("^the error \"(.*?)\" is displayed$")
    public void the_error_is_displayed(String error) throws Throwable {
        assertThat(output.getError()).isEqualTo("error: "+error);
    }

}
