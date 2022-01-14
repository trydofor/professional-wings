package pro.fessional.wings.slardar.autozone.spring;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.convert.TypeDescriptor;

import java.time.ZonedDateTime;
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
public class ZonedDateTime2StringConverter extends DateTimeFormatSupport {

    private final DateTimeFormatter format;
    private final Set<ConvertiblePair> pairs = Collections.singleton(new ConvertiblePair(ZonedDateTime.class, String.class));
    private final boolean autoZone;

    @Override
    public Set<ConvertiblePair> getConvertibleTypes() {
        return pairs;
    }

    @Override
    public Object convert(Object source, @NotNull TypeDescriptor sourceType, @NotNull TypeDescriptor targetType) {
        final ZonedDateTime zdt;
        if (autoZone) {
            final TimeZone tz = LocaleContextHolder.getTimeZone();
            zdt = ((ZonedDateTime) source).withZoneSameInstant(tz.toZoneId());
        }
        else {
            zdt = (ZonedDateTime) source;
        }
        final DateTimeFormatter fmt = getFormatter(targetType);
        return zdt.format(fmt == null ? format : fmt);
    }
}
