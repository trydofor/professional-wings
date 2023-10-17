package pro.fessional.wings.slardar.autozone.json;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import pro.fessional.wings.slardar.autozone.AutoTimeZone;
import pro.fessional.wings.slardar.autozone.AutoZoneAware;
import pro.fessional.wings.slardar.autozone.AutoZoneType;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * <a href="https://github.com/FasterXML/jackson-modules-java8/issues/230">LocalDateTimeSerializer constructor protected</a>
 *
 * @author trydofor
 * @since 2022-10-01
 */
public class JacksonLocalDateTimeSerializer extends LocalDateTimeSerializer implements AutoZoneAware {

    public static DateTimeFormatter defaultFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    public static AutoZoneType defaultAutoZone = AutoZoneType.Off;

    private AutoZoneType autoZone;

    // has no default (no arg) constructor
    public JacksonLocalDateTimeSerializer() {
        this(defaultFormatter, defaultAutoZone);
    }

    public JacksonLocalDateTimeSerializer(DateTimeFormatter f, AutoZoneType auto) {
        super(f);
        autoZone = auto;
    }

    @Override
    public void serialize(LocalDateTime value, JsonGenerator g, SerializerProvider provider) throws IOException {
        super.serialize(autoLocalResponse(value, autoZone), g, provider);
    }

    @Override
    public void serializeWithType(LocalDateTime value, JsonGenerator g, SerializerProvider provider, TypeSerializer typeSer) throws IOException {
        super.serializeWithType(autoLocalResponse(value, autoZone), g, provider, typeSer);
    }

    ///
    protected JacksonLocalDateTimeSerializer(LocalDateTimeSerializer base, Boolean useTimestamp, Boolean useNanoseconds, DateTimeFormatter f, AutoZoneType auto) {
        super(base, useTimestamp, useNanoseconds, f);
        this.autoZone = auto;
    }

    @Override
    protected JacksonLocalDateTimeSerializer withFormat(Boolean useTimestamp, DateTimeFormatter f, JsonFormat.Shape shape) {
        return new JacksonLocalDateTimeSerializer(this, useTimestamp, this._useNanoseconds, f, autoZone);
    }

    @Override
    protected JacksonLocalDateTimeSerializer withFeatures(Boolean writeZoneId, Boolean writeNanoseconds) {
        return new JacksonLocalDateTimeSerializer(this, this._useTimestamp, writeNanoseconds, this._formatter, autoZone);
    }


    @Override
    public JacksonLocalDateTimeSerializer createContextual(SerializerProvider prov, BeanProperty property) throws JsonMappingException {
        JacksonLocalDateTimeSerializer ser = (JacksonLocalDateTimeSerializer) super.createContextual(prov, property);
        if (property != null) {
            final AutoTimeZone anno = property.getAnnotation(AutoTimeZone.class);
            if (anno != null) {
                ser.autoZone = anno.value();
            }
        }
        return ser;
    }
}
