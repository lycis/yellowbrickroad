package at.deder.ybr.commands;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import at.deder.ybr.channels.AbstractOutputChannel;
import at.deder.ybr.channels.OutputChannelFactory;
import at.deder.ybr.configuration.ServerManifest;
import at.deder.ybr.filesystem.FileSystem;
import at.deder.ybr.filesystem.IFileSystemAccessor;

public class PrepareServer implements ICliCommand {

    private String targetFolder = ".";
    
    private IFileSystemAccessor fileSystem = null;
    private AbstractOutputChannel     output     = null;
    
    // constants
    public static String INDEX_DEFAULT_TEXT = "Welcome to the Emerald City!\n";

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
        
        boolean error = false;
        output.println("Preparing server structure...");

        // check if target folder exists
        File target = null;
        if (".".equals(targetFolder)) {
            target = fileSystem.getWorkingDirectory();
        } else {
            target = fileSystem.getFile(targetFolder);
        }

        if (target == null) {
            output.printErrLn("error: target does not exist");
            return;
        }

        // create folder structure
        output.printDetailLn("Creating folder structure ... ");
       
        File repository = createEmptyDirectory(target, "repository");
        if (repository == null) {
            return;
        }

        if (createEmptyDirectory(repository, "com") == null) {
            return;
        }

        if (createEmptyDirectory(repository, "org") == null) {
            return;
        }

        output.printDetailLn("done");

        // create manifest
        output.printDetailLn("Writing manifest ... ");

        File manifest = null;
        try {
            manifest = fileSystem.createFile(target, "manifest.yml", false);
        } catch (IOException e) {
            output.printErrLn("error: could not create manifest file (" + e.getMessage() + ")");
            error = true;
        }

        if (error) {
            return; // stop on error
        }
        ServerManifest sm = new ServerManifest();
        sm.initDefaults();

        try {
            sm.writeYaml(new FileWriter(manifest));
        } catch (IOException ex) {
            output.printErrLn("error: " + ex.getMessage());
            error = true;
        }

        if (error) {
            return; // stop on error
        }
        
        output.printDetailLn("done");

        // create index.html
        output.printDetailLn("Writing index.html ... ");

        File index = null;
        try {
            index = fileSystem.createFile(target, "index.html", false);
        } catch (IOException e) {
            output.printErrLn("error: could not create index.html (" + e.getMessage() + ")");
        }

        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(index));
            writer.write(INDEX_DEFAULT_TEXT);
        } catch (IOException ex) {
            output.printErrLn("error: " + ex.getMessage());
            error = true;
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException ex) {
            };
        }

        if (error) {
            return; // stop on error
        }
        output.printDetailLn("done");

        output.println("done.");
        output.println("");
        output.println("Please run 'update-server' to rebuild the server manifest.");
    }

    private File createEmptyDirectory(File parent, String name) {
        File dir;
        try {
            dir = fileSystem.createFile(parent, name, true);
        } catch (IOException e) {
            output.printErrLn("error: could not create directory '" + parent.getAbsolutePath() + File.separator + name + "' ("
                    + e.getMessage() + ")");
            return null;
        }

        return dir;
    }
}
