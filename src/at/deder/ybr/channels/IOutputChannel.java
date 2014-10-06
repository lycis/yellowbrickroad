package at.deder.ybr.channels;

/**
 * Provides access to some kind of outputlayer (e.g. stdout)
 * @author lycis
 */
public interface IOutputChannel {
    /**
     * writes the given string to the output.
     * @param s 
     */
    public void print(String s);
    
    /**
     * writes the given string followed by a line break.
     * @param s 
     */
    public void println(String s);
    
    /**
     * writes the given string to the error output channel.
     * @param s 
     */
    public void printErr(String s);
    
    /**
     * writes the given string to the error output channel and adds a
     * line break.
     * @param s 
     */
    public void printErrLn(String s);
}
