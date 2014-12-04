package at.deder.ybr.channels;

/**
 * This output accessor suppresses all output that is directed to its channels.
 * It actually contains only stubs for all printing methods that do nothing.
 * 
 * @author lycis
 */
public class SilentOutputChannel extends AbstractOutputChannel {

    @Override
    public void print(String s) {
        return;
    }

    @Override
    public void printErr(String s) {
        return;
    }    
}
