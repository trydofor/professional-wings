package pro.fessional.wings.slardar.autozone.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer;
import org.springframework.context.i18n.LocaleContextHolder;
import pro.fessional.wings.silencer.datetime.DateTimePattern;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

/**
 * @author trydofor
 * @since 2021-03-18
 */
public class JacksonZonedSerializer extends ZonedDateTimeSerializer {

    public JacksonZonedSerializer() {
        super(DateTimePattern.FMT_FULL_19);
    }

    public JacksonZonedSerializer(DateTimeFormatter formatter) {
        super(formatter);
    }

    @Override
    public void serialize(ZonedDateTime value, JsonGenerator g, SerializerProvider provider) throws IOException {
        final TimeZone tz = LocaleContextHolder.getTimeZone();
        final ZoneId zid = tz.toZoneId();
        if (!zid.equals(value.getZone())) {
            value = value.withZoneSameInstant(zid);
        }
        super.serialize(value, g, provider);
    }
}
