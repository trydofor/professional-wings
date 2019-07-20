package pro.fessional.wings.slardar.spring.help;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.TimeZoneAwareLocaleContext;
import pro.fessional.wings.silencer.context.WingsI18nContext;
import pro.fessional.wings.slardar.http.TypedRequestUtil;

import javax.servlet.http.HttpServletRequest;
import java.time.ZoneId;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @author trydofor
 * @since 2019-07-03
 */
public class WingsI18nWebUtil {
    private WingsI18nWebUtil() {
    }

    public static final String LOCALE_CONTEXT = "WINGS.I18N_CONTEXT";

    @Nullable
    public static WingsI18nContext getI18nContext(@NotNull HttpServletRequest request) {
        return TypedRequestUtil.getAttribute(request, LOCALE_CONTEXT, WingsI18nContext.class);
    }

    @Nullable
    public static LocaleContext getLocaleContext(@NotNull HttpServletRequest request) {
        return TypedRequestUtil.getAttribute(request, LOCALE_CONTEXT, LocaleContext.class);
    }

    @NotNull
    public static WingsI18nContext getI18nContextOrDefault(@NotNull HttpServletRequest request) {
        WingsI18nContext context = TypedRequestUtil.getAttribute(request, LOCALE_CONTEXT, WingsI18nContext.class);
        if (context == null) {
            context = new Context(Locale.getDefault(), TimeZone.getDefault(), ZoneId.systemDefault());
        }
        return context;
    }

    @NotNull
    public static LocaleContext getLocaleContextOrDefault(@NotNull HttpServletRequest request) {
        LocaleContext context = TypedRequestUtil.getAttribute(request, LOCALE_CONTEXT, LocaleContext.class);
        if (context == null) {
            context = new Context(Locale.getDefault(), TimeZone.getDefault(), ZoneId.systemDefault());
        }
        return context;
    }

    @NotNull
    public static Context putI18nContext(@NotNull HttpServletRequest request, @NotNull Locale locale, @NotNull TimeZone timeZone) {
        return putI18nContext(request, locale, timeZone, timeZone.toZoneId());
    }

    @NotNull
    public static Context putI18nContext(@NotNull HttpServletRequest request, @NotNull Locale locale, @NotNull ZoneId zoneId) {
        return putI18nContext(request, locale, TimeZone.getTimeZone(zoneId), zoneId);
    }

    @NotNull
    public static Context putI18nContext(@NotNull HttpServletRequest request, @NotNull Locale locale, @NotNull TimeZone timeZone, @NotNull ZoneId zoneId) {
        Context context = new Context(locale, timeZone, zoneId);
        return putI18nContext(request, context);
    }

    @NotNull
    public static <T extends WingsI18nContext & TimeZoneAwareLocaleContext> T putI18nContext(@NotNull HttpServletRequest request, @NotNull T context) {
        request.setAttribute(LOCALE_CONTEXT, context);
        return context;
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
        public @NotNull ZoneId getZoneId() {
            return zoneId;
        }
    }
}
