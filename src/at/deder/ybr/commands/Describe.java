package at.deder.ybr.commands;

import at.deder.ybr.Constants;
import at.deder.ybr.channels.IOutputChannel;
import at.deder.ybr.channels.OutputChannelFactory;
import at.deder.ybr.configuration.ClientConfiguration;
import at.deder.ybr.filesystem.FileSystem;
import at.deder.ybr.filesystem.IFileSystemAccessor;
import at.deder.ybr.repository.RepositoryEntry;

import at.deder.ybr.server.IServerGateway;
import at.deder.ybr.server.ProtocolViolationException;
import at.deder.ybr.server.ServerFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

/**
 * This command prints out information regarding a specific package in the
 * repository.
 * 
 * @author lycis
 */
public class Describe implements ICliCommand {
    private boolean      verbose        = false;
    private List<String> packageUriList = null;

    @Override
    public void setOption(String name, String value) {
        if (Constants.OPTION_VERBOSE.equals(name) && Constants.VALUE_TRUE.equals(value)) {
            verbose = true;
        }
    }

    @Override
    public void setData(List<String> cliData) {
          packageUriList = cliData; // all inputs are supposed to be addresses of packages
    }

    @Override
    public void execute() {
        IFileSystemAccessor fileSystem = FileSystem.getAccess();
        IOutputChannel          output = OutputChannelFactory.getOutputChannel();
        
        File workDir = fileSystem.getWorkingDirectory();
        File configFile = fileSystem.getFileInDir(workDir, "ybr-config.yml");
        if(configFile == null) {
            output.printErrLn("error: ybr-config.yml does not exist");
            return;
        }
        
        ClientConfiguration config;
        try {
            config = ClientConfiguration.readYaml(new FileReader(configFile));
        } catch (FileNotFoundException ex) {
            output.printErrLn("error: ybr-config.yml is not accessible ("+ex.getMessage()+")");
            return;
        }
        
        // search package and display description
        IServerGateway server = ServerFactory.createServer(config);
        
        for(String packageName: packageUriList) {
            if(!packageName.startsWith(".")) {
                output.print(".");
            }
            output.println(packageName);
            
            RepositoryEntry entry = null;
            try {
                entry = server.getPackage(packageName);
            } catch (ProtocolViolationException ex) {
                if(ex.getCause() != null) {
                    output.printErrLn("error: "+ex.getMessage()+" ("+ex.getCause().getMessage()+")");
                }else {
                    output.printErrLn("error: "+ex.getMessage());
                }
                return;
            }
            if(entry == null) {
                output.println("<not found>");
            } else {
                if(entry.getDescription() != null &&
                   !entry.getDescription().isEmpty()) {
                    output.println(entry.getDescription());
                } else {
                    output.println("<no description>");
                }
            }
        }
    }    
}
