package pro.fessional.wings.slardar.autozone.json;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import pro.fessional.mirana.time.DateParser;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.List;

/**
 * @author trydofor
 * @since 2021-05-22
 */
public class JacksonLocalDateDeserializer extends LocalDateDeserializer {
    private final List<DateTimeFormatter> formats;

    public JacksonLocalDateDeserializer(DateTimeFormatter dtf, List<DateTimeFormatter> formats) {
        super(dtf);
        this.formats = formats;
    }

    public JacksonLocalDateDeserializer(LocalDateDeserializer base, DateTimeFormatter dtf, List<DateTimeFormatter> formats) {
        super(base, dtf);
        this.formats = formats;
    }

    protected JacksonLocalDateDeserializer(LocalDateDeserializer base, Boolean leniency, List<DateTimeFormatter> formats) {
        super(base, leniency);
        this.formats = formats;
    }

    protected JacksonLocalDateDeserializer(LocalDateDeserializer base, JsonFormat.Shape shape, List<DateTimeFormatter> formats) {
        super(base, shape);
        this.formats = formats;
    }

    @Override
    protected LocalDateDeserializer withDateFormat(DateTimeFormatter dtf) {
        if (dtf == _formatter) return this;
        final List<DateTimeFormatter> fts = new ArrayList<>(formats.size());
        fts.add(dtf);
        fts.addAll(formats);
        return new JacksonLocalDateDeserializer(this, dtf, fts);
    }

    @Override
    protected LocalDateDeserializer withLeniency(Boolean leniency) {
        return new JacksonLocalDateDeserializer(this, leniency, formats);
    }

    @Override
    protected LocalDateDeserializer withShape(JsonFormat.Shape shape) {
        return new JacksonLocalDateDeserializer(this, shape, formats);
    }

    @Override
    public LocalDate deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        TemporalAccessor tma = DateParser.parseTemporal(parser.getText(), formats, true);
        if (tma == null) {
            return super.deserialize(parser, context);
        }

        final LocalDate ldt = tma.query(DateParser.QueryDate);
        return ldt != null ? ldt : super.deserialize(parser, context);
    }
}
