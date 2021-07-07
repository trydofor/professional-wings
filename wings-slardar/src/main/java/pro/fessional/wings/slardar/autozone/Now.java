package pro.fessional.wings.slardar.autozone;

import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.LocaleContextHolder;
import pro.fessional.wings.slardar.context.TerminalContext;

import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * @author trydofor
 * @see pro.fessional.mirana.time.DateLocaling
 * @see pro.fessional.mirana.time.SlideDate
 * @since 2021-04-13
 */
public class Now {

    public static LocalDateTime system() {
        return LocalDateTime.now();
    }

    public static LocalDateTime client() {
        LocaleContext lc = LocaleContextHolder.getLocaleContext();
        final ZoneId zid;
        if (lc == null) {
            zid = TerminalContext.get().getTimeZone().toZoneId();
        }
        else {
            zid = LocaleContextHolder.getTimeZone(lc).toZoneId();
        }
        return LocalDateTime.now(zid);
    }
}
