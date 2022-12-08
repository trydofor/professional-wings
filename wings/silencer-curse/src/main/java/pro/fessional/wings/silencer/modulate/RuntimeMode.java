package pro.fessional.wings.silencer.modulate;

/**
 * @author trydofor
 * @since 2022-03-11
 */
public class RuntimeMode {

    protected static RunMode runMode = null;

    public static RunMode getRunMode() {
        return runMode;
    }

    public static boolean isRunMode(RunMode mode) {
        return runMode == mode;
    }

    public static boolean hasRunMode(RunMode... modes) {
        final RunMode thisMode = runMode;
        for (RunMode mode : modes) {
            if (thisMode == mode) return true;
        }
        return false;
    }

    protected static ApiMode apiMode = null;

    public static ApiMode getApiMode() {
        return apiMode;
    }

    public static boolean isApiMode(ApiMode mode) {
        return apiMode == mode;
    }

    public static boolean hasApiMode(ApiMode... modes) {
        final ApiMode thisMode = apiMode;
        for (ApiMode mode : modes) {
            if (thisMode == mode) return true;
        }
        return false;
    }
}
