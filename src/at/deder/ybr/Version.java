package at.deder.ybr;

public class Version {

    private static final int MAJOR = 0;
    private static final int MINOR = 0;
    private static final int PATCH = 0;
    private static final String CODENAME = "alpha";

    public static String getCompleteVersion() {
        return MAJOR + "." + MINOR + "." + PATCH + " (" + CODENAME + ")";
    }

    public static int getMajor() {
        return MAJOR;
    }

    public static int getMinor() {
        return MINOR;
    }

    public static int getPatch() {
        return PATCH;
    }

    public static String getCodename() {
        return CODENAME;
    }

    public static int compareTo(int major, int minor, int patch) {
        if (MAJOR < major) {
            return -1;
        }

        if (MAJOR > major) {
            return 1;
        }

        if (MINOR < minor) {
            return -1;
        }

        if (MINOR > minor) {
            return 1;
        }

        if (PATCH < patch) {
            return -1;
        }

        if (PATCH > patch) {
            return 1;
        }

        return 0;
    }
}
