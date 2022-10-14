package pro.fessional.wings.slardar.autozone.spring;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.TypeDescriptor;
import pro.fessional.mirana.time.DateParser;
import pro.fessional.wings.slardar.autozone.AutoTimeZone;
import pro.fessional.wings.slardar.autozone.AutoZoneAware;
import pro.fessional.wings.slardar.autozone.AutoZoneType;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static java.util.Collections.singletonList;

/**
 * ConversionService
 *
 * @author trydofor
 * @since 2021-03-22
 */

@RequiredArgsConstructor
public class String2OffsetDateTimeConverter extends DateTimeFormatSupport implements AutoZoneAware {

    private final List<DateTimeFormatter> formats;
    private final AutoZoneType autoZone;
    private final Set<ConvertiblePair> pairs = Collections.singleton(new ConvertiblePair(String.class, OffsetDateTime.class));

    public String2OffsetDateTimeConverter(List<DateTimeFormatter> formats, boolean auto) {
        this(formats, AutoZoneType.valueOf(auto));
    }

    @Override
    public Set<ConvertiblePair> getConvertibleTypes() {
        return pairs;
    }

    @Override
    public Object convert(Object source, @NotNull TypeDescriptor sourceType, @NotNull TypeDescriptor targetType) {
        final DateTimeFormatter fmt = getFormatter(targetType);
        final List<DateTimeFormatter> fmts = fmt == null ? formats : singletonList(fmt);
        TemporalAccessor tma = DateParser.parseTemporal((String) source, fmts, true);
        if (tma != null) {
            final AutoTimeZone anno = targetType.getAnnotation(AutoTimeZone.class);
            return autoOffsetRequest(tma, anno == null ? autoZone : anno.value());
        }

        return null;
    }
}
