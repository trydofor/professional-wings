package pro.fessional.wings.slardar.autozone.json;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.InstantSerializerBase;
import pro.fessional.wings.slardar.autozone.AutoTimeZone;
import pro.fessional.wings.slardar.autozone.AutoZoneAware;
import pro.fessional.wings.slardar.autozone.AutoZoneType;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author trydofor
 * @see JavaTimeModule
 * @since 2021-03-18
 */
public class JacksonOffsetDateTimeSerializer extends InstantSerializerBase<OffsetDateTime> implements AutoZoneAware {

    public static DateTimeFormatter defaultFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
    public static AutoZoneType defaultAutoZone = AutoZoneType.Auto;

    private AutoZoneType autoZone;

    // has no default (no arg) constructor
    public JacksonOffsetDateTimeSerializer() {
        this(defaultFormatter, defaultAutoZone);
    }

    public JacksonOffsetDateTimeSerializer(DateTimeFormatter formatter, AutoZoneType auto) {
        super(OffsetDateTime.class, dt -> dt.toInstant().toEpochMilli(),
                OffsetDateTime::toEpochSecond, OffsetDateTime::getNano, formatter);
        this.autoZone = auto;
    }

    @Override
    public void serialize(OffsetDateTime value, JsonGenerator g, SerializerProvider provider) throws IOException {
        super.serialize(autoOffsetResponse(value, autoZone), g, provider);
    }

    ///
    protected JacksonOffsetDateTimeSerializer(JacksonOffsetDateTimeSerializer base,
                                              Boolean useTimestamp, DateTimeFormatter formatter, AutoZoneType auto) {
        this(base, useTimestamp, null, formatter, auto);
    }

    protected JacksonOffsetDateTimeSerializer(JacksonOffsetDateTimeSerializer base,
                                              Boolean useTimestamp, Boolean useNanoseconds, DateTimeFormatter formatter, AutoZoneType auto) {
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
    public JacksonOffsetDateTimeSerializer createContextual(SerializerProvider prov, BeanProperty property) throws JsonMappingException {
        JacksonOffsetDateTimeSerializer ser = (JacksonOffsetDateTimeSerializer) super.createContextual(prov, property);
        if (property != null) {
            final AutoTimeZone anno = property.getAnnotation(AutoTimeZone.class);
            if (anno != null) {
                ser.autoZone = anno.value();
            }
        }
        return ser;
    }
}
