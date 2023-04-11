package pro.fessional.wings.silencer.spring.help;

import lombok.SneakyThrows;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import static org.springframework.util.ResourceUtils.CLASSPATH_URL_PREFIX;

/**
 * @author trydofor
 * @since 2022-06-17
 */
public class CommonPropHelper {

    public static final String DisabledValue = "-";
    public static final String MaskingValue = "*****";

    public static boolean notValue(String str) {
        return str == null || str.isEmpty() || DisabledValue.equals(str) || MaskingValue.equals(str);
    }

    public static boolean hasValue(String value) {
        return !notValue(value);
    }

    @NotNull
    public static LinkedHashSet<String> onlyValue(Collection<String> values) {
        if (values == null) values = Collections.emptyList();
        LinkedHashSet<String> set = new LinkedHashSet<>(values);
        set.removeIf(CommonPropHelper::notValue);
        return set;
    }

    @NotNull
    public static LinkedHashMap<String, String> onlyValue(Map<String, String> values) {
        if (values == null) values = Collections.emptyMap();
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        for (Map.Entry<String, String> en : values.entrySet()) {
            final String value = en.getValue();
            if (hasValue(value)) {
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
    @SneakyThrows @NotNull
    public static String toString(@NotNull Resource resource) {
        if (resource instanceof ClassPathResource) {
            final String path = ((ClassPathResource) resource).getPath();
            return CLASSPATH_URL_PREFIX + path;
        }
        return resource.getURL().toExternalForm();
    }

    /**
     * if this.value is invalid, then use that.value by key matches.
     *
     * @param thiz this map
     * @param that that map
     */
    public static void mergeNotValue(@NotNull Map<String, String> thiz, @Nullable Map<String, String> that) {
        if (that == null || that.isEmpty()) return;

        if (thiz.isEmpty()) {
            thiz.putAll(that);
        }
        else {
            for (Map.Entry<String, String> en : thiz.entrySet()) {
                final String v = en.getValue();
                if (notValue(v)) {
                    final String tv = that.get(en.getKey());
                    en.setValue(tv);
                }
            }
        }
    }

    /**
     * comma-separated values to StringArray
     *
     * @see StringUtils#commaDelimitedListToStringArray(String)
     */
    @Contract("_,true->!null")
    public static String[] arrayOrNull(String str, boolean nonnull) {
        final String[] arr = StringUtils.commaDelimitedListToStringArray(str);
        if (nonnull) {
            return arr;
        }
        else {
            return arr.length == 0 ? null : arr;
        }
    }
}
