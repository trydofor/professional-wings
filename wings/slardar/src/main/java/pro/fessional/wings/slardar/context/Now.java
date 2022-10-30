package pro.fessional.wings.slardar.context;

import org.jetbrains.annotations.NotNull;
import pro.fessional.mirana.time.ThreadNow;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.Date;

import static pro.fessional.wings.slardar.context.LocaleZoneIdUtil.ZoneIdNonnull;

/**
 * @author trydofor
 * @since 2022-10-10
 */
public class Now extends ThreadNow {
    @NotNull
    public static Clock clientClock() {
        return clock(ZoneIdNonnull.get());
    }

    @NotNull
    public static Date clientUtilDate() {
        return utilDate(ZoneIdNonnull.get());
    }

    @NotNull
    public static LocalDate clientLocalDate() {
        return localDate(ZoneIdNonnull.get());
    }

    @NotNull
    public static LocalTime clientLocalTime() {
        return localTime(ZoneIdNonnull.get());
    }

    @NotNull
    public static LocalDateTime clientLocalDateTime() {
        return localDateTime(ZoneIdNonnull.get());
    }

    @NotNull
    public static ZonedDateTime clientZonedDateTime() {
        return zonedDateTime(ZoneIdNonnull.get());
    }

    @NotNull
    public static OffsetDateTime clientOffsetDateTime() {
        return offsetDateTime(ZoneIdNonnull.get());
    }

    @NotNull
    public static Instant clientInstant() {
        return instant(ZoneIdNonnull.get());
    }
}
