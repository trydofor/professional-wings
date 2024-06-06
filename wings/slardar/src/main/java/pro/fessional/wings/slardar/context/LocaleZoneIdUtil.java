package pro.fessional.wings.slardar.context;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.context.i18n.LocaleContextHolder;

import java.time.ZoneId;
import java.util.Locale;

/**
 * @author trydofor
 * @since 2022-10-09
 */
public class LocaleZoneIdUtil {

    /**
     * Get the ZoneId of the current thread with priority logined TerminalContext, LocaleContextHolder
     */
    @Nullable
    public static ZoneId ZoneIdNullable() {
        final TerminalContext.Context ctx = TerminalContext.get(false);
        if (!ctx.isNull()) {
            return ctx.getZoneId();
        }
        return LocaleContextHolder.getTimeZone().toZoneId();
    }


    /**
     * Get the Locale of the current thread with priority logined TerminalContext, LocaleContextHolder
     */
    @Nullable
    public static Locale LocaleNullable() {
        final TerminalContext.Context ctx = TerminalContext.get(false);
        if (!ctx.isNull()) {
            return ctx.getLocale();
        }
        return LocaleContextHolder.getLocale();
    }

    /**
     * Get the ZoneId of the current thread with priority logined TerminalContext, LocaleContextHolder, systemDefault
     */
    @NotNull
    public static ZoneId ZoneIdNonnull() {
        final ZoneId zid = ZoneIdNullable();
        return zid != null ? zid : TerminalContext.defaultZoneId();
    }

    /**
     * Get the Locale of the current thread with priority logined TerminalContext, LocaleContextHolder, systemDefault
     */
    @NotNull
    public static Locale LocaleNonnull() {
        final Locale lcl = LocaleNullable();
        return lcl != null ? lcl : TerminalContext.defaultLocale();
    }
}
