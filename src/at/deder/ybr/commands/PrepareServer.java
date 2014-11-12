package at.deder.ybr.commands;

import java.io.File;
import java.io.IOException;
import java.util.List;

import at.deder.ybr.Constants;
import at.deder.ybr.filesystem.IFileSystemAccessor;
import at.deder.ybr.channels.IOutputChannel;
import at.deder.ybr.channels.OutputChannelFactory;
import at.deder.ybr.configuration.ServerManifest;
import java.io.BufferedWriter;
import java.io.FileWriter;

public class PrepareServer implements ICliCommand {

    private boolean verbose = false;
    private String targetFolder = ".";
    
    private IFileSystemAccessor fileSystem = null;
    private IOutputChannel     output     = null;
    
    // constants
    public static String INDEX_DEFAULT_TEXT = "Welcome to the Emerald City!\n";

    @Override
    public void setOption(String name, String value) {
        if (Constants.OPTION_VERBOSE.equals(name) && Constants.VALUE_TRUE.equals(value)) {
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
        output = OutputChannelFactory.getOutputChannel();
        
        boolean error = false;
        output.println("Preparing server structure...");

        // check if target folder exists
        File target = null;
        if (".".equals(targetFolder)) {
            target = fileSystem.getWorkingDirectory();
        } else {
            target = fileSystem.getFile(targetFolder);
        }

        if (!target.exists()) {
            output.println("error: target folder does not exist");
            return;
        }

        // create folder structure
        if (verbose) {
            output.print("Creating folder structure ... ");
        }
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

        if (verbose) {
            output.println("done");
        }

        // create manifest
        if (verbose) {
            output.print("Writing manifest ... ");
        }

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
        if (verbose) {
            output.println("done");
        }

        // create index.html
        if (verbose) {
            output.print("Writing index.html ... ");
        }

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
        if (verbose) {
            output.println("done");
        }

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

    @Override
    public void setFileSystemAccessor(IFileSystemAccessor f) {
        fileSystem = f;
    }

}
