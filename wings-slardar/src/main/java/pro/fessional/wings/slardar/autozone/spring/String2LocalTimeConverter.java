package pro.fessional.wings.slardar.autozone.spring;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.TypeDescriptor;
import pro.fessional.mirana.time.DateParser;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @author trydofor
 * @since 2021-05-19
 */
@RequiredArgsConstructor
public class String2LocalTimeConverter extends DateTimeFormatSupport {
    private final List<DateTimeFormatter> formats;
    private final Set<ConvertiblePair> pairs = Collections.singleton(new ConvertiblePair(String.class, LocalTime.class));

    @Override
    public Set<ConvertiblePair> getConvertibleTypes() {
        return pairs;
    }

    @Override
    public Object convert(Object source, @NotNull TypeDescriptor sourceType, @NotNull TypeDescriptor targetType) {
        final DateTimeFormatter fmt = getFormatter(targetType);
        final LocalTime dt;
        if (fmt != null) {
            dt = DateParser.parseTime((String) source, fmt);
        }
        else {
            dt = DateParser.parseTime((String) source, formats);
        }
        return dt;
    }
}
