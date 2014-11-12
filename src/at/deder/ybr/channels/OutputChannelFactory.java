package at.deder.ybr.channels;

/**
 * This factory is used to get access to an output channel.
 * @author lycis
 */
public class OutputChannelFactory {
    
    private static IOutputChannel defaultChannel = new ConsoleOutputChannel();
    
    // not instantiable
    private OutputChannelFactory() {
        
    }
    
    /**
     * Provides the default output channel
     */
    public static IOutputChannel getOutputChannel() {
        return defaultChannel;
    }
    
    /**
     * Changes the default output channel
     */
    synchronized public static void setOutputChannel(IOutputChannel o) {
        defaultChannel = o;
    }
}
