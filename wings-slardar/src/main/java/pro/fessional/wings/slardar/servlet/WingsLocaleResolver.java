package pro.fessional.wings.slardar.servlet;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.TimeZoneAwareLocaleContext;
import org.springframework.web.servlet.i18n.AbstractLocaleContextResolver;
import pro.fessional.mirana.i18n.LocaleResolver;
import pro.fessional.mirana.i18n.ZoneIdResolver;
import pro.fessional.wings.silencer.context.WingsI18nContext;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.ZoneId;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import static java.util.Collections.emptyList;

/**
 * @author trydofor
 * @since 2019-06-30
 */
@RequiredArgsConstructor
public class WingsLocaleResolver extends AbstractLocaleContextResolver {

    public static final String CONTEXT_KEY = "WINGS.I18N_CONTEXT";

    private final Config config;

    @NotNull
    @Override
    public LocaleContext resolveLocaleContext(@NotNull HttpServletRequest request) {
        return (LocaleContext) resolveI18nContext(request);
    }

    public WingsI18nContext resolveI18nContext(HttpServletRequest request) {

        Object obj = request.getAttribute(CONTEXT_KEY);
        if (obj instanceof WingsI18nContext) {
            return (WingsI18nContext) obj;
        }

        final Locale locale = resolveUserLocale(request);
        final TimeZone timeZone = resolveUserTimeZone(request);
        final ZoneId zoneId = timeZone.toZoneId();

        Context context = new Context(locale, timeZone, zoneId);
        request.setAttribute(CONTEXT_KEY, context);

        return context;
    }

    @Override
    public void setLocaleContext(@NotNull HttpServletRequest request, HttpServletResponse response, LocaleContext context) {

        if (context instanceof WingsI18nContext && context instanceof TimeZoneAwareLocaleContext) {
            request.setAttribute(CONTEXT_KEY, context);
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

        context = new Context(locale, timeZone, timeZone.toZoneId());
        request.setAttribute(CONTEXT_KEY, context);
    }

    // /////////////////
    private TimeZone resolveUserTimeZone(HttpServletRequest request) {

        for (String s : config.getZoneidParam()) {
            String q = request.getParameter(s);
            if (q != null && !q.isEmpty()) {
                return ZoneIdResolver.timeZone(q);
            }
        }

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (String s : config.getZoneidCookie()) {
                for (Cookie c : cookies) {
                    if (c.getName().equalsIgnoreCase(s)) {
                        return ZoneIdResolver.timeZone(c.getValue());
                    }
                }
            }
        }

        for (String s : config.getZoneidParam()) {
            String h = request.getHeader(s);
            if (h != null && !h.isEmpty()) {
                return ZoneIdResolver.timeZone(h);
            }
        }

        return TimeZone.getDefault();
    }

    private Locale resolveUserLocale(HttpServletRequest request) {

        for (String s : config.getLocaleParam()) {
            String q = request.getParameter(s);
            if (q != null && !q.isEmpty()) {
                return LocaleResolver.locale(q);
            }
        }

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (String s : config.getLocaleCookie()) {
                for (Cookie c : cookies) {
                    if (c.getName().equalsIgnoreCase(s)) {
                        return LocaleResolver.locale(c.getValue());
                    }
                }
            }
        }

        for (String s : config.getLocaleParam()) {
            String h = request.getHeader(s);
            if (h != null && !h.isEmpty()) {
                return LocaleResolver.locale(h);
            }
        }

        return Locale.getDefault();
    }

    @Data
    public static class Config {
        private List<String> localeParam = emptyList();
        private List<String> localeCookie = emptyList();
        private List<String> localeHeader = emptyList();
        private List<String> zoneidParam = emptyList();
        private List<String> zoneidCookie = emptyList();
        private List<String> zoneidHeader = emptyList();
    }

    public static class Context implements TimeZoneAwareLocaleContext, WingsI18nContext {

        private final Locale locale;
        private final TimeZone timeZone;
        private final ZoneId zoneId;

        public Context(Locale locale, TimeZone timeZone, ZoneId zoneId) {
            this.locale = locale;
            this.timeZone = timeZone;
            this.zoneId = zoneId;
        }

        @Override
        public TimeZone getTimeZone() {
            return timeZone;
        }

        @Override
        public Locale getLocale() {
            return locale;
        }

        @Override
        public @NotNull
        ZoneId getZoneId() {
            return zoneId;
        }
    }
}