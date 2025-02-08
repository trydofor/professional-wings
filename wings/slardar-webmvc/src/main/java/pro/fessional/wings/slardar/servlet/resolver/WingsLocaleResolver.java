package pro.fessional.wings.slardar.servlet.resolver;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.SimpleTimeZoneAwareLocaleContext;
import org.springframework.context.i18n.TimeZoneAwareLocaleContext;
import org.springframework.web.servlet.i18n.AbstractLocaleContextResolver;
import pro.fessional.mirana.i18n.LocaleResolver;
import pro.fessional.mirana.i18n.ZoneIdResolver;
import pro.fessional.wings.slardar.context.AttributeHolder;
import pro.fessional.wings.slardar.context.SecurityContextUtil;
import pro.fessional.wings.slardar.context.TerminalContext;
import pro.fessional.wings.slardar.security.WingsUserDetails;

import java.time.ZoneId;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

import static pro.fessional.wings.slardar.constants.SlardarServletConst.AttrI18nContext;
import static pro.fessional.wings.slardar.context.TerminalAttribute.LocaleByUid;
import static pro.fessional.wings.slardar.context.TerminalAttribute.ZoneIdByUid;

/**
 * get current Locale and ZoneId in the following order:
 * (1) request `WINGS.I18N_CONTEXT`
 * (2) query string `locale`, `zoneid`
 * (3) http header `Accept-Language`,`Zone-Id`
 * (4) cookie `WINGS_LOCALE`, `WINGS_ZONEID`
 * (5) login user's SecurityContext to get Wings settings
 * (6) system default value
 *
 * @author trydofor
 * @since 2019-06-30
 */
@Getter
public class WingsLocaleResolver extends AbstractLocaleContextResolver {

    private final Set<String> localeParam = new LinkedHashSet<>();
    private final Set<String> localeCookie = new LinkedHashSet<>();
    private final Set<String> localeHeader = new LinkedHashSet<>();
    private final Set<String> zoneidParam = new LinkedHashSet<>();
    private final Set<String> zoneidCookie = new LinkedHashSet<>();
    private final Set<String> zoneidHeader = new LinkedHashSet<>();

    public void addLocaleCookie(Collection<String> keys) {
        localeCookie.addAll(keys);
    }

    public void addLocaleHeader(Collection<String> keys) {
        localeHeader.addAll(keys);
    }

    public void addLocaleParam(Collection<String> keys) {
        localeParam.addAll(keys);
    }

    public void addZoneidCookie(Collection<String> keys) {
        zoneidCookie.addAll(keys);
    }

    public void addZoneidHeader(Collection<String> keys) {
        zoneidHeader.addAll(keys);
    }

    public void addZoneidParam(Collection<String> keys) {
        zoneidParam.addAll(keys);
    }

    @NotNull
    @Override
    public LocaleContext resolveLocaleContext(@NotNull HttpServletRequest request) {
        return resolveI18nContext(request);
    }

    @NotNull
    public TimeZoneAwareLocaleContext resolveI18nContext(HttpServletRequest request) {
        return resolveI18nContext(request, null);
    }

    @NotNull
    public TimeZoneAwareLocaleContext resolveI18nContext(HttpServletRequest request, Long userId) {

        Object obj = request.getAttribute(AttrI18nContext.value);
        if (obj instanceof TimeZoneAwareLocaleContext alc) {
            return alc;
        }

        Locale locale = resolveUserLocale(request);
        TimeZone timeZone = resolveUserTimeZone(request);

        if (locale == null || timeZone == null) {
            final WingsUserDetails details = SecurityContextUtil.getUserDetails(false);

            if (locale == null) {
                if (details == null) {
                    if (userId != null) {
                        locale = AttributeHolder.tryAttr(LocaleByUid, userId, false);
                    }
                }
                else {
                    locale = details.getLocale();
                }

                if (locale == null) {
                    locale = TerminalContext.defaultLocale();
                }
            }

            if (timeZone == null) {
                if (details == null) {
                    if (userId != null) {
                        final ZoneId zid = AttributeHolder.tryAttr(ZoneIdByUid, userId, false);
                        if (zid != null) {
                            timeZone = TimeZone.getTimeZone(zid);
                        }
                    }
                }
                else {
                    timeZone = TimeZone.getTimeZone(details.getZoneId());
                }

                if (timeZone == null) {
                    timeZone = TerminalContext.defaultTimeZone();
                }
            }
        }

        var ctx = new SimpleTimeZoneAwareLocaleContext(locale, timeZone);
        request.setAttribute(AttrI18nContext.value, ctx);

        return ctx;
    }

    @Override
    public void setLocaleContext(@NotNull HttpServletRequest request, HttpServletResponse response, LocaleContext context) {
        if (context instanceof TimeZoneAwareLocaleContext ctx) {
            request.setAttribute(AttrI18nContext.value, ctx);
            return;
        }

        Locale locale = context.getLocale();
        if (locale == null) {
            locale = resolveUserLocale(request);
        }
        if (locale == null) {
            locale = TerminalContext.defaultLocale();
        }

        TimeZone timeZone = resolveUserTimeZone(request);
        if (timeZone == null) {
            timeZone = TerminalContext.defaultTimeZone();
        }

        var ctx = new SimpleTimeZoneAwareLocaleContext(locale, timeZone);
        request.setAttribute(AttrI18nContext.value, ctx);
    }

    // /////////////////
    private TimeZone resolveUserTimeZone(HttpServletRequest request) {

        for (String s : zoneidParam) {
            String q = request.getParameter(s);
            if (q != null && !q.isEmpty()) {
                return ZoneIdResolver.timeZone(q);
            }
        }

        for (String s : zoneidHeader) {
            String h = request.getHeader(s);
            if (h != null && !h.isEmpty()) {
                return ZoneIdResolver.timeZone(h);
            }
        }

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (String s : zoneidCookie) {
                for (Cookie c : cookies) {
                    if (c.getName().equalsIgnoreCase(s)) {
                        return ZoneIdResolver.timeZone(c.getValue());
                    }
                }
            }
        }

        return null;
    }

    private Locale resolveUserLocale(HttpServletRequest request) {

        for (String s : localeParam) {
            String q = request.getParameter(s);
            if (q != null && !q.isEmpty()) {
                return LocaleResolver.locale(q);
            }
        }

        for (String s : localeHeader) {
            String h = request.getHeader(s);
            if (h != null && !h.isEmpty()) {
                return LocaleResolver.locale(h);
            }
        }

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (String s : localeCookie) {
                for (Cookie c : cookies) {
                    if (c.getName().equalsIgnoreCase(s)) {
                        return LocaleResolver.locale(c.getValue());
                    }
                }
            }
        }

        return null;
    }
}
