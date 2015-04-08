package at.deder.ybr.commands;

import at.deder.ybr.channels.AbstractOutputChannel;
import at.deder.ybr.channels.OutputChannelFactory;
import at.deder.ybr.configuration.ClientConfiguration;
import at.deder.ybr.configuration.InvalidConfigurationException;
import at.deder.ybr.configuration.ServerManifest;
import at.deder.ybr.filesystem.FileSystem;
import at.deder.ybr.filesystem.IFileSystemAccessor;
import at.deder.ybr.repository.RepositoryEntry;
import at.deder.ybr.server.IServerGateway;
import at.deder.ybr.server.ProtocolViolationException;
import at.deder.ybr.server.ServerFactory;
import at.deder.ybr.server.UnknownServerTypeException;
import at.deder.ybr.structures.Tree;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.commons.lang3.StringUtils;

/**
 * Search and list packages by name.
 *
 * @author lycis
 */
public class Search implements ICliCommand {

    private String searchString;

    @Override
    public void setOption(String name, String value) {
    }

    @Override
    public void setData(List<String> cliData) {
        if (cliData == null) {
            searchString = "";
            return;
        }

        if (cliData.isEmpty()) {
            searchString = "";
            return;
        }

        searchString = StringUtils.join(cliData, " ");
    }

    @Override
    public void execute() {
        AbstractOutputChannel output = OutputChannelFactory.getOutputChannel();
        IFileSystemAccessor filesystem = FileSystem.getAccess();
        if (searchString.isEmpty()) {
            output.printErrLn("error: please give a string to search for");
            return;
        }

        File config = filesystem.getClientConfigFile(filesystem.getWorkingDirectory());
        if (config == null) {
            output.printErrLn("error: no configuration file");
            return;
        }

        ClientConfiguration clientConf;
        try {
            clientConf = ClientConfiguration.readYaml(new FileReader(config));
        } catch (FileNotFoundException ex) {
            output.printErrLn("error: parsing configuration failed.");
            output.printErrLn("reason: " + ex.getMessage());
            return;
        }

        // check if search string compiles
        try {
            Pattern.compile(searchString);
        } catch (PatternSyntaxException ex) {
            output.printErrLn("error: syntax error in search pattern (" + ex.getPattern() + ")");
            output.printErrLn("cause: " + ex.getMessage());
            return;
        }

        IServerGateway server = null;
		try {
			server = ServerFactory.createServer(clientConf);
		} catch (UnknownServerTypeException e1) {
			output.printErrLn("unknown server type: "+clientConf.getType());
		} catch (InvalidConfigurationException e) {
			output.printErrLn("configuration error: "+e.getMessage());
		}
		
        ServerManifest manifest;
        try {
            manifest = server.getManifest();
        } catch (ProtocolViolationException ex) {
            output.printErrLn("error: accessing server manifest failed");
            output.printErrLn("reason: " + ex.getMessage());
            if (ex.getCause() != null) {
                output.printErrLn("cause: " + ex.getCause().getMessage());
            }
            return;
        }

        RepositoryEntry repository = manifest.getRepository();
        List<RepositoryEntry> matchingEntries = findMatchingByName(searchString, repository);

        // print formatted output
        if (matchingEntries.isEmpty()) {
            output.println("nothing found");
        } else {
            for (RepositoryEntry e : matchingEntries) {
                if (!e.getAbsoluteName().isEmpty()) {
                    output.println(e.getAbsoluteName());
                }
            }
        }

    }

    private List<RepositoryEntry> findMatchingByName(String mask, RepositoryEntry root) {
        List<RepositoryEntry> list = new ArrayList<RepositoryEntry>();

        if (root == null) {
            return list;
        }

        if (root.getAbsoluteName().matches(mask)) {
            list.add(root);
        }

        for (Tree t : root.getChildren()) {
            list.addAll(findMatchingByName(mask, (RepositoryEntry) t));
        }

        return list;
    }
}
