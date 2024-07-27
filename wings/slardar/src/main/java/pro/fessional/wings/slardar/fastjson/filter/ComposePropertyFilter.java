package pro.fessional.wings.slardar.fastjson.filter;

import com.alibaba.fastjson2.filter.PropertyFilter;
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
public class ComposePropertyFilter implements PropertyFilter {

    private final boolean delta;
    private final boolean andOr;
    private final List<PropertyFilter> filters;

    public ComposePropertyFilter(boolean andOr, PropertyFilter... filters) {
        this(true, andOr, Arrays.asList(filters));
    }

    public ComposePropertyFilter(boolean delta, boolean andOr, PropertyFilter... filters) {
        this(delta, andOr, Arrays.asList(filters));
    }

    public ComposePropertyFilter(boolean andOr, @NotNull Collection<? extends PropertyFilter> filters) {
        this(true, andOr, filters);
    }

    @SuppressWarnings("unchecked")
    public ComposePropertyFilter(boolean delta, boolean andOr, @NotNull Collection<? extends PropertyFilter> filters) {
        this.delta = delta;
        this.andOr = andOr;
        this.filters = filters instanceof List ? (List<PropertyFilter>) filters : new ArrayList<>(filters);
    }

    @Override
    public boolean apply(Object object, String name, Object value) {
        for (PropertyFilter ft : filters) {
            boolean b = ft.apply(object, name, value);
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
