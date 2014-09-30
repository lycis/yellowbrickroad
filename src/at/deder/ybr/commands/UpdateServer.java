package at.deder.ybr.commands;

import at.deder.ybr.Constants;
import java.util.List;

import at.deder.ybr.access.IFileSystemAccessor;
import at.deder.ybr.beans.RepositoryEntry;
import at.deder.ybr.beans.ServerManifest;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

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
        File target;
        if (".".equals(targetFolder)) {
            target = fileSystem.getWorkingDirectory();
        } else {
            target = fileSystem.getFile(targetFolder);
        }

        if (!target.exists()) {
            System.out.println("error: target folder does not exist");
            return;
        }

        // check if server structure is available in the target folder
        if (!isServerStructurePrepared(target)) {
            System.out.println("error: target folder does not contain a yellow brick road server");
            System.out.println("Run 'prepare-server' to initialise a server structure.");
            return;
        }

        // recursively walk through the file system and add to the repo tree
        printDetail("Scanning file system...");
        RepositoryEntry rootNode = parseRepositoryEntry(null, fileSystem.getFileInDir(target, "repository"));

        // read existing manifest
        printDetail("Updating manifest...");
        ServerManifest manifest = null;
        try {
            manifest = ServerManifest.readYaml(new FileReader(fileSystem.getFileInDir(target, "manifest.yml")));
        } catch (FileNotFoundException ex) {
            System.err.println("error: could not read existing manifest (" + ex.getMessage() + ")");
            return;
        }

        manifest.setRepository(rootNode);
        try {
            manifest.writeYaml(new FileWriter(fileSystem.getFileInDir(target, "manifest.yml")));
        } catch (IOException ex) {
            System.err.println("error: " + ex.getMessage());
            return;
        }

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
        if (!target.exists()) {
            return null;
        }

        if (!target.isDirectory()) {
            return null;
        }

        RepositoryEntry entry = new RepositoryEntry();
        entry.setName(target.getName());

        // read node description
        File descriptionFile = fileSystem.getFileInDir(target, "description");
        if (descriptionFile != null) {
            setNodeDescription(entry, descriptionFile);
        }

        if (parent != null) {
            parent.addChild(entry);
            printDetail("Registered node '"+entry.getAbsolutePath()+"'");
        }

        File[] files = target.listFiles();
        for (File f : files) {
            if (f.isDirectory()) {
                entry.addChild(parseRepositoryEntry(entry, f));
            }
        }

        return entry;
    }

    /**
     * Checks if the target folder contains a valid server file structure
     *
     * @param target
     * @return
     */
    private boolean isServerStructurePrepared(File target) {
        File[] fileList = target.listFiles();
        boolean repositoryDir = false;
        boolean manifest = false;

        for (File f : fileList) {
            if (f.isDirectory() && "repository".equals(f.getName())) {
                repositoryDir = true;
            }

            if (!f.isDirectory() && "manifest.yml".equals(f.getName())) {
                manifest = true;
            }
        }

        return (repositoryDir && manifest);
    }

    /**
     * Fill the description of a repository node with the content of the given
     * file.
     *
     * @param entry
     * @param descriptionFile
     */
    private void setNodeDescription(RepositoryEntry entry, File descriptionFile) {
        BufferedReader reader = null;
        String description = "";
        
        try {
            reader = new BufferedReader(new FileReader(descriptionFile));
            String line;
            while((line=reader.readLine()) != null) {
                description += line;
            }
        } catch (FileNotFoundException ex) {
            // TODO error handling
            entry.setDescription("");
            return;
        } catch (IOException ex) {
            // TODO error handling
            entry.setDescription("");
            return;
        } finally {
            if(reader != null) {
                try {
                    reader.close();
                } catch (IOException ex) {
                    // do nothing
                }
            }
        }
    
        entry.setDescription(description);
    }
    
    /**
     * print a message when verbose mode is active
     * @param message 
     */
    private void printDetail(String message) {
        if(verbose) {
            System.out.println(message);
        }
    }
}
