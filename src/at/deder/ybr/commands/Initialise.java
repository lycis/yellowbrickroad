package at.deder.ybr.commands;

import at.deder.ybr.Constants;
import static at.deder.ybr.Main.printUsageHint;
import at.deder.ybr.channels.IOutputChannel;
import at.deder.ybr.channels.OutputChannelFactory;
import at.deder.ybr.configuration.ClientConfiguration;
import at.deder.ybr.filesystem.FileSystem;
import at.deder.ybr.filesystem.IFileSystemAccessor;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.lang3.StringUtils;

/**
 * This command initialises a local directory for the use with ybr. Essentially
 * it creates a config file.
 * 
 * @author lycis
 */
public class Initialise implements ICliCommand {
    
    private String targetDir;
    private String targetFile;
    private boolean validExecution = true;

    @Override
    public void setOption(String name, String value) {
        
    }

    @Override
    public void setData(List<String> cliData) {
        Option file = OptionBuilder.withArgName("file").hasArg().withLongOpt("file").create("f");
        Options opt = new Options();
        opt.addOption(file);
        
        CommandLineParser clParser = new PosixParser();
        CommandLine cLine = null;
        try {
            cLine = clParser.parse(opt, cliData.toArray(new String[cliData.size()]));
        } catch (ParseException ex) {
            IOutputChannel output = OutputChannelFactory.getOutputChannel();
            output.printErrLn("error: "+ex.getMessage());
            validExecution = false;
            return;
        }
        
        if(cLine.hasOption("f")) {
            targetFile = cLine.getOptionValue("f");
        } else {
            targetFile = Constants.CLIENT_CONFIG_FILE;
        }
        
        cliData = cLine.getArgList();
        if(cliData.size() > 0) {
            targetDir = cliData.get(0);
        } else {
            targetDir = "";
        }
    }

    @Override
    public void execute() {
        if(!validExecution) {
            return; // execution was aborted before start (e.g. bad options)
        }
        
        
        IFileSystemAccessor fileSystem = FileSystem.getAccess();
        IOutputChannel          output = OutputChannelFactory.getOutputChannel();
        
        File workDir = null;
        if(targetDir.isEmpty()) {
            workDir = fileSystem.getWorkingDirectory();
        } else {
            workDir = fileSystem.getFile(targetDir);
        }
        
        if(workDir == null) {
            output.printErrLn("error: target directory does not exist");
            return;
        }
        
        if(!workDir.isDirectory()) {
            output.printErrLn("error: target is not a directory");
            return;
        }
        
        File existingConf = fileSystem.getClientConfigFile(workDir.getPath());
        if(existingConf != null) {
            output.println("This directory already contains a ybr configuration "+
                           "in file '"+existingConf.getName()+"'. To overwrite this "+
                           "configuriation please delete the file beforehand.");
            return;
        }
        
        File configFile = null;
        try {
            configFile = fileSystem.createFile(workDir, targetFile, false);
        } catch (IOException ex) {
            output.printErrLn("error: could not create config file ("+ex.getMessage()+")");
            return;
        }
        
        try {
            ClientConfiguration.getDefaultConfiguration().writeYaml(new FileWriter(configFile));
        } catch (IOException ex) {
            output.printErrLn("error: could not write config file ("+ex.getMessage()+")");
        }
    }    
}
