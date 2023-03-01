package pro.fessional.wings.slardar.autozone.json;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer;
import pro.fessional.wings.slardar.autozone.AutoTimeZone;
import pro.fessional.wings.slardar.autozone.AutoZoneAware;
import pro.fessional.wings.slardar.autozone.AutoZoneType;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author trydofor
 * @see JavaTimeModule
 * @since 2021-03-18
 */
public class JacksonZonedDateTimeSerializer extends ZonedDateTimeSerializer implements AutoZoneAware {

    public static DateTimeFormatter defaultFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
    public static AutoZoneType defaultAutoZone = AutoZoneType.Off;

    private AutoZoneType autoZone;

    // has no default (no arg) constructor
    public JacksonZonedDateTimeSerializer() {
        this(defaultFormatter, defaultAutoZone);
    }

    public JacksonZonedDateTimeSerializer(DateTimeFormatter formatter, AutoZoneType auto) {
        super(formatter);
        this.autoZone = auto;
    }

    @Override
    public void serialize(ZonedDateTime value, JsonGenerator g, SerializerProvider provider) throws IOException {
        super.serialize(autoZonedResponse(value, autoZone), g, provider);
    }

    ///
    protected JacksonZonedDateTimeSerializer(ZonedDateTimeSerializer base, Boolean useTimestamp, Boolean useNanoseconds, DateTimeFormatter formatter, JsonFormat.Shape shape, Boolean writeZoneId, AutoZoneType auto) {
        super(base, useTimestamp, useNanoseconds, formatter, shape, writeZoneId);
        this.autoZone = auto;
    }

    @Override
    protected JacksonZonedDateTimeSerializer withFormat(Boolean useTimestamp, DateTimeFormatter formatter, JsonFormat.Shape shape) {
        return new JacksonZonedDateTimeSerializer(this, useTimestamp, _useNanoseconds, formatter, shape, _writeZoneId, autoZone);
    }

    @Override
    protected JacksonZonedDateTimeSerializer withFeatures(Boolean writeZoneId, Boolean writeNanoseconds) {
        return new JacksonZonedDateTimeSerializer(this, _useTimestamp, writeNanoseconds, _formatter, this._shape, writeZoneId, autoZone);
    }

    @Override
    public JacksonZonedDateTimeSerializer createContextual(SerializerProvider prov, BeanProperty property) throws JsonMappingException {
        JacksonZonedDateTimeSerializer ser = (JacksonZonedDateTimeSerializer) super.createContextual(prov, property);
        if (property != null) {
            final AutoTimeZone anno = property.getAnnotation(AutoTimeZone.class);
            if (anno != null) {
                ser.autoZone = anno.value();
            }
        }
        return ser;
    }
}
