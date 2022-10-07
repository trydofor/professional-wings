package pro.fessional.wings.slardar.autozone;

import org.jetbrains.annotations.NotNull;
import pro.fessional.wings.slardar.autodto.AutoDtoHelper;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAccessor;

/**
 * Request时，自动把用户时间调至系统时区。
 * Response时，自动把系统时间调至用户时区。
 *
 * @author trydofor
 * @since 2022-10-02
 */
public interface AutoZoneAware {

    // request : String to DateTime, Default Client to System
    @NotNull
    default LocalDateTime autoLocalRequest(@NotNull TemporalAccessor dateTime, @NotNull AutoZoneType autoType) {
        return AutoZoneUtil.autoLocalRequest(dateTime, autoType, AutoDtoHelper.ZoneIdSupplier);
    }

    @NotNull
    default ZonedDateTime autoZonedRequest(@NotNull TemporalAccessor dateTime, @NotNull AutoZoneType autoType) {
        return AutoZoneUtil.autoZonedRequest(dateTime, autoType, AutoDtoHelper.ZoneIdSupplier);
    }

    @NotNull
    default OffsetDateTime autoOffsetRequest(@NotNull TemporalAccessor dateTime, @NotNull AutoZoneType autoType) {
        return AutoZoneUtil.autoOffsetRequest(dateTime, autoType, AutoDtoHelper.ZoneIdSupplier);
    }

    // response : DateTime to String, Default System to Client
    @NotNull
    default LocalDateTime autoLocalResponse(@NotNull LocalDateTime dateTime, @NotNull AutoZoneType autoType) {
        return AutoZoneUtil.autoLocalResponse(dateTime, autoType, AutoDtoHelper.ZoneIdSupplier);
    }

    @NotNull
    default ZonedDateTime autoZonedResponse(@NotNull ZonedDateTime dateTime, @NotNull AutoZoneType autoType) {
        return AutoZoneUtil.autoZonedResponse(dateTime, autoType, AutoDtoHelper.ZoneIdSupplier);
    }

    @NotNull
    default OffsetDateTime autoOffsetResponse(@NotNull OffsetDateTime dateTime, @NotNull AutoZoneType autoType) {
        return AutoZoneUtil.autoOffsetResponse(dateTime, autoType, AutoDtoHelper.ZoneIdSupplier);
    }
}
