package pro.fessional.wings.silencer.common;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * @author trydofor
 * @since 2023-06-13
 */
public class Splitter {

    /**
     * split string by separator
     *
     * @param sep separator
     * @param str string
     * @return list of string
     * @see com.google.common.base.Splitter
     */
    @NotNull
    public static List<String> list(@NotNull String sep, String str) {
        if (str == null || str.isEmpty()) return Collections.emptyList();
        return com.google.common.base.Splitter.on(sep).splitToList(str);
    }

    /**
     * split string by separator
     *
     * @param sep separator
     * @param str string
     * @param max max items
     * @return list of string
     * @see com.google.common.base.Splitter
     */
    @NotNull
    public static List<String> list(@NotNull String sep, String str, int max) {
        if (str == null || str.isEmpty()) return Collections.emptyList();
        return com.google.common.base.Splitter.on(sep).limit(max).splitToList(str);
    }

    /**
     * split string by separator and convert to T
     *
     * @param sep separator
     * @param str string
     * @param fun convert string to T
     * @return list of T
     * @see com.google.common.base.Splitter
     */
    @NotNull
    public static <T> List<T> list(@NotNull String sep, String str, @NotNull Function<String, T> fun) {
        if (str == null || str.isEmpty()) return Collections.emptyList();
        final Iterable<String> iter = com.google.common.base.Splitter.on(sep).split(str);
        List<T> result = new ArrayList<>();
        for (String s : iter) {
            result.add(fun.apply(s));
        }
        return Collections.unmodifiableList(result);
    }

    /**
     * split string by separator and convert to T
     *
     * @param sep separator
     * @param str string
     * @param max max items
     * @param fun convert string to T
     * @return list of T
     * @see com.google.common.base.Splitter
     */
    @NotNull
    public static <T> List<T> list(@NotNull String sep, String str, int max, @NotNull Function<String, T> fun) {
        if (str == null || str.isEmpty()) return Collections.emptyList();
        final Iterable<String> iter = com.google.common.base.Splitter.on(sep).limit(max).split(str);
        List<T> result = new ArrayList<>();
        for (String s : iter) {
            result.add(fun.apply(s));
        }
        return Collections.unmodifiableList(result);
    }

    /**
     * count items split by separator
     * <pre>
     * 0 - if str is empty
     * 1 - if str does not contain sep
     * n+1 - if str contains n sep
     * </pre>
     *
     * @param sep separator
     * @param str string
     * @return count of items
     * @see StringUtils#countMatches(CharSequence, CharSequence)
     */
    public static int count(@NotNull String sep, String str) {
        if (str == null || str.isEmpty()) return 0;
        return StringUtils.countMatches(str, sep) + 1;
    }
}
