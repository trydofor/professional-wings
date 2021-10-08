package pro.fessional.wings.slardar.autozone.spring;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.convert.TypeDescriptor;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Set;
import java.util.TimeZone;

/**
 * ConversionService
 *
 * @author trydofor
 * @since 2021-03-22
 */

@RequiredArgsConstructor
public class OffsetDateTime2StringConverter extends DateTimeFormatSupport {

    private final DateTimeFormatter format;
    private final Set<ConvertiblePair> pairs = Collections.singleton(new ConvertiblePair(OffsetDateTime.class, String.class));
    private final boolean autoZone;

    @Override
    public Set<ConvertiblePair> getConvertibleTypes() {
        return pairs;
    }

    @Override
    public Object convert(Object source, @NotNull TypeDescriptor sourceType, @NotNull TypeDescriptor targetType) {
        final OffsetDateTime odt;
        if (autoZone) {
            final TimeZone tz = LocaleContextHolder.getTimeZone();
            odt = ((OffsetDateTime) source).atZoneSameInstant(tz.toZoneId()).toOffsetDateTime();
        }
        else {
            odt = (OffsetDateTime) source;
        }
        final DateTimeFormatter fmt = getFormatter(targetType);
        return odt.format(fmt == null ? format : fmt);
    }
}
