package at.deder.ybr.commands;

import at.deder.ybr.Constants;
import at.deder.ybr.channels.IOutputChannel;
import at.deder.ybr.channels.OutputChannelFactory;
import at.deder.ybr.configuration.ClientConfiguration;
import at.deder.ybr.filesystem.FileSystem;
import at.deder.ybr.filesystem.IFileSystemAccessor;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This command initialises a local directory for the use with ybr. Essentially
 * it creates a config file.
 * 
 * @author lycis
 */
public class Initialise implements ICliCommand {
    
    private String targetDir;

    @Override
    public void setOption(String name, String value) {
        
    }

    @Override
    public void setData(List<String> cliData) {
        if(cliData.size() > 0) {
            targetDir = cliData.get(0);
        } else {
            targetDir = "";
        }
    }

    @Override
    public void execute() {
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
        
        File configFile = null;
        try {
            configFile = fileSystem.createFile(workDir, Constants.CLIENT_CONFIG_FILE, false);
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
