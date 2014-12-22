package at.deder.ybr;

import at.deder.ybr.channels.ConsoleOutputChannel;
import at.deder.ybr.channels.FileOutputChannel;
import at.deder.ybr.filesystem.LocalFileSystemAccessor;
import at.deder.ybr.channels.AbstractOutputChannel;
import at.deder.ybr.channels.OutputChannelFactory;
import at.deder.ybr.channels.SilentOutputChannel;
import java.util.HashMap;
import java.util.Map;

import at.deder.ybr.commands.ICliCommand;
import at.deder.ybr.filesystem.FileSystem;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

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
       Options cliOptions = buildOptions();
       
       // parse command line
       CommandLineParser clParser = new YbrOptionParser();
       CommandLine cLine = null;
        try {
            cLine = clParser.parse(cliOptions, args);
        } catch (ParseException ex) {
            System.out.println("error: "+ex.getMessage());
            printUsageHint();
            System.exit(1);
        }

        // process command line options that do not require a command
        if (cLine.hasOption(Constants.OPTION_VERSION)) {
            printVersionInfo();
            return;
        }

        if (cLine.hasOption(Constants.OPTION_HELP)) {
            printUsageHint();
            return;
        }

        // process according command
        List<String> commandList = cLine.getArgList();

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

        // process and configure output mode
        if(cLine.hasOption(Constants.OPTION_VERBOSE) &&
           cLine.hasOption(Constants.OPTION_SILENT)) {
            System.err.println("error: -verbose and -silent must not be combined");
            System.exit(1);
        }
        
        if(cLine.hasOption(Constants.OPTION_LOG) &&
           cLine.hasOption(Constants.OPTION_SILENT)) {
            System.err.println("error: -log and -silent must not be combined");
            System.exit(1);
        }
        
        AbstractOutputChannel outputAccessor = new ConsoleOutputChannel(); // default for output is console
        if(cLine.hasOption(Constants.OPTION_SILENT)) { // use silent output accessor
            outputAccessor = new SilentOutputChannel();
        }
        
        outputAccessor.setVerbose(cLine.hasOption(Constants.OPTION_VERBOSE)); // verbose output
        
        if(cLine.hasOption(Constants.OPTION_LOG)) { // use logfile
            File logFile = new File(cLine.getOptionValue(Constants.OPTION_LOG));
            
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
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp( "ybr [options] <command>", buildOptions() );
        
        System.out.println("");
        System.out.println("commands:");
        System.out.println("describe <package>\tdisplay information about a specific package");
        System.out.println("initialise [-file <filename>] [target-dir]\tinitialises a directory for the use with ybr");
        System.out.println("update\t\t\tupdate the locally stored libraries of a project");
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
        
        at.deder.ybr.commands.Update cmdUpdate = new at.deder.ybr.commands.Update();
        commandMap.put("update", cmdUpdate);
        commandMap.put("upd", cmdUpdate);
        
        at.deder.ybr.commands.GenerateIndex cmdGenerateIndex = new at.deder.ybr.commands.GenerateIndex();
        commandMap.put("generate-index", cmdGenerateIndex);
        commandMap.put("gen-index", cmdGenerateIndex);
        commandMap.put("gen-ind", cmdGenerateIndex);
        commandMap.put("gen-i", cmdGenerateIndex);
    }

    private static Options buildOptions() {
        Option help    = new Option(Constants.OPTION_HELP,    "print this message");
        Option version = new Option(Constants.OPTION_VERSION, "print version information");
        Option verbose = new Option(Constants.OPTION_VERBOSE, "display extended output");
        Option silent  = new Option(Constants.OPTION_SILENT,  "suppress all output");
        Option log     = OptionBuilder.withArgName("file").hasArg().withDescription("write output to the given file").create(Constants.OPTION_LOG);
        
        
        Options cliOptions = new Options();
        cliOptions.addOption(help);
        cliOptions.addOption(version);
        cliOptions.addOption(verbose);
        cliOptions.addOption(silent);
        cliOptions.addOption(log);
        return cliOptions;
    }
}
