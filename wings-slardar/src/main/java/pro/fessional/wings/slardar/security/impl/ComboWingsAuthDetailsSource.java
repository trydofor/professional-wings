package pro.fessional.wings.slardar.security.impl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.core.Ordered;
import pro.fessional.mirana.func.Dcl;
import pro.fessional.wings.slardar.security.WingsAuthDetailsSource;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

/**
 * @author trydofor
 * @since 2021-02-17
 */
public class ComboWingsAuthDetailsSource implements WingsAuthDetailsSource<Object> {

    private final List<Combo<?>> combos = new ArrayList<>();
    private final Dcl dclCombos = Dcl.of(() -> combos.sort(Comparator.comparingInt(Combo::getOrder)));

    @Override
    public Object buildDetails(@Nullable Enum<?> authType, @NotNull HttpServletRequest request) {
        dclCombos.runIfDirty();
        for (Combo<?> cb : combos) {
            final Object d = cb.buildDetails(authType, request);
            if (d != null) return d;
        }
        return null;
    }

    public void add(Combo<?> source) {
        if (source == null) return;
        combos.add(source);
        dclCombos.setDirty();
    }

    public void addAll(Collection<? extends Combo<?>> source) {
        if (source == null) return;
        combos.addAll(source);
        dclCombos.setDirty();
    }

    public interface Combo<T> extends Ordered {
        /**
         * 不接受或无法构造返回null
         *
         * @param authType authType
         * @param request  request
         * @return details or null
         */
        T buildDetails(@Nullable Enum<?> authType, @NotNull HttpServletRequest request);
    }
}
