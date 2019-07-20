package pro.fessional.wings.slardar.spring.help;

import lombok.RequiredArgsConstructor;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.TimeZoneAwareLocaleContext;
import org.springframework.web.servlet.i18n.AbstractLocaleContextResolver;
import pro.fessional.mirana.i18n.LocaleResolver;
import pro.fessional.mirana.i18n.ZoneIdResolver;
import pro.fessional.wings.silencer.context.WingsI18nContext;
import pro.fessional.wings.slardar.spring.conf.WingsI18nResolverProperties;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.ZoneId;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @author trydofor
 * @since 2019-06-30
 */
@RequiredArgsConstructor
public class WingsI18nWebResolver extends AbstractLocaleContextResolver {

    private final WingsI18nResolverProperties properties;

    @Override
    public LocaleContext resolveLocaleContext(HttpServletRequest request) {

        LocaleContext context = WingsI18nWebUtil.getLocaleContext(request);
        if (context != null) {
            return context;
        }

        final Locale locale = resolveUserLocale(request);
        final TimeZone timeZone = resolveUserTimeZone(request);
        final ZoneId zoneId = timeZone.toZoneId();

        return WingsI18nWebUtil.putI18nContext(request, locale, timeZone, zoneId);
    }

    @Override
    public void setLocaleContext(HttpServletRequest request, HttpServletResponse response, LocaleContext context) {

        if (context instanceof WingsI18nContext && context instanceof TimeZoneAwareLocaleContext) {
            WingsI18nWebUtil.putI18nContext(request, (WingsI18nContext & TimeZoneAwareLocaleContext) context);
            return;
        }

        Locale locale = context.getLocale();
        TimeZone timeZone = null;
        if (context instanceof TimeZoneAwareLocaleContext) {
            TimeZoneAwareLocaleContext tc = (TimeZoneAwareLocaleContext) context;
            timeZone = tc.getTimeZone();
        }
        if (timeZone == null && context instanceof WingsI18nContext) {
            WingsI18nContext ic = (WingsI18nContext) context;
            timeZone = ic.getTimeZone();
        }

        if (locale == null) {
            locale = Locale.getDefault();
        }
        if (timeZone == null) {
            timeZone = TimeZone.getDefault();
        }
        WingsI18nWebUtil.putI18nContext(request, locale, timeZone);
    }

    // /////////////////
    private TimeZone resolveUserTimeZone(HttpServletRequest request) {

        for (String s : properties.getZoneidParam()) {
            String q = request.getParameter(s);
            if (q != null && !q.isEmpty()) {
                return ZoneIdResolver.timeZone(q);
            }
        }

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (String s : properties.getZoneidCookie()) {
                for (Cookie c : cookies) {
                    if (c.getName().equalsIgnoreCase(s)) {
                        return ZoneIdResolver.timeZone(c.getValue());
                    }
                }
            }
        }

        for (String s : properties.getZoneidParam()) {
            String h = request.getHeader(s);
            if (h != null && !h.isEmpty()) {
                return ZoneIdResolver.timeZone(h);
            }
        }

        return TimeZone.getDefault();
    }

    private Locale resolveUserLocale(HttpServletRequest request) {

        for (String s : properties.getLocaleParam()) {
            String q = request.getParameter(s);
            if (q != null && !q.isEmpty()) {
                return LocaleResolver.locale(q);
            }
        }

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (String s : properties.getLocaleCookie()) {
                for (Cookie c : cookies) {
                    if (c.getName().equalsIgnoreCase(s)) {
                        return LocaleResolver.locale(c.getValue());
                    }
                }
            }
        }

        for (String s : properties.getLocaleParam()) {
            String h = request.getHeader(s);
            if (h != null && !h.isEmpty()) {
                return LocaleResolver.locale(h);
            }
        }

        return Locale.getDefault();
    }
}