package at.deder.ybr.test.cukes.steps;

import cucumber.api.PendingException;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

/**
 * Steps for Cucumber tests.
 *
 * @author lycis
 */
public class CukeSteps {

    @Given("^there is no config file")
    public void there_is_no_config_file() throws Throwable {
        throw new PendingException();
    }

    @When("^I update the project$")
    public void i_update_the_project() throws Throwable {
        // Write code here that turns the phrase above into concrete acts
        throw new PendingException();
    }

    @Then("^the error \"(.*?)\" is displayed$")
    public void the_error_is_displayed(String arg1) throws Throwable {
        // Write code here that turns the phrase above into concrete acts
        throw new PendingException();
    }

}
