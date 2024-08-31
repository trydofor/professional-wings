package pro.fessional.wings.silencer.modulate;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pro.fessional.mirana.cond.StaticFlag;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author trydofor
 * @since 2022-03-11
 */
public class RuntimeMode {

    private static volatile @NotNull RunMode runMode = RunMode.Nothing;
    private static volatile @NotNull ApiMode apiMode = ApiMode.Nothing;

    private static final ConcurrentHashMap<String, Boolean> runVote = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, Boolean> apiVote = new ConcurrentHashMap<>();

    protected RuntimeMode(@Nullable RunMode run, @Nullable ApiMode api) {
        if (run != null) {
            runMode = run;
            runVote.clear();
        }
        if (api != null) {
            apiMode = api;
            apiVote.clear();
        }
    }

    @NotNull
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

    public static boolean isRunMode(CharSequence mode) {
        if (mode == null) return false;
        return StaticFlag.vote(runMode.name(), mode.toString()) > 0;
    }

    public static boolean hasRunMode(CharSequence... modes) {
        for (CharSequence mode : modes) {
            if (isRunMode(mode)) return true;
        }
        return false;
    }

    public static boolean voteRunMode(String votes) {
        return runVote.computeIfAbsent(votes, k -> StaticFlag.hasVote(runMode.name(), k));
    }

    @NotNull
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

    public static boolean isApiMode(CharSequence mode) {
        if (mode == null) return false;
        return StaticFlag.vote(apiMode.name(), mode.toString()) > 0;
    }

    public static boolean hasApiMode(CharSequence... modes) {
        for (CharSequence mode : modes) {
            if (isApiMode(mode)) return true;
        }
        return false;
    }

    public static boolean voteApiMode(String votes) {
        return apiVote.computeIfAbsent(votes, k -> StaticFlag.hasVote(apiMode.name(), k));
    }

    // //

    private static final AtomicReference<Boolean> unitTest = new AtomicReference<>(null);

    public static boolean isUnitTest() {
        Boolean b = unitTest.get();
        if (b == null) {
            b = false;
            for (StackTraceElement el : new RuntimeException().getStackTrace()) {
                if (el.getClassName().startsWith("org.junit.")) {
                    b = true;
                    break;
                }
            }
            unitTest.set(b);
        }
        return b;
    }

}
