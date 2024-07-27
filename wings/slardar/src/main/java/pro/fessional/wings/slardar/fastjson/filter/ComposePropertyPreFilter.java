package pro.fessional.wings.slardar.fastjson.filter;

import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.filter.PropertyPreFilter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * <pre>
 * delta - the default return of this
 * andOr - compose by `and` or `or`
 * </pre>
 *
 * @author trydofor
 * @since 2024-07-27
 */
public class ComposePropertyPreFilter implements PropertyPreFilter {

    private final boolean delta;
    private final boolean andOr;
    private final List<PropertyPreFilter> filters;

    public ComposePropertyPreFilter(boolean andOr, PropertyPreFilter... filters) {
        this(true, andOr, Arrays.asList(filters));
    }

    public ComposePropertyPreFilter(boolean delta, boolean andOr, PropertyPreFilter... filters) {
        this(delta, andOr, Arrays.asList(filters));
    }

    public ComposePropertyPreFilter(boolean andOr, @NotNull Collection<? extends PropertyPreFilter> filters) {
        this(true, andOr, filters);
    }

    @SuppressWarnings("unchecked")
    public ComposePropertyPreFilter(boolean delta, boolean andOr, @NotNull Collection<? extends PropertyPreFilter> filters) {
        this.delta = delta;
        this.andOr = andOr;
        this.filters = filters instanceof List ? (List<PropertyPreFilter>) filters : new ArrayList<>(filters);
    }

    @Override
    public boolean process(JSONWriter writer, Object source, String name) {
        for (PropertyPreFilter ft : filters) {
            boolean b = ft.process(writer, source, name);
            if (andOr) {
                if (!b) return false; // and false
            }
            else {
                if (b) return true; // or true
            }
        }

        return delta;
    }
}
