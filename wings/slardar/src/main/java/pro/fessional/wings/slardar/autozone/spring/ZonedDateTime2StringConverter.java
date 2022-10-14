package pro.fessional.wings.slardar.autozone.spring;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.TypeDescriptor;
import pro.fessional.wings.slardar.autozone.AutoTimeZone;
import pro.fessional.wings.slardar.autozone.AutoZoneAware;
import pro.fessional.wings.slardar.autozone.AutoZoneType;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Set;

/**
 * ConversionService
 *
 * @author trydofor
 * @since 2021-03-22
 */

@RequiredArgsConstructor
public class ZonedDateTime2StringConverter extends DateTimeFormatSupport implements AutoZoneAware {

    private final DateTimeFormatter format;
    private final AutoZoneType autoZone;
    private final Set<ConvertiblePair> pairs = Collections.singleton(new ConvertiblePair(ZonedDateTime.class, String.class));

    public ZonedDateTime2StringConverter(DateTimeFormatter format, boolean auto) {
        this(format, AutoZoneType.valueOf(auto));
    }

    @Override
    public Set<ConvertiblePair> getConvertibleTypes() {
        return pairs;
    }

    @Override
    public Object convert(Object source, @NotNull TypeDescriptor sourceType, @NotNull TypeDescriptor targetType) {
        final AutoTimeZone anno = targetType.getAnnotation(AutoTimeZone.class);
        final ZonedDateTime zdt = autoZonedResponse((ZonedDateTime) source, anno == null ? autoZone : anno.value());
        final DateTimeFormatter fmt = getFormatter(targetType);
        return zdt.format(fmt == null ? format : fmt);
    }
}
