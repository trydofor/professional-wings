package pro.fessional.wings.slardar.autozone;

import org.springframework.context.i18n.LocaleContextHolder;
import pro.fessional.mirana.time.DateLocaling;
import pro.fessional.mirana.time.DateParser;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.TimeZone;

/**
 * Request时，自动把用户时间调至系统时区。
 * Response时，自动把系统时间调至用户时区。
 *
 * @author trydofor
 * @since 2022-10-02
 */
public interface AutoZoneAware {

    // request : String to DateTime, Default Client to System
    default LocalDateTime autoLocalRequest(TemporalAccessor tma, AutoZoneType auto) {
        if (auto == AutoZoneType.Off) {
            return tma.query(DateParser.QueryDateTime);
        }

        final ZonedDateTime zdt = autoZonedRequest(tma, auto);
        return zdt.toLocalDateTime();
    }

    default ZonedDateTime autoZonedRequest(TemporalAccessor tma, AutoZoneType auto) {
        // ① tma是用户发出，先调整为Client时间
        final ZoneId utz = LocaleContextHolder.getTimeZone().toZoneId();
        final ZonedDateTime zdt = DateParser.parseZoned(tma, utz);

        // ② 变为System时区
        if (auto == AutoZoneType.Auto || auto == AutoZoneType.System) {
            return zdt.withZoneSameInstant(ZoneId.systemDefault());
        }

        return zdt;
    }

    default OffsetDateTime autoOffsetRequest(TemporalAccessor tma, AutoZoneType auto) {
        final ZonedDateTime zdt = autoZonedRequest(tma, auto);
        return zdt.toOffsetDateTime();
    }

    // response : DateTime to String, Default System to Client
    default LocalDateTime autoLocalResponse(LocalDateTime ldt, AutoZoneType auto) {
        // 假设LocalDateTime都是系统时区
        if (auto == AutoZoneType.Auto || auto == AutoZoneType.Client) {
            final TimeZone tz = LocaleContextHolder.getTimeZone();
            return DateLocaling.useLdt(ldt, tz.toZoneId());
        }

        return ldt;
    }

    default ZonedDateTime autoZonedResponse(ZonedDateTime zdt, AutoZoneType auto) {
        if (auto == AutoZoneType.Auto || auto == AutoZoneType.Client) {
            final TimeZone tz = LocaleContextHolder.getTimeZone();
            return zdt.withZoneSameInstant(tz.toZoneId());
        }
        else if (auto == AutoZoneType.System) {
            return zdt.withZoneSameInstant(ZoneId.systemDefault());
        }

        return zdt;
    }

    default OffsetDateTime autoOffsetResponse(OffsetDateTime odt, AutoZoneType auto) {
        if (auto == AutoZoneType.Auto || auto == AutoZoneType.Client) {
            final TimeZone tz = LocaleContextHolder.getTimeZone();
            return odt.atZoneSameInstant(tz.toZoneId()).toOffsetDateTime();
        }
        else if (auto == AutoZoneType.System) {
            return odt.atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime();
        }

        return odt;
    }
}
