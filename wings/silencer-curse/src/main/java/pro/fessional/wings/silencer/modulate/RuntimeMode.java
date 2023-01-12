package pro.fessional.wings.silencer.modulate;

import org.jetbrains.annotations.NotNull;

/**
 * @author trydofor
 * @since 2022-03-11
 */
public class RuntimeMode {

    @NotNull
    protected static RunMode runMode = RunMode.Nothing;

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
        return contains(mode.toString(), runMode.name());
    }

    public static boolean hasRunMode(CharSequence... modes) {
        for (CharSequence mode : modes) {
            if (isRunMode(mode)) return true;
        }
        return false;
    }

    @NotNull
    protected static ApiMode apiMode = ApiMode.Nothing;

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
        return contains(mode.toString(), apiMode.name());
    }

    public static boolean hasApiMode(CharSequence... modes) {
        for (CharSequence mode : modes) {
            if (isApiMode(mode)) return true;
        }
        return false;
    }

    private static boolean contains(String tstr, String ostr) {
        final int tlen = tstr.length();
        int toff = 0;
        for (int i = 0; i < tlen; i++) {
            if (Character.isJavaIdentifierPart(tstr.charAt(i))) {
                toff = i;
                break;
            }
        }

        final int olen = ostr.length();
        if (tstr.regionMatches(true, toff, ostr, 0, olen)) {
            final int idx = toff + olen;
            return idx == tlen || !Character.isJavaIdentifierPart(tstr.charAt(idx));
        }

        return false;
    }
}
