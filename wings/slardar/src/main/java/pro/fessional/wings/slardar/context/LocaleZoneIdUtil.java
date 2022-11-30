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
     * 获得当前线程的ZoneId，优先级为TerminalContext，LocaleContextHolder
     */
    public static final Supplier<ZoneId> ZoneIdNullable = () -> {
        final ZoneId zid;
        if (TerminalContext.isActive()) {
            zid = TerminalContext.get(false).getZoneId();
        }
        else {
            zid = LocaleContextHolder.getTimeZone().toZoneId();
        }
        return zid;
    };

    /**
     * 获得当前线程的Locale，优先级为TerminalContext，LocaleContextHolder
     */
    public static final Supplier<Locale> LocaleNullable = () -> {
        final Locale lcl;
        if (TerminalContext.isActive()) {
            lcl = TerminalContext.get(false).getLocale();
        }
        else {
            lcl = LocaleContextHolder.getLocale();
        }
        return lcl;
    };

    /**
     * 获得当前线程的ZoneId，优先级为TerminalContext，LocaleContextHolder，systemDefault
     */
    public static final Supplier<ZoneId> ZoneIdNonnull = () -> {
        final ZoneId zid = ZoneIdNullable.get();
        return zid != null ? zid : TerminalContext.defaultZoneId();
    };

    /**
     * 获得当前线程的Locale，优先级为TerminalContext，LocaleContextHolder，systemDefault
     */
    public static final Supplier<Locale> LocaleNonnull = () -> {
        final Locale lcl = LocaleNullable.get();
        return lcl != null ? lcl : TerminalContext.defaultLocale();
    };
}
