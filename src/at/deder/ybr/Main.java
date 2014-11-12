package at.deder.ybr;

import at.deder.ybr.channels.ConsoleOutputChannel;
import at.deder.ybr.channels.FileOutputChannel;
import at.deder.ybr.filesystem.LocalFileSystemAccessor;
import at.deder.ybr.channels.IOutputChannel;
import at.deder.ybr.channels.OutputChannelFactory;
import at.deder.ybr.channels.SilentOutputChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ml.options.OptionSet;
import ml.options.Options;
import ml.options.Options.Multiplicity;
import at.deder.ybr.commands.ICliCommand;
import at.deder.ybr.filesystem.FileSystem;
import java.io.File;
import java.io.IOException;
import ml.options.Options.Separator;

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
        cliOptions.getSet().addOption(Constants.OPTION_VERSION, Multiplicity.ZERO_OR_ONE);
        cliOptions.getSet().addOption(Constants.OPTION_HELP,    Multiplicity.ZERO_OR_ONE);
        cliOptions.getSet().addOption(Constants.OPTION_VERBOSE, Multiplicity.ZERO_OR_ONE);
        cliOptions.getSet().addOption(Constants.OPTION_SILENT,  Multiplicity.ZERO_OR_ONE);
        cliOptions.getSet().addOption(Constants.OPTION_LOG, Separator.BLANK, Multiplicity.ZERO_OR_ONE);
        
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
        
        // process and configure output mode
        if(optionSet.isSet(Constants.OPTION_VERBOSE) &&
                optionSet.isSet(Constants.OPTION_SILENT)) {
            System.err.println("error: -verbose and -silent must not be combined");
            System.exit(1);
        }
        
        if(optionSet.isSet(Constants.OPTION_LOG) &&
                optionSet.isSet(Constants.OPTION_SILENT)) {
            System.err.println("error: -log and -silent must not be combined");
            System.exit(1);
        }
        
        if(optionSet.isSet(Constants.OPTION_VERBOSE)) { // verbose output
            executor.setOption(Constants.OPTION_VERBOSE, Constants.VALUE_TRUE);
        } else {
            executor.setOption(Constants.OPTION_VERBOSE, Constants.VALUE_FALSE);
        }
        
        IOutputChannel outputAccessor = new ConsoleOutputChannel(); // default for output is console
        if(optionSet.isSet(Constants.OPTION_SILENT)) { // use silent output accessor
            outputAccessor = new SilentOutputChannel();
        }
        
        if(optionSet.isSet(Constants.OPTION_LOG)) { // use logfile
            File logFile = new File(optionSet.getOption(Constants.OPTION_LOG).getResultValue(0));
            
            try {
                outputAccessor = new FileOutputChannel(logFile);
            } catch (IOException ex) {
                System.err.println("error: can not open log file ("+ex.getMessage()+")");
                System.exit(1);
            }
        }
        
        
        // set accessors
        FileSystem.setAccessorClass(LocalFileSystemAccessor.class);
        OutputChannelFactory.setOutputChannel(outputAccessor);
        
        executor.execute();
    }

    public static void printUsageHint() {
        System.out.println("ybr [options] <command>");
        System.out.println("");
        System.out.println("options:");
        System.out.println("-help\t\tprint this information");
        System.out.println("-version\tprint version information");
        System.out.println("-silent\tsuppress all output");
        System.out.println("-verbose\tdisplay extended output");
        System.out.println("-log <file>\twrite output to the given file");
        System.out.println("");
        System.out.println("commands:");
        System.out.println("describe <package>\t\tdisplay information about a specific package");
        System.out.println("initialise [target-dir]\t\tinitialises a directory for the use with ybr");
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
        
        at.deder.ybr.commands.Describe cmdDescribe = new at.deder.ybr.commands.Describe();
        commandMap.put("describe", cmdDescribe);
        commandMap.put("desc", cmdDescribe);
        
        at.deder.ybr.commands.Initialise cmdInit = new at.deder.ybr.commands.Initialise();
        commandMap.put("initialise", cmdInit);
        commandMap.put("initialize", cmdInit);
        commandMap.put("init", cmdInit);
    }
}
