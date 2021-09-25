package pro.fessional.wings.slardar.autozone.json;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.datatype.jsr310.deser.InstantDeserializer;
import org.springframework.context.i18n.LocaleContextHolder;
import pro.fessional.mirana.time.DateLocaling;
import pro.fessional.mirana.time.DateParser;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.List;

/**
 * @author trydofor
 * @since 2019-09-01
 */
public class JacksonZonedDateTimeDeserializer extends InstantDeserializer<ZonedDateTime> {

    private final boolean autoZone;
    private final List<DateTimeFormatter> formats;

    public JacksonZonedDateTimeDeserializer(DateTimeFormatter formatter, List<DateTimeFormatter> formats, boolean auto) {
        super(ZonedDateTime.class,
                formatter,
                temporal -> DateParser.parseZoned(temporal, ZoneId.systemDefault()),
                a -> ZonedDateTime.ofInstant(Instant.ofEpochMilli(a.value), a.zoneId),
                a -> ZonedDateTime.ofInstant(Instant.ofEpochSecond(a.integer, a.fraction), a.zoneId),
                (zonedDateTime, zoneId) -> zonedDateTime,
                false // keep zero offset and Z separate since zones explicitly supported
        );
        this.formats = formats;
        this.autoZone = auto;
    }

    public JacksonZonedDateTimeDeserializer(JacksonZonedDateTimeDeserializer jacksonZonedDeserializer, Boolean leniency, List<DateTimeFormatter> formats, boolean auto) {
        super(jacksonZonedDeserializer, leniency);
        this.formats = formats;
        this.autoZone = auto;
    }


    @Override
    protected JacksonZonedDateTimeDeserializer withDateFormat(DateTimeFormatter dtf) {
        if (dtf == _formatter) return this;
        return new JacksonZonedDateTimeDeserializer(dtf, formats, autoZone);
    }

    @Override
    protected JacksonZonedDateTimeDeserializer withLeniency(Boolean leniency) {
        return new JacksonZonedDateTimeDeserializer(this, leniency, formats, autoZone);
    }

    @Override
    protected JacksonZonedDateTimeDeserializer withShape(JsonFormat.Shape shape) {
        return this;
    }

    @Override
    public ZonedDateTime deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        String str = parser.getText().trim();
        TemporalAccessor tma = DateParser.parseTemporal(str, formats, true);
        if (tma == null) {
            return super.deserialize(parser, context);
        }
        final ZoneId zid = LocaleContextHolder.getTimeZone().toZoneId();
        final ZonedDateTime zdt = DateParser.parseZoned(tma, zid);

        return autoZone ? DateLocaling.zoneZone(zdt, ZoneId.systemDefault()) : zdt;
    }
}
