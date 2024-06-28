package pro.fessional.wings.slardar.context;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
        return ctx.isNull() ? null: ctx.getZoneId();
    }


    /**
     * Get the Locale of the current thread with priority logined TerminalContext, LocaleContextHolder
     */
    @Nullable
    public static Locale LocaleNullable() {
        final TerminalContext.Context ctx = TerminalContext.get(false);
        return ctx.isNull() ? null: ctx.getLocale();
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
