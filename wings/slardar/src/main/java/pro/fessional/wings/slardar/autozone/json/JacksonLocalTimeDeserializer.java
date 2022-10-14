package pro.fessional.wings.slardar.autozone.json;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import pro.fessional.mirana.time.DateParser;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.List;

/**
 * @author trydofor
 * @since 2021-05-22
 */
public class JacksonLocalTimeDeserializer extends LocalTimeDeserializer {
    private final List<DateTimeFormatter> formats;

    public JacksonLocalTimeDeserializer(DateTimeFormatter formatter, List<DateTimeFormatter> formats) {
        super(formatter);
        this.formats = formats;
    }

    protected JacksonLocalTimeDeserializer(LocalTimeDeserializer base, Boolean leniency, List<DateTimeFormatter> formats) {
        super(base, leniency);
        this.formats = formats;
    }

    @Override
    protected LocalTimeDeserializer withDateFormat(DateTimeFormatter dtf) {
        if (dtf == _formatter) return this;
        final List<DateTimeFormatter> fts = new ArrayList<>(formats.size());
        fts.add(dtf);
        fts.addAll(formats);
        return new JacksonLocalTimeDeserializer(dtf, fts);
    }

    @Override
    protected LocalTimeDeserializer withLeniency(Boolean leniency) {
        return new JacksonLocalTimeDeserializer(this, leniency, formats);
    }

    @Override
    protected LocalTimeDeserializer withShape(JsonFormat.Shape shape) {
        return this;
    }

    @Override
    public LocalTime deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        TemporalAccessor tma = DateParser.parseTemporal(parser.getText(), formats, true);
        if (tma == null) {
            return super.deserialize(parser, context);
        }

        final LocalTime ldt = tma.query(DateParser.QueryTime);
        return ldt != null ? ldt : super.deserialize(parser, context);
    }
}
