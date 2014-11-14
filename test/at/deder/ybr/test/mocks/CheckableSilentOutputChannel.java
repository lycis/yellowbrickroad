package at.deder.ybr.test.mocks;

import at.deder.ybr.channels.SilentOutputChannel;

/**
 * Use only for testing! Long use may make your system slow.
 * 
 * @author lycis
 */
public class CheckableSilentOutputChannel extends SilentOutputChannel {
    private String completeOutput = "";
    private String completeError  = "";
    
     @Override
    public void print(String s) {
        completeOutput += s;
        return;
    }

    @Override
    public void println(String s) {
        print(s+"\n");
        return;
    }

    @Override
    public void printErr(String s) {
        completeError += s;
        return;
    }

    @Override
    public void printErrLn(String s) {
        printErr(s+"\n");
        return;
    }
    
    /**
     * Check if the logged output equals a given string.
     */
    public boolean outputEquals(String s) {
        return completeOutput.equals(s);
    }
    
    /**
     * Check if the logged output contains the given regular expression.
     */
    public boolean outputContains(String regex) {
        return completeOutput.matches(regex);
    }
    
    /**
     * Check if the logged output equals a given string.
     */
    public boolean errorEquals(String s) {
        return completeError.equals(s);
    }
    
    /**
     * Check if the logged output contains the given regular expression.
     */
    public boolean errorContains(String regex) {
        return completeError.matches(regex);
    }
    
    public String getOutput() {
        return completeOutput;
    }
    
    public String getError() {
        return completeError;
    }
    
}
