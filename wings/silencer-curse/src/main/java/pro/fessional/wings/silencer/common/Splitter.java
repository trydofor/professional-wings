package pro.fessional.wings.silencer.common;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * @author trydofor
 * @see com.google.common.base.Splitter
 * @since 2023-06-13
 */
public class Splitter {

    @NotNull
    public static List<String> list(@NotNull String sep, String str) {
        if (str == null || str.isEmpty()) return Collections.emptyList();
        return com.google.common.base.Splitter.on(sep).splitToList(str);
    }

    @NotNull
    public static List<String> list(@NotNull String sep, String str, int max) {
        if (str == null || str.isEmpty()) return Collections.emptyList();
        return com.google.common.base.Splitter.on(sep).limit(max).splitToList(str);
    }

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
}
