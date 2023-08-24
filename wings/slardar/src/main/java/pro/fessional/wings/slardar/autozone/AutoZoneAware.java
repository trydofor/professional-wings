package pro.fessional.wings.slardar.autozone;

import org.jetbrains.annotations.NotNull;
import pro.fessional.wings.slardar.context.LocaleZoneIdUtil;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAccessor;

/**
 * When request, auto convert timezone from client to system.
 * When response, auto convert timezone from system to client
 *
 * @author trydofor
 * @since 2022-10-02
 */
public interface AutoZoneAware {

    // request : String to DateTime, Default Client to System
    @NotNull
    default LocalDateTime autoLocalRequest(@NotNull TemporalAccessor dateTime, @NotNull AutoZoneType autoType) {
        return AutoZoneUtil.autoLocalRequest(dateTime, autoType, LocaleZoneIdUtil.ZoneIdNonnull);
    }

    @NotNull
    default ZonedDateTime autoZonedRequest(@NotNull TemporalAccessor dateTime, @NotNull AutoZoneType autoType) {
        return AutoZoneUtil.autoZonedRequest(dateTime, autoType, LocaleZoneIdUtil.ZoneIdNonnull);
    }

    @NotNull
    default OffsetDateTime autoOffsetRequest(@NotNull TemporalAccessor dateTime, @NotNull AutoZoneType autoType) {
        return AutoZoneUtil.autoOffsetRequest(dateTime, autoType, LocaleZoneIdUtil.ZoneIdNonnull);
    }

    // response : DateTime to String, Default System to Client
    @NotNull
    default LocalDateTime autoLocalResponse(@NotNull LocalDateTime dateTime, @NotNull AutoZoneType autoType) {
        return AutoZoneUtil.autoLocalResponse(dateTime, autoType, LocaleZoneIdUtil.ZoneIdNonnull);
    }

    @NotNull
    default ZonedDateTime autoZonedResponse(@NotNull ZonedDateTime dateTime, @NotNull AutoZoneType autoType) {
        return AutoZoneUtil.autoZonedResponse(dateTime, autoType, LocaleZoneIdUtil.ZoneIdNonnull);
    }

    @NotNull
    default OffsetDateTime autoOffsetResponse(@NotNull OffsetDateTime dateTime, @NotNull AutoZoneType autoType) {
        return AutoZoneUtil.autoOffsetResponse(dateTime, autoType, LocaleZoneIdUtil.ZoneIdNonnull);
    }
}
