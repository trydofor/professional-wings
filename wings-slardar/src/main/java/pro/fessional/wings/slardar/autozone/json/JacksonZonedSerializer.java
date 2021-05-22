package pro.fessional.wings.slardar.autozone.json;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer;
import org.springframework.context.i18n.LocaleContextHolder;
import pro.fessional.mirana.time.DateLocaling;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

/**
 * @author trydofor
 * @see JavaTimeModule
 * @since 2021-03-18
 */
public class JacksonZonedSerializer extends ZonedDateTimeSerializer {

    public static DateTimeFormatter globalDefault = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    // has no default (no arg) constructor
    public JacksonZonedSerializer() {
        super(globalDefault);
    }

    public JacksonZonedSerializer(DateTimeFormatter formatter) {
        super(formatter);
    }

    public JacksonZonedSerializer(ZonedDateTimeSerializer base, Boolean useTimestamp, DateTimeFormatter formatter, Boolean writeZoneId) {
        super(base, useTimestamp, formatter, writeZoneId);
    }

    public JacksonZonedSerializer(ZonedDateTimeSerializer base, Boolean useTimestamp, Boolean useNanoseconds, DateTimeFormatter formatter, Boolean writeZoneId) {
        super(base, useTimestamp, useNanoseconds, formatter, writeZoneId);
    }

    @Override
    public void serialize(ZonedDateTime value, JsonGenerator g, SerializerProvider provider) throws IOException {
        final TimeZone tz = LocaleContextHolder.getTimeZone();
        value = DateLocaling.zoneZone(value, tz.toZoneId());
        super.serialize(value, g, provider);
    }

    @Override
    protected ZonedDateTimeSerializer withFormat(Boolean useTimestamp, DateTimeFormatter formatter, JsonFormat.Shape shape) {
        return new JacksonZonedSerializer(this, useTimestamp, formatter, _writeZoneId);
    }

    @Override
    protected ZonedDateTimeSerializer withFeatures(Boolean writeZoneId, Boolean writeNanoseconds) {
        return new JacksonZonedSerializer(this, _useTimestamp, writeNanoseconds, _formatter, writeZoneId);
    }
}
