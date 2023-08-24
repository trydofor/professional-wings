package pro.fessional.wings.slardar.context;

import org.springframework.context.i18n.LocaleContextHolder;

import java.time.ZoneId;
import java.util.Locale;
import java.util.function.Supplier;

/**
 * @author trydofor
 * @since 2022-10-09
 */
public class LocaleZoneIdUtil {

    /**
     * Get the ZoneId of the current thread with priority logined TerminalContext, LocaleContextHolder
     */
    public static final Supplier<ZoneId> ZoneIdNullable = () -> {
        final TerminalContext.Context ctx = TerminalContext.get(false);
        if (!ctx.isNull()) {
            return ctx.getZoneId();
        }
        return LocaleContextHolder.getTimeZone().toZoneId();
    };

    /**
     * Get the Locale of the current thread with priority logined TerminalContext, LocaleContextHolder
     */
    public static final Supplier<Locale> LocaleNullable = () -> {
        final TerminalContext.Context ctx = TerminalContext.get(false);
        if (!ctx.isNull()) {
            return ctx.getLocale();
        }
        return LocaleContextHolder.getLocale();
    };

    /**
     * Get the ZoneId of the current thread with priority logined TerminalContext, LocaleContextHolder, systemDefault
     */
    public static final Supplier<ZoneId> ZoneIdNonnull = () -> {
        final ZoneId zid = ZoneIdNullable.get();
        return zid != null ? zid : TerminalContext.defaultZoneId();
    };

    /**
     * Get the Locale of the current thread with priority logined TerminalContext, LocaleContextHolder, systemDefault
     */
    public static final Supplier<Locale> LocaleNonnull = () -> {
        final Locale lcl = LocaleNullable.get();
        return lcl != null ? lcl : TerminalContext.defaultLocale();
    };
}
