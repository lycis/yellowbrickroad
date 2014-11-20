package at.deder.ybr.commands;

import at.deder.ybr.channels.IOutputChannel;
import at.deder.ybr.channels.OutputChannelFactory;
import at.deder.ybr.filesystem.FileSystem;
import at.deder.ybr.filesystem.IFileSystemAccessor;
import java.io.File;
import java.util.List;

/**
 * Fetches all packages according to the config file.
 * @author lycis
 */
public class Update implements ICliCommand {

    @Override
    public void setOption(String name, String value) {
        // TODO implement
    }

    @Override
    public void setData(List<String> cliData) {
        // TODO implement
    }

    @Override
    public void execute() {
        IFileSystemAccessor filesystem = FileSystem.getAccess();
        IOutputChannel      output     = OutputChannelFactory.getOutputChannel();
        
        File config = filesystem.getClientConfigFile(filesystem.getWorkingDirectory().getAbsolutePath());
        if(config == null) {
            output.printErrLn("error: no configuration available");
        }
        
        FileSystem.releaseAccess(filesystem);
    }
    
}
