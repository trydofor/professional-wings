package pro.fessional.wings.warlock.service.conf.mode;

import java.util.function.Supplier;

/**
 * @author trydofor
 * @since 2022-03-11
 */
public class RuntimeMode {

    private static RunMode runMode = null;
    private static Supplier<RunMode> runSupplier = () -> RunMode.Local;

    public static void setRunMode(RunMode mode) {
        runMode = mode;
    }

    public static void setRunMode(Supplier<RunMode> supplier) {
        runMode = null;
        runSupplier = supplier;
    }

    public static RunMode getRunMode() {
        if (runMode == null) {
            runMode = runSupplier.get();
        }
        return runMode;
    }

    public static boolean isRunMode(RunMode mode) {
        return getRunMode() == mode;
    }

    public static boolean hasRunMode(RunMode... modes) {
        final RunMode thisMode = getRunMode();
        for (RunMode mode : modes) {
            if (thisMode == mode) return true;
        }
        return false;
    }


    private static ApiMode apiMode = null;
    private static Supplier<ApiMode> apiSupplier = () -> ApiMode.Nothing;

    public static void setApiMode(ApiMode mode) {
        apiMode = mode;
    }

    public static void setApiMode(Supplier<ApiMode> supplier) {
        apiMode = null;
        apiSupplier = supplier;
    }


    public static ApiMode getApiMode() {
        if (apiMode == null) {
            apiMode = apiSupplier.get();
        }
        return apiMode;
    }

    public static boolean isApiMode(ApiMode mode) {
        return getApiMode() == mode;
    }

    public static boolean hasApiMode(ApiMode... modes) {
        final ApiMode thisMode = getApiMode();
        for (ApiMode mode : modes) {
            if (thisMode == mode) return true;
        }
        return false;
    }

}
