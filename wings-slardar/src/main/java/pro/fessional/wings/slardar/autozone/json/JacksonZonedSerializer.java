package pro.fessional.wings.slardar.autozone.json;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer;
import org.springframework.context.i18n.LocaleContextHolder;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @author trydofor
 * @see JavaTimeModule
 * @since 2021-03-18
 */
public class JacksonZonedSerializer extends ZonedDateTimeSerializer {

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
        final ZoneId zid = tz.toZoneId();
        if (!zid.equals(value.getZone())) {
            value = value.withZoneSameInstant(zid);
        }
        super.serialize(value, g, provider);
    }

    // fuck the JSR310FormattedSerializerBase is not visible
    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) {
        JsonFormat.Value format = findFormatOverrides(prov, property, handledType());
        if (format != null) {
            final Boolean useTimestamp;

            // Simple case first: serialize as numeric timestamp?
            JsonFormat.Shape shape = format.getShape();
            if (shape == JsonFormat.Shape.ARRAY || shape.isNumeric()) {
                useTimestamp = Boolean.TRUE;
            }
            else {
                useTimestamp = (shape == JsonFormat.Shape.STRING) ? Boolean.FALSE : null;
            }
            DateTimeFormatter dtf = _formatter;

            // If not, do we have a pattern?
            if (format.hasPattern()) {
                final String pattern = format.getPattern();
                final Locale locale = format.hasLocale() ? format.getLocale() : prov.getLocale();
                if (locale == null) {
                    dtf = DateTimeFormatter.ofPattern(pattern);
                }
                else {
                    dtf = DateTimeFormatter.ofPattern(pattern, locale);
                }
                //Issue #69: For instant serializers/deserializers we need to configure the formatter with
                //a time zone picked up from JsonFormat annotation, otherwise serialization might not work
                if (format.hasTimeZone()) {
                    dtf = dtf.withZone(format.getTimeZone().toZoneId());
                }
            }
            JsonSerializer<?> ser = this;
            if ((shape != _shape) || (useTimestamp != _useTimestamp) || (dtf != _formatter)) {
                ser = new JacksonZonedSerializer(this, useTimestamp, dtf, _writeZoneId);
            }
            Boolean writeZoneId = format.getFeature(JsonFormat.Feature.WRITE_DATES_WITH_ZONE_ID);
            Boolean writeNanoseconds = format.getFeature(JsonFormat.Feature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS);
            if ((writeZoneId != null) || (writeNanoseconds != null)) {
                ser = new JacksonZonedSerializer(this, _useTimestamp, writeNanoseconds, _formatter, writeZoneId);
            }
            return ser;
        }
        return this;
    }
}
