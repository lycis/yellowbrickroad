package at.deder.ybr;

import at.deder.ybr.access.ConsoleOutputAccessor;
import at.deder.ybr.access.FileSystemAccessor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ml.options.OptionSet;
import ml.options.Options;
import ml.options.Options.Multiplicity;
import at.deder.ybr.commands.ICliCommand;

/**
 * This class is the main entry point for the application. It parses all command
 * line parameters and triggers according actions.
 *
 * @author lycis
 *
 */
public class Main {

    private static final Map<String, ICliCommand> commandMap = new HashMap<>();

    static {
        initCommandMap();
    }

    public static void main(String[] args) {
        Options cliOptions = new Options(args, 0, 99);

        // define possible command line options
        cliOptions.getSet().addOption("version", Multiplicity.ZERO_OR_ONE);
        cliOptions.getSet().addOption("help", Multiplicity.ZERO_OR_ONE);
        cliOptions.getSet().addOption("verbose", Multiplicity.ZERO_OR_ONE);

        // evaluate options
        if (!cliOptions.check(true, false)) {
            System.out.println("error: " + cliOptions.getCheckErrors());
            printUsageHint();
            System.exit(1);
        }

        // process command line options that do not require a command
        if (cliOptions.getSet().isSet("version")) {
            printVersionInfo();
            return;
        }

        if (cliOptions.getSet().isSet("help")) {
            printUsageHint();
            return;
        }

        // process according command
        ArrayList<String> commandList = cliOptions.getSet().getData();

        if (commandList.isEmpty()) {
			// if there is no command at this point (no-command options are already
            // filtered) than the user did not provide enough input
            printUsageHint();
            System.exit(1);
        }

        // find according command
        String command = commandList.remove(0); // first data item is the action
        ICliCommand executor = commandMap.get(command);
        if (executor == null) {
            // action does not exist
            System.out.println("unknown command '" + command + "'");
            System.exit(1);
        }

        executor.setData(commandList); // pass remaining cli data

        // set options on command
        OptionSet optionSet = cliOptions.getSet();
        executor.setOption(Constants.OPTION_VERBOSE,
                Constants.booleanToValue(optionSet.isSet(Constants.OPTION_VERBOSE)));
        
        // sec accessors
        executor.setFileSystemAccessor(new FileSystemAccessor());
        executor.setOutputAccessor(new ConsoleOutputAccessor());
        
        executor.execute();
    }

    public static void printUsageHint() {
        System.out.println("ybr [options] <command>");
        System.out.println("");
        System.out.println("options:");
        System.out.println("-help\t\tprint this information");
        System.out.println("-version\tprint version information");
        System.out.println("");
        System.out.println("commands:");
        System.out.println("");
        System.out.println("server-commands:");
        System.out.println("prepare-server\t\tprepare the basic folder structure for a server");
        System.out.println("update-server\t\tscan server tree for updates and generate manifest");
    }

    public static void printVersionInfo() {
        System.out.println(Version.getCompleteVersion());
    }

    /**
     * initialise the command map to map cli actions to internal command
     * executors
     */
    private static void initCommandMap() {
        commandMap.put("prepare-server", new at.deder.ybr.commands.PrepareServer());
        commandMap.put("update-server", new at.deder.ybr.commands.UpdateServer());
    }
}
