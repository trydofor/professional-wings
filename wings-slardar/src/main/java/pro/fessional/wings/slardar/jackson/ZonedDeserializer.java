package pro.fessional.wings.slardar.jackson;

import com.fasterxml.jackson.datatype.jsr310.deser.InstantDeserializer;
import pro.fessional.wings.silencer.datetime.TimeZoneDefault;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalQueries;

/**
 * @author trydofor
 * @since 2019-09-01
 */
public class ZonedDeserializer extends InstantDeserializer<ZonedDateTime> {
    public ZonedDeserializer(DateTimeFormatter formatter) {
        super(ZonedDateTime.class,
                formatter,
                temporal -> {
                    ZoneId zoneId = temporal.query(TemporalQueries.zone());
                    if (zoneId == null) zoneId = TimeZoneDefault.ZONE_ID;
                    LocalDate date = LocalDate.from(temporal);
                    LocalTime time = LocalTime.from(temporal);
                    return ZonedDateTime.of(date, time, zoneId);
                },
                a -> ZonedDateTime.ofInstant(Instant.ofEpochMilli(a.value), a.zoneId),
                a -> ZonedDateTime.ofInstant(Instant.ofEpochSecond(a.integer, a.fraction), a.zoneId),
                (zonedDateTime, zoneId) -> zonedDateTime,
                false // keep zero offset and Z separate since zones explicitly supported
        );
    }
}
