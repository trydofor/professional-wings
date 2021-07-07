package pro.fessional.wings.slardar.autozone.spring;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.TypeDescriptor;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Set;

/**
 * @author trydofor
 * @since 2021-05-19
 */
@RequiredArgsConstructor
public class LocalTime2StringConverter extends DateTimeFormatSupport {
    private final DateTimeFormatter format;
    private final Set<ConvertiblePair> pairs = Collections.singleton(new ConvertiblePair(LocalTime.class, String.class));

    @Override
    public Set<ConvertiblePair> getConvertibleTypes() {
        return pairs;
    }

    @Override
    public Object convert(Object source, @NotNull TypeDescriptor sourceType, @NotNull TypeDescriptor targetType) {
        final DateTimeFormatter fmt = getFormatter(targetType);
        return ((LocalTime) source).format(fmt == null ? format : fmt);
    }
}
