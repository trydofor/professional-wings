package pro.fessional.wings.slardar.serialize;

import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import pro.fessional.mirana.bits.Base64;

/**
 * Data accuracy is higher than Json, but the data structure may not, such as TreeMap or HashMap.
 *
 * @author trydofor
 * @since 2022-03-09
 */
public class KryoConversion implements ConversionService {

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
        if (source instanceof String) {
            final byte[] bytes = Base64.decode((String) source);
            return KryoSimple.readClassAndObject(bytes);
        }
        if (String.class.equals(targetType)) {
            final byte[] bytes = KryoSimple.writeClassAndObject(source);
            return (T) Base64.encode(bytes);
        }
        return null;
    }

    @SneakyThrows
    @Override
    public Object convert(Object source, TypeDescriptor sourceType, @NotNull TypeDescriptor targetType) {
        return convert(source, targetType.getType());
    }
}
