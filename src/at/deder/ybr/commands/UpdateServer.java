package at.deder.ybr.commands;

import at.deder.ybr.Constants;
import java.util.List;

import at.deder.ybr.access.IFileSystemAccessor;

// TODO implement
public class UpdateServer implements ICliCommand {

    private boolean verbose = false;
    private String targetFolder = "";
    private IFileSystemAccessor fileSystem = null;

    @Override
    public void setOption(String name, String value) {
        if (Constants.OPTION_VERBOSE.equals(name)) {
            verbose = true;
        }
    }

    @Override
    public void setData(List<String> cliData) {
        if (cliData.size() < 1) {
            // no target folder given -> use current folder
            targetFolder = ".";
            return;
        }

        targetFolder = cliData.get(0);
    }

    @Override
    public void execute() {
        // TODO Auto-generated method stub
        System.out.println("update-server");
    }

    @Override
    public void setFileSystemAccessor(IFileSystemAccessor f) {
        fileSystem = f;
    }

}
