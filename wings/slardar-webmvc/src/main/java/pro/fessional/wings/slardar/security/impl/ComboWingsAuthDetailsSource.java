package pro.fessional.wings.slardar.security.impl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.LocaleResolver;
import pro.fessional.mirana.func.Dcl;
import pro.fessional.wings.slardar.security.WingsAuthDetails;
import pro.fessional.wings.slardar.security.WingsAuthDetailsSource;
import pro.fessional.wings.slardar.security.WingsAuthHelper;
import pro.fessional.wings.slardar.servlet.resolver.WingsRemoteResolver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * build auth details and set locale
 *
 * @author trydofor
 * @since 2021-02-17
 */
public class ComboWingsAuthDetailsSource implements WingsAuthDetailsSource<WingsAuthDetails> {

    protected final List<Combo<?>> combos = new ArrayList<>();
    protected final Dcl<Void> dclCombos = Dcl.of(() -> combos.sort(Comparator.comparingInt(Combo::getOrder)));

    @Setter @Getter
    protected Set<String> ignoredMetaKey = Collections.emptySet();

    @Setter @Getter
    protected WingsRemoteResolver wingsRemoteResolver = null;

    @Setter @Getter
    protected LocaleResolver localeResolver;

    @Override
    public WingsAuthDetails buildDetails(@NotNull Enum<?> authType, @NotNull HttpServletRequest request) {
        dclCombos.runIfDirty();

        // set correct locale for exception
        if (localeResolver != null) {
            final Locale locale = localeResolver.resolveLocale(request);
            LocaleContextHolder.setLocale(locale);
        }

        WingsAuthDetails detail = null;
        for (Combo<?> cb : combos) {
            detail = cb.buildDetails(authType, request);
            if (detail != null) break;
        }
        if (detail == null) {
            detail = new DefaultWingsAuthDetails();
        }

        buildMetaData(authType, request, detail);

        return detail;
    }

    public final void add(Combo<?> source) {
        if (source == null) return;
        combos.add(source);
        dclCombos.setDirty();
    }

    public final void addAll(Collection<? extends Combo<?>> source) {
        if (source == null) return;
        combos.addAll(source);
        dclCombos.setDirty();
    }

    protected void buildMetaData(@NotNull Enum<?> authType, @NotNull HttpServletRequest request, @NotNull WingsAuthDetails details) {
        final Map<String, String> meta = details.getMetaData();
        final String zone = WingsAuthHelper.getAuthZoneAttribute(request);
        meta.put(WingsAuthHelper.AuthType, authType.name());
        if (zone != null) {
            meta.putIfAbsent(WingsAuthHelper.AuthZone, zone);
        }
        if (wingsRemoteResolver != null) {
            meta.put(WingsAuthHelper.AuthAddr, wingsRemoteResolver.resolveRemoteIp(request));
            meta.put(WingsAuthHelper.AuthAgent, wingsRemoteResolver.resolveAgentInfo(request));
        }

        final Enumeration<String> names = request.getParameterNames();
        while (names.hasMoreElements()) {
            final String n = names.nextElement();
            if (!ignoredMetaKey.contains(n)) {
                meta.putIfAbsent(n, request.getParameter(n));
            }
        }
    }

    public interface Combo<T extends WingsAuthDetails> extends Ordered {
        /**
         * Return null if not accepted or cant be constructed
         *
         * @param authType authType
         * @param request  request
         * @return details or null
         */
        T buildDetails(@NotNull Enum<?> authType, @NotNull HttpServletRequest request);
    }
}
