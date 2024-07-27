package pro.fessional.wings.slardar.fastjson.filter;

import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.filter.PropertyPreFilter;
import org.jetbrains.annotations.NotNull;

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

    public void addClazz(@NotNull Class<?> clz) {
        clazz.add(clz);
    }

    public void addEqual(@NotNull String str) {
        equal.add(str);
    }

    public void addRegex(Pattern ptn) {
        regex.add(ptn);
    }

    public void addClazz(@NotNull Collection<Class<?>> clz) {
        clazz.addAll(clz);
    }

    public void addEqual(@NotNull Collection<String> str) {
        equal.addAll(str);
    }

    public void addRegex(Collection<Pattern> ptn) {
        regex.addAll(ptn);
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
