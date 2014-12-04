package at.deder.ybr.channels;

/**
 * This output accessor forwards output to the default console channels.
 * @author lycis
 */
public class ConsoleOutputChannel extends AbstractOutputChannel {
    @Override
    public void print(String s) {
        System.out.print(s);
    }

    @Override
    public void printErr(String s) {
        System.err.print(s);
    }    
}
