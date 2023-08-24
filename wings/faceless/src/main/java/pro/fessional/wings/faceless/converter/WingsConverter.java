package pro.fessional.wings.faceless.converter;

import org.jetbrains.annotations.Nullable;
import pro.fessional.mirana.cast.BiConvertor;

/**
 * Bidirectional conversion of Source and Target
 *
 * @author trydofor
 * @since 2021-01-17
 */
public interface WingsConverter<S, T> extends BiConvertor<S, T>,
        org.springframework.core.convert.converter.Converter<S, T> {

    @Override
    @Nullable
    default T convert(@Nullable S source) {
        return toTarget(source);
    }
}
