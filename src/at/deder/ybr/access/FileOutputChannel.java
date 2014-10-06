package at.deder.ybr.access;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Grants access to logfile writer.
 * @author lycis
 */
public class FileOutputChannel implements IOutputChannel {

    private File outputFile             = null;
    private BufferedWriter outputWriter = null;
    
    public FileOutputChannel(File f, boolean append) throws IOException {
        outputFile = f;
        outputWriter = new BufferedWriter(new FileWriter(outputFile));
    }
    
    public FileOutputChannel(File f) throws IOException {
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
