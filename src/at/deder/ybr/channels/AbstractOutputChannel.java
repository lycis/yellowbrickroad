package at.deder.ybr.channels;

/**
 * Provides access to some kind of outputlayer (e.g. stdout)
 * @author lycis
 */
public abstract class AbstractOutputChannel {
    private boolean verbose = false;
    
    /**
     * writes the given string to the output.
     * @param s 
     */
    abstract public void print(String s);
    
    /**
     * writes the given string followed by a line break.
     * @param s 
     */
    public void println(String s) {
        print(s+"\n");
    }
    
    /**
     * writes the given string to the error output channel.
     * @param s 
     */
    abstract public void printErr(String s);
    
    /**
     * writes the given string to the error output channel and adds a
     * line break.
     * @param s 
     */
    public void printErrLn(String s) {
        printErr(s+"\n");
    }
    
    /**
     * only writes when verbose mode is active
     * @param s 
     */
    public void printDetail(String s) {
        if(verbose) {
            print(s);
        }
    }
    
    public void printDetailLn(String s) {
        printDetail(s+"\n");
    }
    
    /**
     * toggle verbose mode for this channel
     * @param verbose 
     */
    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }
}
