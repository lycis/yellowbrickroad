package at.deder.ybr.channels;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Grants access to logfile writer.
 *
 * @author lycis
 */
public class FileOutputChannel implements IOutputChannel {

    private File outputFile = null;

    public FileOutputChannel(File f, boolean append) throws IOException {
        outputFile = f;

        // clean file if append is not wanted
        if (!append) {
            cleanFile(f);
        }

    }

    public FileOutputChannel(File f) throws IOException {
        this(f, false);
    }

    @Override
    public void print(String s) {
        BufferedWriter outputWriter = null;
        try {
            outputWriter = new BufferedWriter(new FileWriter(outputFile, true));
            outputWriter.write(s);
        } catch (IOException ex) {
            System.err.println("error while writing to log: " + ex.getMessage());
        } finally {
            if (outputWriter != null) {
                try {
                    outputWriter.close();
                } catch (IOException ex) {
                }
            }
        }
    }

    @Override
    public void println(String s) {
        print(s + "\n");
    }

    @Override
    public void printErr(String s) {
        print(s);
    }

    @Override
    public void printErrLn(String s) {
        printErr(s + "\n");
    }

    /**
     * Clear all content from a file.
     * @param f
     * @throws IOException 
     */
    private void cleanFile(File f) throws IOException{
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(f));
            writer.write("");
            writer.close();
        } catch (IOException ex) {
            throw ex;
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException ex) {
                    // do nothing
                }
            }
        }
    }

}
