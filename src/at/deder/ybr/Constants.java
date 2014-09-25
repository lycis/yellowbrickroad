package at.deder.ybr;

/**
 * Holds all global constants
 *
 * @author lycis
 *
 */
public class Constants {

    // option names/keys

    public static final String OPTION_VERBOSE = "verbose";

    // option values
    public static final String VALUE_TRUE = "true";
    public static final String VALUE_FALSE = "false";

    /**
     * Transforms a boolean into a String value
     *
     * @param b
     * @return
     */
    public static String booleanToValue(boolean b) {
        return (b ? VALUE_TRUE : VALUE_FALSE);
    }
}
