package at.deder.ybr.commands;

import at.deder.ybr.Constants;
import java.util.List;

import at.deder.ybr.filesystem.IFileSystemAccessor;
import at.deder.ybr.channels.AbstractOutputChannel;
import at.deder.ybr.channels.OutputChannelFactory;
import at.deder.ybr.repository.RepositoryEntry;
import at.deder.ybr.configuration.ServerManifest;
import at.deder.ybr.filesystem.FileSystem;
import com.esotericsoftware.yamlbeans.YamlException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UpdateServer implements ICliCommand {

    private String targetFolder = "";
    
    private IFileSystemAccessor fileSystem = null;
    private AbstractOutputChannel     output     = null;

    @Override
    public void setOption(String name, String value) {
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
        fileSystem = FileSystem.getAccess();
        output     = OutputChannelFactory.getOutputChannel();

        // check if target folder exists
        File target;
        if (".".equals(targetFolder)) {
            target = fileSystem.getWorkingDirectory();
        } else {
            target = fileSystem.getFile(targetFolder);
        }

        if (!target.exists()) {
            output.println("error: target folder does not exist");
            return;
        }

        // check if server structure is available in the target folder
        if (!isServerStructurePrepared(target)) {
            output.printErrLn("error: target folder does not contain a yellow brick road server");
            output.println("Run 'prepare-server' to initialise a server structure.");
            return;
        }
        
        output.println("Updating server manifest...");

        // recursively walk through the file system and add to the repo tree
        output.printDetailLn("Scanning file system...");
        RepositoryEntry rootNode = parseRepositoryEntry(null, fileSystem.getFileInDir(target, "repository"));

        // read existing manifest
        output.printDetailLn("Updating manifest...");
        ServerManifest manifest = null;
        try {
            manifest = ServerManifest.readYaml(new FileReader(fileSystem.getFileInDir(target, "manifest.yml")));
        } catch (FileNotFoundException ex) {
            output.printErrLn("error: manifest does not exist");
            return;
        } catch (YamlException ex) {
            output.printErrLn("error: parsing manifest failed ("+ex.getMessage()+")");
        }

        manifest.setRepository(rootNode);
        try {
            manifest.writeYaml(new FileWriter(fileSystem.getFileInDir(target, "manifest.yml")));
        } catch (IOException ex) {
            output.printErrLn("error: " + ex.getMessage());
            return;
        }

        output.println("done.");
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
            output.printDetailLn("Registered node '"+entry.getAbsolutePath()+"'");
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
            entry.setDescription("");
            return;
        } catch (IOException ex) {
            output.printDetailLn("warning: reading file '"+descriptionFile.getAbsolutePath()+"' resulted in: "+ex.getMessage());
            entry.setDescription("");
            return;
        } finally {
            if(reader != null) {
                try {
                    reader.close();
                } catch (IOException ex) {
                    output.printDetailLn("warning: resource leak #USxxx0 because of exception ("+ex.getMessage()+")");
                }
            }
        }
    
        entry.setDescription(description);
    }

}
