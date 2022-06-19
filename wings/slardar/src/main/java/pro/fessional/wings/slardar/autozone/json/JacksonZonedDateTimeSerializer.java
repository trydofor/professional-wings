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
public class JacksonZonedDateTimeSerializer extends ZonedDateTimeSerializer {

    public static DateTimeFormatter defaultFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
    public static boolean defaultAutoZone = false;
    private final boolean autoZone;

    // has no default (no arg) constructor
    public JacksonZonedDateTimeSerializer() {
        super(defaultFormatter);
        this.autoZone = defaultAutoZone;
    }

    public JacksonZonedDateTimeSerializer(DateTimeFormatter formatter, boolean auto) {
        super(formatter);
        this.autoZone = auto;
    }

    public JacksonZonedDateTimeSerializer(ZonedDateTimeSerializer base, Boolean useTimestamp, DateTimeFormatter formatter, Boolean writeZoneId, boolean auto) {
        super(base, useTimestamp, formatter, writeZoneId);
        this.autoZone = auto;
    }

    public JacksonZonedDateTimeSerializer(ZonedDateTimeSerializer base, Boolean useTimestamp, Boolean useNanoseconds, DateTimeFormatter formatter, Boolean writeZoneId, boolean auto) {
        super(base, useTimestamp, useNanoseconds, formatter, writeZoneId);
        this.autoZone = auto;
    }

    @Override
    protected JacksonZonedDateTimeSerializer withFormat(Boolean useTimestamp, DateTimeFormatter formatter, JsonFormat.Shape shape) {
        return new JacksonZonedDateTimeSerializer(this, useTimestamp, formatter, _writeZoneId, autoZone);
    }

    @Override
    protected JacksonZonedDateTimeSerializer withFeatures(Boolean writeZoneId, Boolean writeNanoseconds) {
        return new JacksonZonedDateTimeSerializer(this, _useTimestamp, writeNanoseconds, _formatter, writeZoneId, autoZone);
    }

    @Override
    public void serialize(ZonedDateTime value, JsonGenerator g, SerializerProvider provider) throws IOException {
        if (autoZone) {
            final TimeZone tz = LocaleContextHolder.getTimeZone();
            value = DateLocaling.zoned(value, tz.toZoneId());
        }
        super.serialize(value, g, provider);
    }
}
