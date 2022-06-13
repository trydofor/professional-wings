package pro.fessional.wings.slardar.autozone.spring;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.convert.TypeDescriptor;
import pro.fessional.mirana.time.DateParser;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

/**
 * ConversionService
 *
 * @author trydofor
 * @since 2021-03-22
 */

@RequiredArgsConstructor
public class String2OffsetDateTimeConverter extends DateTimeFormatSupport {

    private final List<DateTimeFormatter> formats;
    private final Set<ConvertiblePair> pairs = Collections.singleton(new ConvertiblePair(String.class, OffsetDateTime.class));
    private final boolean autoZone;

    @Override
    public Set<ConvertiblePair> getConvertibleTypes() {
        return pairs;
    }

    @Override
    public Object convert(Object source, @NotNull TypeDescriptor sourceType, @NotNull TypeDescriptor targetType) {
        final TimeZone tz = LocaleContextHolder.getTimeZone();
        final DateTimeFormatter fmt = getFormatter(targetType);
        final OffsetDateTime odt;
        if (fmt != null) {
            odt = DateParser.parseOffset((String) source, tz.toZoneId(), fmt);
        }
        else {
            odt = DateParser.parseOffset((String) source, tz.toZoneId(), formats);
        }

        if (autoZone) {
            return odt.atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime();
        }
        else {
            return odt;
        }
    }
}
