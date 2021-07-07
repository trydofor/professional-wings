package pro.fessional.wings.slardar.autozone.json;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import pro.fessional.mirana.time.DateParser;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.List;

/**
 * @author trydofor
 * @since 2021-05-22
 */
public class JacksonLocalDateTimeDeserializer extends LocalDateTimeDeserializer {

    private final List<DateTimeFormatter> formats;

    public JacksonLocalDateTimeDeserializer(DateTimeFormatter formatter, List<DateTimeFormatter> formats) {
        super(formatter);
        this.formats = formats;
    }

    protected JacksonLocalDateTimeDeserializer(LocalDateTimeDeserializer base, Boolean leniency, List<DateTimeFormatter> formats) {
        super(base, leniency);
        this.formats = formats;
    }

    @Override
    protected LocalDateTimeDeserializer withDateFormat(DateTimeFormatter dtf) {
        if (dtf == _formatter) return this;
        return new JacksonLocalDateTimeDeserializer(dtf, formats);
    }

    @Override
    protected LocalDateTimeDeserializer withLeniency(Boolean leniency) {
        return new JacksonLocalDateTimeDeserializer(this, leniency, formats);
    }

    @Override
    protected LocalDateTimeDeserializer withShape(JsonFormat.Shape shape) {
        return this;
    }

    @Override
    public LocalDateTime deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        String str = parser.getText().trim();
        TemporalAccessor tma = DateParser.parseTemporal(str, formats, true);
        if (tma == null) {
            return super.deserialize(parser, context);
        }

        final LocalDateTime ldt = tma.query(DateParser.QueryDateTime);
        return ldt != null ? ldt : super.deserialize(parser, context);
    }
}
