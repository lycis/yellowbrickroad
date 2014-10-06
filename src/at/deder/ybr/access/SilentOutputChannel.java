package at.deder.ybr.access;

/**
 * This output accessor suppresses all output that is directed to its channels.
 * It actually contains only stubs for all printing methods that do nothing.
 * 
 * @author lycis
 */
public class SilentOutputChannel implements IOutputChannel {

    @Override
    public void print(String s) {
        return;
    }

    @Override
    public void println(String s) {
        return;
    }

    @Override
    public void printErr(String s) {
        return;
    }

    @Override
    public void printErrLn(String s) {
        return;
    }
    
}
