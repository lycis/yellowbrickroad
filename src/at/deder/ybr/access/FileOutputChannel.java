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
    }
    
    public FileOutputChannel(File f) throws IOException {
        this(f, false);
    }
        
    @Override
    public void print(String s) {
        try {
            outputWriter = new BufferedWriter(new FileWriter(outputFile, true));
            outputWriter.write(s);
        } catch (IOException ex) {
            System.err.println("exception: "+ex.getMessage());
        }finally {
            if(outputWriter != null) {
                try{
                    outputWriter.close();
                } catch (IOException ex) {} 
            }
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
