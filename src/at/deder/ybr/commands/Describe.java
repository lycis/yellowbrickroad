package at.deder.ybr.commands;

import at.deder.ybr.Constants;
import at.deder.ybr.channels.AbstractOutputChannel;
import at.deder.ybr.channels.OutputChannelFactory;
import at.deder.ybr.configuration.ClientConfiguration;
import at.deder.ybr.configuration.InvalidConfigurationException;
import at.deder.ybr.filesystem.FileSystem;
import at.deder.ybr.filesystem.IFileSystemAccessor;
import at.deder.ybr.repository.RepositoryEntry;
import at.deder.ybr.server.IServerGateway;
import at.deder.ybr.server.ProtocolViolationException;
import at.deder.ybr.server.ServerFactory;
import at.deder.ybr.server.UnknownServerTypeException;

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
    private List<String> packageUriList = null;

    @Override
    public void setOption(String name, String value) {
    }

    @Override
    public void setData(List<String> cliData) {
          packageUriList = cliData; // all inputs are supposed to be addresses of packages
    }

    @Override
    public void execute() {
        IFileSystemAccessor fileSystem = FileSystem.getAccess();
        AbstractOutputChannel          output = OutputChannelFactory.getOutputChannel();
        
        File workDir = fileSystem.getWorkingDirectory();
        File configFile = fileSystem.getClientConfigFile(workDir);
        if(configFile == null) {
            output.printErrLn("error: directory does not contain any config file");
            return;
        }
        
        ClientConfiguration config;
        try {
            config = ClientConfiguration.readYaml(new FileReader(configFile));
        } catch (FileNotFoundException ex) {
            output.printErrLn("error: config file "+configFile.getName()+"' is not accessible ("+ex.getMessage()+")");
            return;
        }
        
        // search package and display description
        IServerGateway server = null;
		try {
			server = ServerFactory.createServer(config);
		} catch (UnknownServerTypeException e) {
			output.printErrLn("unknown server type: "+config.getType());
		} catch (InvalidConfigurationException e) {
			output.printErrLn("configuration error: "+e.getMessage());
		}
        
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
