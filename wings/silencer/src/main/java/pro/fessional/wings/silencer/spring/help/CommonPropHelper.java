package pro.fessional.wings.silencer.spring.help;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * @author trydofor
 * @since 2022-06-17
 */
public class CommonPropHelper {

    public static final String DisabledValue = "-";

    public static boolean invalidValue(String value) {
        return value == null || value.isEmpty() || DisabledValue.equals(value);
    }

    public static boolean validValue(String value) {
        return !invalidValue(value);
    }

    public static TreeSet<String> validValue(Collection<String> values) {
        TreeSet<String> set = new TreeSet<>(values);
        set.removeIf(CommonPropHelper::invalidValue);
        return set;
    }

    public static TreeMap<String, String> validValue(Map<String, String> values) {
        TreeMap<String, String> map = new TreeMap<>();
        for (Map.Entry<String, String> en : values.entrySet()) {
            final String value = en.getValue();
            if (validValue(value)) {
                map.put(en.getKey(), value);
            }
        }
        return map;
    }
}
