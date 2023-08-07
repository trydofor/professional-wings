package pro.fessional.wings.slardar.serialize;

import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import pro.fessional.wings.slardar.fastjson.FastJsonHelper;

/**
 * The underlying implementation is FastJson,
 * which is limited to simple types of trusted services,
 * and may have security issues in non-trusted domains.
 * For complex types with generalizations, fastjson parses them correctly,
 * in addition to paying more attention to the precision of number in js.
 *
 * @author trydofor
 * @since 2022-03-09
 */
public class JsonConversion implements ConversionService {

    @Override
    public boolean canConvert(Class<?> sourceType, @NotNull Class<?> targetType) {
        return String.class.equals(sourceType) || String.class.equals(targetType);
    }

    @Override
    public boolean canConvert(TypeDescriptor sourceType, @NotNull TypeDescriptor targetType) {
        return (sourceType != null && String.class.equals(sourceType.getType())) ||
               (String.class.equals(targetType.getType()));
    }

    @SuppressWarnings("unchecked")
    @SneakyThrows
    @Override
    public <T> T convert(Object source, @NotNull Class<T> targetType) {
        if (String.class.equals(targetType)) {
            if (source instanceof String) {
                return (T) source;
            }
            else {
                return (T) FastJsonHelper.string(source);
            }
        }

        if (source instanceof String) {
            return FastJsonHelper.object((String) source, targetType);
        }
        return null;
    }

    @SneakyThrows
    @Override
    public Object convert(Object source, TypeDescriptor sourceType, @NotNull TypeDescriptor targetType) {

        if (String.class.equals(targetType.getType())) {
            if (source instanceof String) {
                return source;
            }
            else {
                return FastJsonHelper.string(source);
            }
        }

        if (source instanceof String) {
            return FastJsonHelper.object((String) source, targetType);
        }
        return null;
    }
}
