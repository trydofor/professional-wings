package pro.fessional.wings.silencer.support;

import lombok.SneakyThrows;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.ResourceUtils;
import pro.fessional.mirana.data.Null;
import pro.fessional.wings.silencer.spring.help.ApplicationContextHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static org.springframework.boot.context.config.ConfigDataLocation.OPTIONAL_PREFIX;
import static org.springframework.util.ResourceUtils.CLASSPATH_URL_PREFIX;

/**
 * @author trydofor
 * @since 2022-06-17
 */
public class PropHelper {

    public static final String DisabledValue = "-";
    public static final String MaskingValue = "*****";

    /**
     * invalid if null/blank/DisabledValue/MaskingValue
     */
    public static boolean invalid(String str) {
        return str == null || str.isBlank() || DisabledValue.equals(str) || MaskingValue.equals(str);
    }

    /**
     * true if not invalid
     */
    public static boolean valid(String value) {
        return !invalid(value);
    }

    /**
     * uniq and remove invalid
     */
    @NotNull
    public static LinkedHashSet<String> onlyValid(Collection<String> values) {
        if (values == null) return new LinkedHashSet<>();

        final LinkedHashSet<String> set = new LinkedHashSet<>(values.size());
        for (String value : values) {
            if (valid(value)) set.add(value);
        }

        return set;
    }

    /**
     * remain the entry if its value is valid
     */
    @NotNull
    public static LinkedHashMap<String, String> onlyValid(Map<String, String> values) {
        if (values == null) return new LinkedHashMap<>();

        final LinkedHashMap<String, String> map = new LinkedHashMap<>(values.size());
        for (Map.Entry<String, String> en : values.entrySet()) {
            final String value = en.getValue();
            if (valid(value)) {
                map.put(en.getKey(), value);
            }
        }
        return map;
    }

    /**
     * if this.value is invalid, then use that.value by key
     *
     * @param thiz this map
     * @param that that map
     */
    public static void mergeToInvalid(@NotNull Map<String, String> thiz, @Nullable Map<String, String> that) {
        if (that == null || that.isEmpty()) return;

        if (thiz.isEmpty()) {
            thiz.putAll(that);
        }
        else {
            for (Map.Entry<String, String> en : thiz.entrySet()) {
                final String thisVal = en.getValue();
                if (invalid(thisVal)) {
                    final String thatVal = that.get(en.getKey());
                    en.setValue(thatVal);
                }
            }
        }
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

    /**
     * make sure prefix `optional:`
     */
    @NotNull
    public static String prefixOptional(@NotNull String url) {
        return url.startsWith(OPTIONAL_PREFIX)
            ? url
            : OPTIONAL_PREFIX + url;
    }

    /**
     * remove prefix `optional:`, return elz if no prefix
     */
    @Contract("_,!null->!null")
    public static String removeOptional(@NotNull String url, String elz) {
        boolean ok = false;
        while (url.startsWith(OPTIONAL_PREFIX)) {
            url = url.substring(9);
            ok = true;
        }
        return ok ? url : elz;
    }

    /**
     * remove prefix `optional:`, return the input url if no prefix
     */
    @NotNull
    public static String removeOptional(@NotNull String url) {
        return removeOptional(url, url);
    }

    /**
     * `optional:` prefix will return null if exception.
     * use ApplicationContext(if prepared) or DefaultResourceLoader as loader by default.
     */
    @Nullable
    public static Resource resourceString(String url) {
        ResourceLoader resourceLoader = ApplicationContextHelper.isPrepared() ?
            ApplicationContextHelper.getContext() : new DefaultResourceLoader();
        return resourceString(url, resourceLoader);
    }

    /**
     * `optional:` prefix will return null if exception
     */
    @Nullable
    public static Resource resourceString(String url, @NotNull ResourceLoader resourceLoader) {
        if (url == null || url.isBlank()) return null;

        String u1 = removeOptional(url, null);
        if (u1 != null) {
            try {
                return resourceLoader.getResource(u1);
            }
            catch (Exception e) {
                return null;
            }
        }
        else {
            return resourceLoader.getResource(url);
        }
    }

    /**
     * parse comma-delimited-list string, no strip item, no drop
     */
    @Contract("!null -> !null")
    public static String commaString(Object[] items) {
        return delimitedString(items, ",", false, false);
    }

    @Contract("!null,_ -> !null")
    public static String commaString(Object[] items, Function<Object, String> convert) {
        return delimitedString(items, ",", false, false, convert);
    }

    /**
     * parse comma-delimited-list string, no strip item, no drop
     */
    @Contract("!null -> !null")
    public static String commaString(Collection<?> items) {
        return delimitedString(items, ",", false, false);
    }

    @Contract("!null,_ -> !null")
    public static String commaString(Collection<?> items, Function<Object, String> convert) {
        return delimitedString(items, ",", false, false, convert);
    }

    @Contract("!null,_,_ -> !null")
    public static String commaString(Object[] items, boolean strip, boolean drop) {
        return delimitedString(items, ",", strip, drop);
    }

    @Contract("!null,_,_,_ -> !null")
    public static String commaString(Object[] items, boolean strip, boolean drop, Function<Object, String> convert) {
        return delimitedString(items, ",", strip, drop, convert);
    }

    @Contract("!null,_,_ -> !null")
    public static String commaString(Collection<?> items, boolean strip, boolean drop) {
        return delimitedString(items, ",", strip, drop);
    }

    @Contract("!null,_,_,_ -> !null")
    public static String commaString(Collection<?> items, boolean strip, boolean drop, Function<Object, String> convert) {
        return delimitedString(items, ",", strip, drop, convert);
    }

    @Contract("!null,_,_,_ -> !null")
    public static String delimitedString(Object[] items, String delimiter, boolean strip, boolean drop) {
        Collection<?> its = items == null ? null : Arrays.asList(items);
        return delimitedString(its, delimiter, strip, drop);
    }

    @Contract("!null,_,_,_,_ -> !null")
    public static String delimitedString(Object[] items, String delimiter, boolean strip, boolean drop, Function<Object, String> convert) {
        Collection<?> its = items == null ? null : Arrays.asList(items);
        return delimitedString(its, delimiter, strip, drop, convert);
    }

    @Contract("!null,_,_,_ -> !null")
    public static String delimitedString(Collection<?> items, String delimiter, boolean strip, boolean drop) {
        return delimitedString(items, delimiter, strip, drop, null);
    }

    @Contract("!null,_,_,_,_ -> !null")
    public static String delimitedString(Collection<?> items, String delimiter, boolean strip, boolean drop, Function<Object, String> convert) {
        if (items == null) return null;

        StringBuilder sb = new StringBuilder();
        boolean empty = true;
        for (Object obj : items) {
            String str = obj == null ? null : convert == null ? obj.toString() : convert.apply(obj);
            if (str == null) {
                if (!drop) {
                    empty = false;
                    sb.append(delimiter);
                }
                continue;
            }

            if (strip) str = str.strip();

            if (!(drop && invalid(str))) {
                empty = false;
                sb.append(delimiter).append(str);
            }
        }
        return empty ? Null.Str : sb.substring(delimiter.length());
    }

    /**
     * parse comma-delimited-list string, strip item, drop invalid
     */
    @NotNull
    public static String[] commaArray(String commaString) {
        return commaArray(commaString, true, true);
    }

    /**
     * parse comma-delimited-list string, whether to strip item, whether to drop invalid
     */
    @NotNull
    public static String[] commaArray(String commaString, boolean strip, boolean drop) {
        List<String> list = delimitedList(commaString, ",", strip, drop);
        return list.toArray(Null.StrArr);
    }

    /**
     * parse comma-delimited-list string, strip item, drop invalid
     */
    @NotNull
    public static List<String> commaList(String commaString) {
        return commaList(commaString, true, true);
    }

    @NotNull
    public static <T> List<T> commaList(String commaString, Function<String, T> convert) {
        return commaList(commaString, true, true, convert);
    }

    /**
     * parse comma-delimited-list string, whether to strip item, whether to drop invalid
     */
    @NotNull
    public static List<String> commaList(String commaString, boolean strip, boolean drop) {
        return delimitedList(commaString, ",", strip, drop);
    }

    @NotNull
    public static <T> List<T> commaList(String commaString, boolean strip, boolean drop, Function<String, T> convert) {
        return delimitedList(commaString, ",", strip, drop, convert);
    }

    /**
     * parse delimiter(comma if empty) delimited-list string, whether to strip item, whether to drop invalid
     */
    @NotNull
    public static List<String> delimitedList(String delimitedString, String delimiter, boolean strip, boolean drop) {
        return delimitedList(delimitedString, delimiter, strip, drop, null);
    }

    @SuppressWarnings("unchecked")
    @NotNull
    public static <T> List<T> delimitedList(String delimitedString, String delimiter, boolean strip, boolean drop, Function<String, T> convert) {
        if (delimitedString == null || delimitedString.isBlank() && drop) return Collections.emptyList();

        if (delimiter == null || delimiter.isEmpty()) delimiter = ",";
        final int len = delimiter.length();

        List<Object> result = new ArrayList<>();
        int offset = 0;
        int curIdx;
        while ((curIdx = delimitedString.indexOf(delimiter, offset)) != -1) {
            addValue(result, delimitedString.substring(offset, curIdx), strip, drop, convert);
            offset = curIdx + len;

        }
        if (offset <= delimitedString.length()) {
            addValue(result, delimitedString.substring(offset), strip, drop, convert);
        }

        return (List<T>) result;
    }

    private static void addValue(List<Object> result, String str, boolean strip, boolean drop, Function<String, ?> convert) {
        if (strip) str = str.strip();
        if (drop && invalid(str)) return;

        if (convert != null) {
            result.add(convert.apply(str));
        }
        else {
            result.add(str);
        }
    }
}
