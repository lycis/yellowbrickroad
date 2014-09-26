package at.deder.ybr.commands;

import at.deder.ybr.Constants;
import java.util.List;

import at.deder.ybr.access.IFileSystemAccessor;
import at.deder.ybr.beans.RepositoryEntry;
import at.deder.ybr.structures.Tree;
import java.io.File;

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
        System.out.println("Updating server manifest...");
        
        // check if target folder exists
        File target = null;
        if (".".equals(targetFolder)) {
            target = fileSystem.getWorkingDirectory();
        } else {
            target = fileSystem.getFile(targetFolder);
        }

        if (!target.exists()) {
            System.out.println("error: target folder does not exist");
            return;
        }
        
        // TODO recursively walk through the file system and add to the repo tree        
       RepositoryEntry rootNode = parseRepositoryEntry(null, target);
        
        System.out.println("done.");
    }

    @Override
    public void setFileSystemAccessor(IFileSystemAccessor f) {
        fileSystem = f;
    }
    
    /**
     * Recursively walks through the file tree and creates a node in the
     * repository tree for each directory.
     * 
     * @param parent parent tree node (set to <code>null</code> for root)
     * @param target directory to walk through
     * @return 
     */
    private RepositoryEntry parseRepositoryEntry(RepositoryEntry parent, File target) {
        if(!target.exists())
            return null;
        
        if(!target.isDirectory())
            return null;
        
        RepositoryEntry entry = new RepositoryEntry();
        entry.setName(target.getName());
       
        if(parent != null) {
            parent.addChild(entry);
        }
        
        File[] files = target.listFiles();
        for(File f: files) {
            if(f.isDirectory()) {
                entry.addChild(parseRepositoryEntry(entry, f));
            }
        }
        
        return entry;
    }

}
