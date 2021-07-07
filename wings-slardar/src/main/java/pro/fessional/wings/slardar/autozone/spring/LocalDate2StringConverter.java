package pro.fessional.wings.slardar.autozone.spring;


import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.TypeDescriptor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Set;

/**
 * @author trydofor
 * @since 2021-05-19
 */
@RequiredArgsConstructor
public class LocalDate2StringConverter extends DateTimeFormatSupport {
    private final DateTimeFormatter format;
    private final Set<ConvertiblePair> pairs = Collections.singleton(new ConvertiblePair(LocalDate.class, String.class));

    @Override
    public Set<ConvertiblePair> getConvertibleTypes() {
        return pairs;
    }

    @Override
    public Object convert(Object source, @NotNull TypeDescriptor sourceType, @NotNull TypeDescriptor targetType) {
        final DateTimeFormatter fmt = getFormatter(targetType);
        return ((LocalDate) source).format(fmt == null ? format : fmt);
    }
}
