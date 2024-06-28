package pro.fessional.wings.silencer.support;

import lombok.SneakyThrows;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.ResourceUtils;
import pro.fessional.mirana.data.Null;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import static org.springframework.util.ResourceUtils.CLASSPATH_URL_PREFIX;

/**
 * @author trydofor
 * @since 2022-06-17
 */
public class PropHelper {

    public static final String DisabledValue = "-";
    public static final String MaskingValue = "*****";

    /**
     * true if empty or DisabledValue or MaskingValue
     */
    public static boolean nonValue(String str) {
        return str == null || str.isBlank() || DisabledValue.equals(str) || MaskingValue.equals(str);
    }

    /**
     * true if not non-value
     */
    public static boolean hasValue(String value) {
        return !nonValue(value);
    }

    /**
     * uniq and remove non-value
     */
    @NotNull
    public static LinkedHashSet<String> onlyValue(Collection<String> values) {
        if (values == null) return new LinkedHashSet<>();

        final LinkedHashSet<String> set = new LinkedHashSet<>(values);
        set.removeIf(PropHelper::nonValue);
        return set;
    }

    /**
     * remove item that has non-value
     */
    @NotNull
    public static LinkedHashMap<String, String> onlyValue(Map<String, String> values) {
        if (values == null) return new LinkedHashMap<>();

        final LinkedHashMap<String, String> map = new LinkedHashMap<>();
        for (Map.Entry<String, String> en : values.entrySet()) {
            final String value = en.getValue();
            if (!nonValue(value)) {
                map.put(en.getKey(), value);
            }
        }
        return map;
    }

    /**
     * Use 'classpath:' format for ClassPathResource and getURL().toExternalForm() for the rest
     *
     * @see ResourceUtils
     */
    @SneakyThrows
    @Contract("!null->!null")
    public static String stringResource(Resource resource) {
        if (resource == null) return null;

        if (resource instanceof ClassPathResource) {
            final String path = ((ClassPathResource) resource).getPath();
            return CLASSPATH_URL_PREFIX + path;
        }
        return resource.getURL().toExternalForm();
    }

    public static Resource resourceString(String url, @NotNull ResourceLoader resourceLoader) {
        if (url == null || url.isBlank()) return null;
        return resourceLoader.getResource(url);
    }

    /**
     * if this.value is non-value, then use that.value by key
     *
     * @param thiz this map
     * @param that that map
     */
    public static void mergeIfNon(@NotNull Map<String, String> thiz, @Nullable Map<String, String> that) {
        if (that == null || that.isEmpty()) return;

        if (thiz.isEmpty()) {
            thiz.putAll(that);
        }
        else {
            for (Map.Entry<String, String> en : thiz.entrySet()) {
                final String v = en.getValue();
                if (nonValue(v)) {
                    final String tv = that.get(en.getKey());
                    en.setValue(tv);
                }
            }
        }
    }

    /**
     * parse comma-delimited-list string, no strip item, no drop
     */
    @Contract("!null -> !null")
    public static String commaString(Object[] items) {
        return delimitedString(items, ",", false, false);
    }

    /**
     * parse comma-delimited-list string, no strip item, no drop
     */
    @Contract("!null -> !null")
    public static String commaString(Collection<?> items) {
        return delimitedString(items, ",", false, false);
    }

    @Contract("!null,_,_ -> !null")
    public static String commaString(Object[] items, boolean strip, boolean drop) {
        return delimitedString(items, ",", strip, drop);
    }

    @Contract("!null,_,_ -> !null")
    public static String commaString(Collection<?> items, boolean strip, boolean drop) {
        return delimitedString(items, ",", strip, drop);
    }

    @Contract("!null,_,_,_ -> !null")
    public static String delimitedString(Object[] items, String delimiter, boolean strip, boolean drop) {
        Collection<?> its = items == null ? null : Arrays.asList(items);
        return delimitedString(its, delimiter, strip, drop);
    }

    @Contract("!null,_,_,_ -> !null")
    public static String delimitedString(Collection<?> items, String delimiter, boolean strip, boolean drop) {
        if (items == null) return null;

        StringBuilder sb = new StringBuilder();
        boolean empty = true;
        for (Object obj : items) {
            if (obj == null) {
                if (!drop) {
                    empty = false;
                    sb.append(delimiter);
                }
                continue;
            }

            String str = obj.toString();
            if (strip) str = str.strip();

            if (!(drop && nonValue(str))) {
                empty = false;
                sb.append(delimiter).append(str);
            }
        }
        return empty ? Null.Str : sb.substring(delimiter.length());
    }

    /**
     * parse comma-delimited-list string, strip item, drop non-value
     */
    @NotNull
    public static String[] commaArray(String commaString) {
        return commaArray(commaString, true, true);
    }

    /**
     * parse comma-delimited-list string, whether to strip item, whether to drop non-value
     */
    @NotNull
    public static String[] commaArray(String commaString, boolean strip, boolean drop) {
        List<String> list = delimitedList(commaString, ",", strip, drop);
        return list.toArray(Null.StrArr);
    }

    /**
     * parse comma-delimited-list string, strip item, drop non-value
     */
    @NotNull
    public static List<String> commaList(String commaString) {
        return commaList(commaString, true, true);
    }

    /**
     * parse comma-delimited-list string, whether to strip item, whether to drop non-value
     */
    @NotNull
    public static List<String> commaList(String commaString, boolean strip, boolean drop) {
        return delimitedList(commaString, ",", strip, drop);
    }

    /**
     * parse delimiter(comma if empty) delimited-list string, whether to strip item, whether to drop non-value
     */
    @NotNull
    public static List<String> delimitedList(String delimitedString, String delimiter, boolean strip, boolean drop) {
        if (delimitedString == null || delimitedString.isBlank() && drop) return Collections.emptyList();

        if (delimiter == null || delimiter.isEmpty()) delimiter = ",";
        final int len = delimiter.length();

        List<String> result = new ArrayList<>();
        int offset = 0;
        int curIdx;
        while ((curIdx = delimitedString.indexOf(delimiter, offset)) != -1) {
            addValue(result, delimitedString.substring(offset, curIdx), strip, drop);
            offset = curIdx + len;

        }
        if (offset <= delimitedString.length()) {
            addValue(result, delimitedString.substring(offset), strip, drop);
        }

        return result;
    }

    private static void addValue(List<String> result, String str, boolean strip, boolean drop) {
        if (strip) str = str.strip();
        if (drop && nonValue(str)) return;
        result.add(str);
    }
}
