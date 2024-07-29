package pro.fessional.wings.slardar.fastjson.filter;

import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.filter.PropertyPreFilter;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * <pre>
 * exclude the prop if
 * * instance of class
 * * name match regexp
 * * name equal
 * </pre>
 *
 * @author trydofor
 * @since 2024-07-27
 */
public class ExcludePropertyPreFilter implements PropertyPreFilter {

    private final Set<Class<?>> clazz = new HashSet<>();
    private final Set<String> equal = new HashSet<>();
    private final Set<Pattern> regex = new HashSet<>();

    /**
     * <pre>
     * support exclude type,
     * * Class - object is instance of
     * * String - name equals
     * * Pattern - name matches regexp
     * * Collection - any of above type
     * * Object[] - any of above type
     * </pre>
     */
    public ExcludePropertyPreFilter(Object exclude) {
        addExclude(exclude);
    }

    protected void addExclude(Object exclude) {
        if (exclude == null) {
            return;
        }
        else if (exclude instanceof Class<?> clz) {
            clazz.add(clz);
        }
        else if (exclude instanceof String str) {
            equal.add(str);
        }
        else if (exclude instanceof Pattern reg) {
            regex.add(reg);
        }
        else if (exclude instanceof Collection<?> col) {
            for (Object o : col) addExclude(o);
        }
        else if (exclude instanceof Object[] arr) {
            for (Object o : arr) addExclude(o);
        }
    }

    @Override
    public boolean process(JSONWriter writer, Object source, String name) {
        for (Class<?> clz : clazz) {
            if (clz.isInstance(source)) return false;
        }

        for (Pattern ptn : regex) {
            if (ptn.matcher(name).find()) return false;
        }

        return !equal.contains(name);
    }
}
