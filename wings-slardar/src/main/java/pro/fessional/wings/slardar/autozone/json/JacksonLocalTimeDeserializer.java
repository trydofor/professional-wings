package pro.fessional.wings.slardar.autozone.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import pro.fessional.mirana.time.DateParser;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
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

    @Override
    public LocalTime deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        String str = parser.getText().trim();
        TemporalAccessor tma = DateParser.parseTemporal(str, formats, true);
        if (tma == null) {
            return super.deserialize(parser, context);
        }

        final LocalTime ldt = tma.query(DateParser.QueryTime);
        return ldt != null ? ldt : super.deserialize(parser, context);
    }
}
