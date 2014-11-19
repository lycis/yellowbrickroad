package at.deder.ybr.test.cukes;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

/**
 * Test starter for cucumber tests.
 * @author lycis
 */

@RunWith(Cucumber.class)
@CucumberOptions(format = { "pretty", "html:target/cucumber", "json:target/cucumber.json" }, 
                 glue = "at.deder.ybr.test.cukes.steps",
                 features = {"features/"})
public class CukeSuite {
    
}
