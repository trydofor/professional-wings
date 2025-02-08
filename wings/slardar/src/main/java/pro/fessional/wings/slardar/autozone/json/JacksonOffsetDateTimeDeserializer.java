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
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.List;

import static com.fasterxml.jackson.datatype.jsr310.JavaTimeFeature.ALWAYS_ALLOW_STRINGIFIED_DATE_TIMESTAMPS;
import static com.fasterxml.jackson.datatype.jsr310.JavaTimeFeature.NORMALIZE_DESERIALIZED_ZONE_ID;

/**
 * @author trydofor
 * @see InstantDeserializer#OFFSET_DATE_TIME
 * @since 2019-09-01
 */
public class JacksonOffsetDateTimeDeserializer extends InstantDeserializer<OffsetDateTime> implements AutoZoneAware {

    private final List<DateTimeFormatter> formats;

    private AutoZoneType autoZone;

    public JacksonOffsetDateTimeDeserializer(DateTimeFormatter formatter, List<DateTimeFormatter> formats, boolean auto) {
        this(formatter, formats, AutoZoneType.valueOf(auto));
    }

    public JacksonOffsetDateTimeDeserializer(DateTimeFormatter formatter, List<DateTimeFormatter> formats, AutoZoneType auto) {
        super(OffsetDateTime.class,
            formatter,
            temporal -> DateParser.parseOffset(temporal, auto == AutoZoneType.Off ? null : ThreadNow.sysZoneId()),
            a -> OffsetDateTime.ofInstant(Instant.ofEpochMilli(a.value), a.zoneId),
            a -> OffsetDateTime.ofInstant(Instant.ofEpochSecond(a.integer, a.fraction), a.zoneId),
            (zonedDateTime, zoneId) -> zonedDateTime,
            false, // keep zero offset and Z separate since zones explicitly supported
            NORMALIZE_DESERIALIZED_ZONE_ID.enabledByDefault(),
            ALWAYS_ALLOW_STRINGIFIED_DATE_TIMESTAMPS.enabledByDefault()
        );
        this.formats = formats;
        this.autoZone = auto;
    }

    @Override
    public OffsetDateTime deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        TemporalAccessor tma = DateParser.parseTemporal(parser.getText(), formats, true);
        if (tma != null) {
            return autoOffsetRequest(tma, autoZone);
        }

        return super.deserialize(parser, context);
    }

    ///
    protected JacksonOffsetDateTimeDeserializer(JacksonOffsetDateTimeDeserializer jacksonZonedDeserializer, Boolean leniency, List<DateTimeFormatter> formats, AutoZoneType auto) {
        super(jacksonZonedDeserializer, leniency);
        this.formats = formats;
        this.autoZone = auto;
    }

    @Override
    protected JacksonOffsetDateTimeDeserializer withDateFormat(DateTimeFormatter dtf) {
        if (dtf == _formatter) return this;
        final List<DateTimeFormatter> fts = new ArrayList<>(formats.size());
        fts.add(dtf);
        fts.addAll(formats);
        return new JacksonOffsetDateTimeDeserializer(dtf, fts, autoZone);
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
    public JacksonOffsetDateTimeDeserializer createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
        JacksonOffsetDateTimeDeserializer dsr = (JacksonOffsetDateTimeDeserializer) super.createContextual(ctxt, property);
        if (property != null) {
            final AutoTimeZone anno = property.getAnnotation(AutoTimeZone.class);
            if (anno != null) {
                dsr.autoZone = anno.value();
            }
        }
        return dsr;
    }

    @NotNull
    public JacksonOffsetDateTimeDeserializer autoOff() {
        return new JacksonOffsetDateTimeDeserializer(_formatter, formats, AutoZoneType.Off);
    }
}
