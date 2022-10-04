package pro.fessional.wings.slardar.autozone.json;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import pro.fessional.mirana.time.DateParser;
import pro.fessional.wings.slardar.autozone.AutoTimeZone;
import pro.fessional.wings.slardar.autozone.AutoZoneAware;
import pro.fessional.wings.slardar.autozone.AutoZoneType;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.List;

/**
 * @author trydofor
 * @since 2021-05-22
 */
public class JacksonLocalDateTimeDeserializer extends LocalDateTimeDeserializer implements AutoZoneAware {

    private final List<DateTimeFormatter> formats;

    private AutoZoneType autoZone;

    public JacksonLocalDateTimeDeserializer(DateTimeFormatter formatter, List<DateTimeFormatter> formats, boolean auto) {
        this(formatter, formats, AutoZoneType.valueOf(auto));
    }

    public JacksonLocalDateTimeDeserializer(DateTimeFormatter formatter, List<DateTimeFormatter> formats, AutoZoneType auto) {
        super(formatter);
        this.formats = formats;
        this.autoZone = auto;
    }

    @Override
    public LocalDateTime deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        final TemporalAccessor tma = DateParser.parseTemporal(parser.getText(), formats, true);
        if (tma != null) {
            return autoLocalRequest(tma, autoZone);
        }

        return super.deserialize(parser, context);
    }

    ///
    protected JacksonLocalDateTimeDeserializer(LocalDateTimeDeserializer base, Boolean leniency, List<DateTimeFormatter> formats) {
        super(base, leniency);
        this.formats = formats;
    }

    @Override
    protected JacksonLocalDateTimeDeserializer withDateFormat(DateTimeFormatter dtf) {
        if (dtf == _formatter) return this;
        final List<DateTimeFormatter> fts = new ArrayList<>(formats.size());
        fts.add(dtf);
        fts.addAll(formats);
        return new JacksonLocalDateTimeDeserializer(dtf, fts, autoZone);
    }

    @Override
    protected JacksonLocalDateTimeDeserializer withLeniency(Boolean leniency) {
        return new JacksonLocalDateTimeDeserializer(this, leniency, formats);
    }

    @Override
    protected JacksonLocalDateTimeDeserializer withShape(JsonFormat.Shape shape) {
        return this;
    }

    @Override
    public JacksonLocalDateTimeDeserializer createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
        JacksonLocalDateTimeDeserializer dsr = (JacksonLocalDateTimeDeserializer) super.createContextual(ctxt, property);
        if (property != null) {
            final AutoTimeZone anno = property.getAnnotation(AutoTimeZone.class);
            if (anno != null) {
                dsr.autoZone = anno.value();
            }
        }
        return dsr;
    }
}
