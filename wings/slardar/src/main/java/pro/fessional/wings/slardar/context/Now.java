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

/**
 * @author trydofor
 * @since 2022-10-10
 */
public class Now extends ThreadNow {
    @NotNull
    public static Clock clientClock() {
        return clock(LocaleZoneIdUtil.ZoneIdNonnull());
    }

    @NotNull
    public static Date clientUtilDate() {
        return utilDate(LocaleZoneIdUtil.ZoneIdNonnull());
    }

    @NotNull
    public static LocalDate clientLocalDate() {
        return localDate(LocaleZoneIdUtil.ZoneIdNonnull());
    }

    @NotNull
    public static LocalTime clientLocalTime() {
        return localTime(LocaleZoneIdUtil.ZoneIdNonnull());
    }

    @NotNull
    public static LocalDateTime clientLocalDateTime() {
        return localDateTime(LocaleZoneIdUtil.ZoneIdNonnull());
    }

    @NotNull
    public static ZonedDateTime clientZonedDateTime() {
        return zonedDateTime(LocaleZoneIdUtil.ZoneIdNonnull());
    }

    @NotNull
    public static OffsetDateTime clientOffsetDateTime() {
        return offsetDateTime(LocaleZoneIdUtil.ZoneIdNonnull());
    }

    @NotNull
    public static Instant clientInstant() {
        return instant(LocaleZoneIdUtil.ZoneIdNonnull());
    }
}
