package at.deder.ybr;

/**
 * Holds all global constants
 *
 * @author lycis
 *
 */
public class Constants {

    // option names/keys
    public static final String OPTION_VERBOSE             = "verbose";
    public static final String OPTION_SILENT              = "silent";
    public static final String OPTION_HELP                = "help";
    public static final String OPTION_VERSION             = "version";
    public static final String OPTION_LOG                 = "log";
    public static final String OPTION_FILE                = "file";
    public static final String OPTION_FILE_SHORT          = "f";
    public static final String OPTION_CREATE_TARGET       = "create-target";
    public static final String OPTION_CREATE_TARGET_SHORT = "ct";
    
    // option values
    public static final String VALUE_TRUE = "true";
    public static final String VALUE_FALSE = "false";
    
    // file names
    public static final String CLIENT_CONFIG_FILE = "ybr-config.yml";
    public static final String INDEX_RULES_FILE   = "index_rules";
    public static final String DESCRIPTION_FILE   = "description";
    public static final String INDEX_FILE         = "index";

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
