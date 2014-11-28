package at.deder.ybr.test.cukes;

import at.deder.ybr.channels.OutputChannelFactory;
import at.deder.ybr.commands.Describe;
import at.deder.ybr.commands.ICliCommand;
import at.deder.ybr.commands.Initialise;
import at.deder.ybr.commands.PrepareServer;
import at.deder.ybr.commands.Update;
import at.deder.ybr.commands.UpdateServer;
import at.deder.ybr.configuration.ClientConfiguration;
import at.deder.ybr.configuration.ServerManifest;
import at.deder.ybr.filesystem.FileSystem;
import at.deder.ybr.repository.RepositoryEntry;
import at.deder.ybr.test.mocks.CheckableSilentOutputChannel;
import at.deder.ybr.test.mocks.MockFileSystemAccessor;
import at.deder.ybr.test.mocks.MockUtils;
import com.esotericsoftware.yamlbeans.YamlException;
import cucumber.api.java.After;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import org.apache.commons.io.FileUtils;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Assert;

/**
 * Steps for Cucumber tests.
 *
 * @author lycis
 */
public class CukeSteps {

    MockFileSystemAccessor filesystem = new MockFileSystemAccessor();
    CheckableSilentOutputChannel output = new CheckableSilentOutputChannel();

    public CukeSteps() {
        // set default accessors and channels
        FileSystem.injectAccessor(filesystem);
        OutputChannelFactory.setOutputChannel(output);
    }

    @After
    public void clean_up_after_scenario() {
        FileSystem.injectAccessor(null);
    }

    @Given("^there is no config file")
    public void there_is_no_config_file() throws Throwable {
        File configFile = filesystem.getClientConfigFile(".");
        if (configFile != null) {
            configFile.delete();
        }
    }

    @When("^I update the project$")
    public void i_update_the_project() throws Throwable {
        executeCommand(new Update(), "");
    }

    @Then("^the error \"(.*?)\" is displayed$")
    public void the_error_is_displayed(String error) throws Throwable {
        assertThat(output.getError()).isEqualTo("error: " + error + "\n");
    }

    @Given("^the current directory is empty$")
    public void the_current_directory_is_empty() throws Throwable {
        File currentDir = filesystem.getWorkingDirectory();
        for (File f : currentDir.listFiles()) {
            if (f.isDirectory()) {
                FileUtils.deleteDirectory(f);
            } else {
                f.delete();
            }
        }
    }

    @Given("^the directory \"(.*?)\" is empty$")
    public void the_directory_is_empty(String dir) throws Throwable {
        File currentDir = filesystem.getFile(dir);
        assertThat(dir).isNotNull();
        for (File f : currentDir.listFiles()) {
            if (f.isDirectory()) {
                FileUtils.deleteDirectory(f);
            } else {
                f.delete();
            }
        }
    }

    @When("^I prepare a server in the current directory$")
    public void i_execute_the_server_preparation_for_the_current_directory() throws Throwable {
        executeCommand(new PrepareServer(), ".");
    }

    @Then("^there is a directory (?:named|called) \"(.*?)\"$")
    public void then_there_is_a_directory_named(String arg1) throws Throwable {
        assertThat(filesystem.getFile(arg1)).isNotNull();
        assertThat(filesystem.getFile(arg1)).isDirectory();
    }

    @Given("^a directory (?:named|called) \"(.*?)\" was created$")
    public void given_there_is_a_directory_named(String arg1) throws Throwable {
        filesystem.createFile(filesystem.getWorkingDirectory(), arg1, true);
    }

    @Then("^there is a file (?:named|called) \"(.*?)\"$")
    public void there_is_a_file_named(String arg1) throws Throwable {
        assertThat(filesystem.getFile(arg1)).isNotNull();
        assertThat(filesystem.getFile(arg1)).isFile();
    }

    @Then("^the file named \"(.*?)\" contains the default manifest$")
    public void the_file_named_contains_the_default_manifest(String arg1) throws Throwable {
        there_is_a_file_named(arg1);

        File target = filesystem.getFile(arg1);
        assertThat(target).isNotNull();
        ServerManifest sm = null;
        try {
            sm = ServerManifest.readYaml(new FileReader(target));
        } catch (FileNotFoundException ex) {
            Assert.fail("manifest file not found");
        }

        assertThat(sm).isEqualTo(MockUtils.getMockManifest());
    }

    @Then("^the file named \"(.*?)\" contains the default index page$")
    public void the_file_named_contains_the_default_index_page(String arg1) throws Throwable {
        there_is_a_file_named(arg1);

        File index = filesystem.getFile(arg1);
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
        } finally {
            try {
                reader.close();
            } catch (IOException ex) {
            };
        }

        assertThat(content).isEqualTo(PrepareServer.INDEX_DEFAULT_TEXT);
    }

    @When("^I prepare a server in directory \"(.*?)\"$")
    public void i_prepare_a_server_in_directory(String dir) throws Throwable {
        executeCommand(new PrepareServer(), dir);
    }

    @When("^I update the server$")
    public void i_update_the_server() {
        executeCommand(new UpdateServer(), ".");
    }

    @Then("^the output shows")
    public void the_output_shows(String expOutput) {
        assertThat(output.getOutput()).isEqualTo(expOutput);
    }

    @Then("^the manifest (\".*?\")*(?:| )looks like$")
    public void the_manifest_looks_like(String file, String content) throws YamlException, FileNotFoundException {

        ServerManifest compareManifest = null;
        try {
            compareManifest = ServerManifest.readYaml(new StringReader(content));
        } catch (YamlException ex) {
            Assert.fail("yaml parse exception: " + ex.getMessage());
        }

        if (file == null) {
            file = "manifest.yml";
        }

        File manifestFile = filesystem.getFile(file);
        assertThat(manifestFile).isNotNull();
        ServerManifest fileManifest = null;
        fileManifest = ServerManifest.readYaml(new FileReader(manifestFile));

        assertThat(compareManifest).isEqualTo(fileManifest);
    }

    @Then("^no error is displayed$")
    public void no_error_is_displayed() {
        assertThat(output.getError()).isEmpty();
    }

    @Given("^the current directory contains a prepared server$")
    public void the_current_directory_contains_a_prepared_server() throws Throwable {
        the_current_directory_is_empty();
        i_prepare_a_server_in_directory(".");
    }

    @Given("^the file \"(.*?)\" contains$")
    public void the_file_contains(String file, String content) throws IOException {
        File f = filesystem.getFile(file);
        if (f == null) {
            f = filesystem.createFile(filesystem.getWorkingDirectory(), file, false);
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(f))) {
            writer.write(content);
        }
    }

    @Given("^the current directory contains a complex repository$")
    public void the_current_directory_contains_a_complex_repository() throws IOException, Throwable {
        the_current_directory_contains_a_prepared_server();

        // create file structure
        File comDir = filesystem.getFile("repository/com/");

        File javaDir = filesystem.createFile(comDir, "java", true);
        File javaUtilDir = filesystem.createFile(javaDir, "util", true);
        File javaUtilIoDir = filesystem.createFile(javaUtilDir, "io", true);
        File javaUtilIoFileDir = filesystem.createFile(javaUtilIoDir, "file", true);

        File cppDir = filesystem.createFile(comDir, "cpp", true);
        File cppUtilDir = filesystem.createFile(cppDir, "util", true);
        File cppUtilx32Dir = filesystem.createFile(cppUtilDir, "x32", true);
        File cppUtilx64Dir = filesystem.createFile(cppUtilDir, "x64", true);

        File orgDir = filesystem.getFile("repository/org/");
        File orgJUnitDir = filesystem.createFile(orgDir, "junit", true);

        // place descriptions
        the_file_contains("repository/com/java/description", "commercial java libraries");
        the_file_contains("repository/com/java/util/description", "java utilities");
        the_file_contains("repository/com/java/util/io/description", "java I/O utilities");
        the_file_contains("repository/com/java/util/io/file/description", "input/output for files in java");
        the_file_contains("repository/com/cpp/description", "c++ libraries (binary format)");
        the_file_contains("repository/com/cpp/util/description", "c++ utilities");
        the_file_contains("repository/com/cpp/util/x32/description", "c++ utilities for x32 arch");
        the_file_contains("repository/com/cpp/util/x64/description", "c++ utilities for 64bit arch");
        the_file_contains("repository/org/junit/description", "junit framework");
    }

    @Then("^the repository entry (.*?) has description$")
    public void the_repository_entry_has_description(String entryId, String description) throws YamlException, FileNotFoundException {
        ServerManifest manifest = ServerManifest.readYaml(new FileReader(filesystem.getFile("manifest.yml")));
        RepositoryEntry entry = getPackage(manifest.getRepository(), entryId);
        assertThat(entry).isNotNull();
        assertThat(entry.getDescription()).isEqualTo(description);
    }

    // executes a CLI command
    private void executeCommand(ICliCommand cmd, String... args) {
        if (args.length == 1 && args[0].isEmpty()) {
            cmd.setData(new ArrayList<String>());
        } else {
            cmd.setData(Arrays.asList(args)); // no args
        }

        cmd.execute();
    }

    // Recursively walk through the repository tree and resolve a given path.
    private RepositoryEntry getPackage(RepositoryEntry root, String name) {
        if (name.startsWith(".")) {
            name = name.substring(1);
        }

        if (root == null) {
            return null;
        }

        List<String> path = Arrays.asList(name.split("\\."));
        if (path.size() < 1) {
            return null;
        }

        String target = path.get(0);
        RepositoryEntry nextEntry;
        try {
            nextEntry = (RepositoryEntry) root.getChildren().stream()
                    .filter(entry -> target.equals(((RepositoryEntry) entry).getName()))
                    .findFirst().get();
        } catch (NoSuchElementException e) {
            nextEntry = null;
        }

        if (!name.contains(".")) {
            return nextEntry;
        } else {
            String remainingPath = name.substring(name.indexOf(".") + 1);
            if (nextEntry != null && remainingPath.length() > 1) {
                return getPackage(nextEntry, remainingPath);
            }
        }
        return nextEntry;
    }

    @When("^I execute (?:|an )initialisation$")
    public void i_execute_an_initialisation() {
        executeCommand(new Initialise(), "");
    }

    @When("^I execute (?:|an )initialisation with \"(.*?)\"$")
    public void i_execute_an_initialisation_with(String args) {
        executeCommand(new Initialise(), args.split(" "));
    }

    @Then("^the file (?:named |)\"(.*?)\" contains the default configuration$")
    public void the_file_named_contains_the_default_configuration(String file) throws FileNotFoundException {
        ClientConfiguration config = ClientConfiguration.readYaml(new FileReader(filesystem.getFile(file)));
        assertThat(config).isEqualTo(ClientConfiguration.getDefaultConfiguration());
    }

    @When("^I request the description of package \"(.*?)\"$")
    public void i_request_the_description_of_package(String arg1) {
        executeCommand(new Describe(), arg1);
    }

    @Given("^the default configuration was written to \"(.*?)\"$")
    public void the_default_configuration_was_written_to(String arg1) throws Throwable {
        the_file_contains(arg1, ClientConfiguration.getDefaultConfiguration().toString());
    }
}
