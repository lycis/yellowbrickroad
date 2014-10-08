package at.deder.ybr.commands;

import at.deder.ybr.Constants;
import at.deder.ybr.channels.IOutputChannel;
import at.deder.ybr.filesysttem.IFileSystemAccessor;
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
    
    private IFileSystemAccessor fileSystem = null;
    private IOutputChannel     output     = null;

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
        // TODO implement
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setFileSystemAccessor(IFileSystemAccessor f) {
         fileSystem = f;
    }

    @Override
    public void setOutputAccessor(IOutputChannel o) {
        output = o;
    }
    
}
