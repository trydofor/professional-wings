package pro.fessional.wings.silencer.spring.help;

import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.ResourceUtils;

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
     * 对 ClassPathResource 使用 'classpath:'格式，其他使用getURL().toExternalForm()
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

}
