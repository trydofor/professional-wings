package pro.fessional.wings.slardar.autozone.json;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.datatype.jsr310.deser.InstantDeserializer;
import org.jetbrains.annotations.NotNull;
import pro.fessional.mirana.time.DateParser;
import pro.fessional.mirana.time.ThreadNow;
import pro.fessional.wings.slardar.autozone.AutoTimeZone;
import pro.fessional.wings.slardar.autozone.AutoZoneAware;
import pro.fessional.wings.slardar.autozone.AutoZoneType;

import java.io.IOException;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.List;

import static com.fasterxml.jackson.datatype.jsr310.JavaTimeFeature.ALWAYS_ALLOW_STRINGIFIED_DATE_TIMESTAMPS;
import static com.fasterxml.jackson.datatype.jsr310.JavaTimeFeature.NORMALIZE_DESERIALIZED_ZONE_ID;

/**
 * @author trydofor
 * @see InstantDeserializer#ZONED_DATE_TIME
 * @since 2019-09-01
 */
public class JacksonZonedDateTimeDeserializer extends InstantDeserializer<ZonedDateTime> implements AutoZoneAware {

    private final List<DateTimeFormatter> formats;

    private AutoZoneType autoZone;

    public JacksonZonedDateTimeDeserializer(DateTimeFormatter formatter, List<DateTimeFormatter> formats, boolean auto) {
        this(formatter, formats, AutoZoneType.valueOf(auto));
    }

    public JacksonZonedDateTimeDeserializer(DateTimeFormatter formatter, List<DateTimeFormatter> formats, AutoZoneType auto) {
        super(ZonedDateTime.class,
            formatter,
            temporal -> DateParser.parseZoned(temporal, auto == AutoZoneType.Off ? null : ThreadNow.sysZoneId()),
            a -> ZonedDateTime.ofInstant(Instant.ofEpochMilli(a.value), a.zoneId),
            a -> ZonedDateTime.ofInstant(Instant.ofEpochSecond(a.integer, a.fraction), a.zoneId),
            (zonedDateTime, zoneId) -> zonedDateTime,
            false, // keep zero offset and Z separate since zones explicitly supported
            NORMALIZE_DESERIALIZED_ZONE_ID.enabledByDefault(),
            ALWAYS_ALLOW_STRINGIFIED_DATE_TIMESTAMPS.enabledByDefault()
        );
        this.formats = formats;
        this.autoZone = auto;
    }

    @Override
    public ZonedDateTime deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        TemporalAccessor tma = DateParser.parseTemporal(parser.getText(), formats, true);
        if (tma != null) {
            return autoZonedRequest(tma, autoZone);
        }

        return super.deserialize(parser, context);
    }

    ///
    protected JacksonZonedDateTimeDeserializer(JacksonZonedDateTimeDeserializer jacksonZonedDeserializer, Boolean leniency, List<DateTimeFormatter> formats, AutoZoneType auto) {
        super(jacksonZonedDeserializer, leniency);
        this.formats = formats;
        this.autoZone = auto;
    }

    @Override
    protected JacksonZonedDateTimeDeserializer withDateFormat(DateTimeFormatter dtf) {
        if (dtf == _formatter) return this;
        final List<DateTimeFormatter> fts = new ArrayList<>(formats.size());
        fts.add(dtf);
        fts.addAll(formats);
        return new JacksonZonedDateTimeDeserializer(dtf, fts, autoZone);
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
    public JacksonZonedDateTimeDeserializer createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
        JacksonZonedDateTimeDeserializer dsr = (JacksonZonedDateTimeDeserializer) super.createContextual(ctxt, property);
        if (property != null) {
            final AutoTimeZone anno = property.getAnnotation(AutoTimeZone.class);
            if (anno != null) {
                dsr.autoZone = anno.value();
            }
        }
        return dsr;
    }

    @NotNull
    public JacksonZonedDateTimeDeserializer autoOff() {
        return new JacksonZonedDateTimeDeserializer(_formatter, formats, AutoZoneType.Off);
    }
}
