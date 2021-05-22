package pro.fessional.wings.slardar.autozone.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import pro.fessional.mirana.time.DateParser;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
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

    @Override
    public LocalDate deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        String str = parser.getText().trim();
        TemporalAccessor tma = DateParser.parseTemporal(str, formats, true);
        if (tma == null) {
            return super.deserialize(parser, context);
        }

        final LocalDate ldt = tma.query(DateParser.QueryDate);
        return ldt != null ? ldt : super.deserialize(parser, context);
    }
}
