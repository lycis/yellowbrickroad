package at.deder.ybr.commands;

import java.io.File;
import java.io.IOException;
import java.util.List;

import at.deder.ybr.Constants;
import at.deder.ybr.access.IFileSystemAccessor;
import at.deder.ybr.beans.ServerManifest;
import java.io.BufferedWriter;
import java.io.FileWriter;

// TODO implement
public class PrepareServer implements ICliCommand {

    private boolean verbose = false;

    private String targetFolder = ".";
    private IFileSystemAccessor fileSystem = null;
    
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
        boolean error = false;
        System.out.println("Preparing server structure...");

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

        // create folder structure
        if (verbose) {
            System.out.print("Creating folder structure ... ");
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
            System.out.println("done");
        }

        // create manifest
        if (verbose) {
            System.out.print("Writing manifest ... ");
        }

        File manifest = null;
        try {
            manifest = fileSystem.createFile(target, "manifest.yml", false);
        } catch (IOException e) {
            System.err.println("error: could not create manifest file (" + e.getMessage() + ")");
            error = true;
        }
        
        if(error)
            return; // stop on error

        ServerManifest sm = new ServerManifest();
        sm.initDefaults();
        
        try {
            sm.writeYaml(new FileWriter(manifest));
        } catch (IOException ex) {
           System.err.println("error: "+ex.getMessage());
           error = true;
        }
        
        if(error)
            return; // stop on error
        
        if (verbose) {
            System.out.println("done");
        }

        // create index.html
        if (verbose) {
            System.out.print("Writing index.html ... ");
        }
        
        File index = null;
        try {
            index = fileSystem.createFile(target, "index.html", false);
        } catch (IOException e) {
            System.err.println("error: could not create index.html (" + e.getMessage() + ")");
        }

        BufferedWriter writer = null;
        try{
            writer = new BufferedWriter(new FileWriter(index));
            writer.write(INDEX_DEFAULT_TEXT);
        } catch (IOException ex) {
            System.err.println("error: "+ex.getMessage());
            error = true;
        } finally {
            try{writer.close();}catch(IOException ex){};
        }
        
        if(error)
            return; // stop on error
        
        if (verbose) {
            System.out.println("done");
        }
        
        System.out.println("done.");
    }

    private File createEmptyDirectory(File parent, String name) {
        File dir = null;
        try {
            dir = fileSystem.createFile(parent, name, true);
        } catch (IOException e) {
            System.err.println("error: could not create directory '" + parent.getAbsolutePath() + File.separator + name + "' ("
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
