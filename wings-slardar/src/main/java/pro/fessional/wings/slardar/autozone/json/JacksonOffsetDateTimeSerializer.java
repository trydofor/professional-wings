package pro.fessional.wings.slardar.autozone.json;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.InstantSerializerBase;
import org.springframework.context.i18n.LocaleContextHolder;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

/**
 * @author trydofor
 * @see JavaTimeModule
 * @since 2021-03-18
 */
public class JacksonOffsetDateTimeSerializer extends InstantSerializerBase<OffsetDateTime> {

    public static DateTimeFormatter defaultFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
    public static boolean defaultAutoZone = true;
    private final boolean autoZone;

    // has no default (no arg) constructor
    public JacksonOffsetDateTimeSerializer() {
        this(defaultFormatter, defaultAutoZone);
    }

    public JacksonOffsetDateTimeSerializer(DateTimeFormatter offset, boolean autoOffset) {
        super(OffsetDateTime.class, dt -> dt.toInstant().toEpochMilli(),
                OffsetDateTime::toEpochSecond, OffsetDateTime::getNano, offset);
        this.autoZone = autoOffset;
    }

    protected JacksonOffsetDateTimeSerializer(JacksonOffsetDateTimeSerializer base,
                                              Boolean useTimestamp, DateTimeFormatter formatter, boolean auto) {
        this(base, useTimestamp, null, formatter, auto);

    }

    protected JacksonOffsetDateTimeSerializer(JacksonOffsetDateTimeSerializer base,
                                              Boolean useTimestamp, Boolean useNanoseconds, DateTimeFormatter formatter, boolean auto) {
        super(base, useTimestamp, useNanoseconds, formatter);
        this.autoZone = auto;
    }

    @Override
    protected JacksonOffsetDateTimeSerializer withFormat(Boolean useTimestamp,
                                                         DateTimeFormatter formatter, JsonFormat.Shape shape) {
        return new JacksonOffsetDateTimeSerializer(this, useTimestamp, formatter, autoZone);
    }

    @Override
    protected JacksonOffsetDateTimeSerializer withFeatures(Boolean writeZoneId, Boolean writeNanoseconds) {
        return new JacksonOffsetDateTimeSerializer(this, _useTimestamp, writeNanoseconds, _formatter, autoZone);
    }

    @Override
    public void serialize(OffsetDateTime value, JsonGenerator g, SerializerProvider provider) throws IOException {
        if (autoZone) {
            final TimeZone tz = LocaleContextHolder.getTimeZone();
            value = value.atZoneSameInstant(tz.toZoneId()).toOffsetDateTime();
            super.serialize(value, g, provider);
        }
        else {
            super.serialize(value, g, provider);
        }
    }
}
