package pro.fessional.wings.slardar.autozone.spring;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.TypeDescriptor;
import pro.fessional.mirana.time.DateParser;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @author trydofor
 * @since 2021-05-19
 */
@RequiredArgsConstructor
public class String2LocalDateConverter extends DateTimeFormatSupport {
    private final List<DateTimeFormatter> formats;
    private final Set<ConvertiblePair> pairs = Collections.singleton(new ConvertiblePair(String.class, LocalDate.class));

    @Override
    public Set<ConvertiblePair> getConvertibleTypes() {
        return pairs;
    }

    @Override
    public Object convert(Object source, @NotNull TypeDescriptor sourceType, @NotNull TypeDescriptor targetType) {
        final DateTimeFormatter fmt = getFormatter(targetType);
        final LocalDate dt;
        if (fmt != null) {
            dt = DateParser.parseDate((String) source, fmt);
        }
        else {
            dt = DateParser.parseDate((String) source, formats);
        }
        return dt;

    }
}
