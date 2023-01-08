package pro.fessional.wings.silencer.spring.help;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

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

    public static LinkedHashSet<String> onlyValue(Collection<String> values) {
        LinkedHashSet<String> set = new LinkedHashSet<>(values);
        set.removeIf(CommonPropHelper::notValue);
        return set;
    }

    public static LinkedHashMap<String, String> onlyValue(Map<String, String> values) {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        for (Map.Entry<String, String> en : values.entrySet()) {
            final String value = en.getValue();
            if (hasValue(value)) {
                map.put(en.getKey(), value);
            }
        }
        return map;
    }

}
