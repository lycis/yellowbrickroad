package at.deder.ybr.access;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Grants access to logfile writer.
 * @author lycis
 */
public class FileOutputAccessor implements IOutputAccessor {

    private File outputFile             = null;
    private BufferedWriter outputWriter = null;
    
    public FileOutputAccessor(File f, boolean append) throws IOException {
        outputFile = f;
        outputWriter = new BufferedWriter(new FileWriter(outputFile));
    }
    
    public FileOutputAccessor(File f) throws IOException {
        this(f, false);
    }
        
    @Override
    public void print(String s) {
        try {
            outputWriter.write(s);
        } catch (IOException ex) {
            // TODO what to do with it?
        }
    }

    @Override
    public void println(String s) {
        print(s+"\n");
    }

    @Override
    public void printErr(String s) {
        print(s);
    }

    @Override
    public void printErrLn(String s) {
        printErr(s+"\n");
    }
    
}
