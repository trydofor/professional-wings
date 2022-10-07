package pro.fessional.wings.slardar.autozone;

import org.jetbrains.annotations.NotNull;
import pro.fessional.mirana.time.DateLocaling;
import pro.fessional.mirana.time.DateParser;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.function.Supplier;

/**
 * @author trydofor
 * @since 2022-10-05
 */
public class AutoZoneUtil {

    // request : String to DateTime, Default Client to System
    @NotNull
    public static LocalDateTime autoLocalRequest(@NotNull TemporalAccessor dateTime, @NotNull AutoZoneType autoType, @NotNull Supplier<ZoneId> client) {
        if (autoType == AutoZoneType.Off) {
            return dateTime.query(DateParser.QueryDateTime);
        }

        final ZonedDateTime zdt = autoZonedRequest(dateTime, autoType, client);
        return zdt.toLocalDateTime();
    }

    @NotNull
    public static ZonedDateTime autoZonedRequest(@NotNull TemporalAccessor dateTime, @NotNull AutoZoneType autoType, @NotNull Supplier<ZoneId> client) {
        // ① tma是用户发出，先调整为Client时间
        final ZonedDateTime zdt = DateParser.parseZoned(dateTime, client.get());

        // ② 变为System时区
        if (autoType == AutoZoneType.Auto || autoType == AutoZoneType.System) {
            return zdt.withZoneSameInstant(ZoneId.systemDefault());
        }

        return zdt;
    }

    @NotNull
    public static OffsetDateTime autoOffsetRequest(@NotNull TemporalAccessor dateTime, @NotNull AutoZoneType autoType, @NotNull Supplier<ZoneId> client) {
        final ZonedDateTime zdt = autoZonedRequest(dateTime, autoType, client);
        return zdt.toOffsetDateTime();
    }

    // response : DateTime to String, Default System to Client
    @NotNull
    public static LocalDateTime autoLocalResponse(@NotNull LocalDateTime dateTime, @NotNull AutoZoneType autoType, @NotNull Supplier<ZoneId> client) {
        // 假设LocalDateTime都是系统时区
        if (autoType == AutoZoneType.Auto || autoType == AutoZoneType.Client) {
            return DateLocaling.useLdt(dateTime, client.get());
        }

        return dateTime;
    }

    @NotNull
    public static ZonedDateTime autoZonedResponse(@NotNull ZonedDateTime dateTime, @NotNull AutoZoneType autoType, @NotNull Supplier<ZoneId> client) {
        if (autoType == AutoZoneType.Auto || autoType == AutoZoneType.Client) {
            return dateTime.withZoneSameInstant(client.get());
        }
        else if (autoType == AutoZoneType.System) {
            return dateTime.withZoneSameInstant(ZoneId.systemDefault());
        }

        return dateTime;
    }

    @NotNull
    public static OffsetDateTime autoOffsetResponse(@NotNull OffsetDateTime dateTime, @NotNull AutoZoneType autoType, @NotNull Supplier<ZoneId> client) {
        if (autoType == AutoZoneType.Auto || autoType == AutoZoneType.Client) {
            return dateTime.atZoneSameInstant(client.get()).toOffsetDateTime();
        }
        else if (autoType == AutoZoneType.System) {
            return dateTime.atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime();
        }

        return dateTime;
    }
}
