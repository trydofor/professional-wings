package pro.fessional.wings.slardar.autozone.json;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.datatype.jsr310.deser.InstantDeserializer;
import org.springframework.context.i18n.LocaleContextHolder;
import pro.fessional.mirana.time.DateParser;

import java.io.IOException;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.List;

/**
 * @author trydofor
 * @since 2019-09-01
 */
public class JacksonOffsetDateTimeDeserializer extends InstantDeserializer<OffsetDateTime> {

    private final boolean autoZone;
    private final List<DateTimeFormatter> formats;

    public JacksonOffsetDateTimeDeserializer(DateTimeFormatter formatter, List<DateTimeFormatter> formats, boolean auto) {
        super(OffsetDateTime.class,
                formatter,
                temporal -> DateParser.parseOffset(temporal, ZoneId.systemDefault()),
                a -> OffsetDateTime.ofInstant(Instant.ofEpochMilli(a.value), a.zoneId),
                a -> OffsetDateTime.ofInstant(Instant.ofEpochSecond(a.integer, a.fraction), a.zoneId),
                (zonedDateTime, zoneId) -> zonedDateTime,
                false // keep zero offset and Z separate since zones explicitly supported
        );
        this.formats = formats;
        this.autoZone = auto;
    }

    public JacksonOffsetDateTimeDeserializer(JacksonOffsetDateTimeDeserializer jacksonZonedDeserializer, Boolean leniency, List<DateTimeFormatter> formats, boolean auto) {
        super(jacksonZonedDeserializer, leniency);
        this.formats = formats;
        this.autoZone = auto;
    }


    @Override
    protected JacksonOffsetDateTimeDeserializer withDateFormat(DateTimeFormatter dtf) {
        if (dtf == _formatter) return this;
        return new JacksonOffsetDateTimeDeserializer(dtf, formats, autoZone);
    }

    @Override
    protected JacksonOffsetDateTimeDeserializer withLeniency(Boolean leniency) {
        return new JacksonOffsetDateTimeDeserializer(this, leniency, formats, autoZone);
    }

    @Override
    protected JacksonOffsetDateTimeDeserializer withShape(JsonFormat.Shape shape) {
        return this;
    }

    @Override
    public OffsetDateTime deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        String str = parser.getText().trim();
        TemporalAccessor tma = DateParser.parseTemporal(str, formats, true);
        if (tma == null) {
            return super.deserialize(parser, context);
        }
        final ZoneId zid = LocaleContextHolder.getTimeZone().toZoneId();
        final OffsetDateTime zdt = DateParser.parseOffset(tma, zid);

        return autoZone ? zdt.atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime() : zdt;
    }
}
