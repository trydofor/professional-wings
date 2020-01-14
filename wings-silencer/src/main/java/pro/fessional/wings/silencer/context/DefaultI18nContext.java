package pro.fessional.wings.silencer.context;

import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.TimeZone;

/**
 * 利用threadlocal做Context
 *
 * @author trydofor
 * @since 2019-09-19
 */
public class DefaultI18nContext implements WingsI18nContext {

    private final ThreadLocal<Locale> locale = new ThreadLocal<>();
    private final ThreadLocal<TimeZone> timezone = new ThreadLocal<>();

    @Nullable
    @Override
    public Locale getLocale() {
        return locale.get();
    }

    @Nullable
    @Override
    public TimeZone getTimeZone() {
        return timezone.get();
    }

    @Override
    public void setLocale(Locale locale) {
        if (locale == null) {
            this.locale.remove();
        } else {
            this.locale.set(locale);
        }
    }

    @Override
    public void setTimeZone(TimeZone timeZone) {
        if (timeZone == null) {
            this.timezone.remove();
        } else {
            this.timezone.set(timeZone);
        }
    }
}
