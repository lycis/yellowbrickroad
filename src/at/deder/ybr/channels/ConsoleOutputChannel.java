package at.deder.ybr.channels;

/**
 * This output accessor forwards output to the default console channels.
 * @author lycis
 */
public class ConsoleOutputChannel implements IOutputChannel {

    @Override
    public void print(String s) {
        System.out.print(s);
    }

    @Override
    public void println(String s) {
        System.out.println(s);
    }

    @Override
    public void printErr(String s) {
        System.err.print(s);
    }

    @Override
    public void printErrLn(String s) {
        System.err.println(s);
    }
    
}
