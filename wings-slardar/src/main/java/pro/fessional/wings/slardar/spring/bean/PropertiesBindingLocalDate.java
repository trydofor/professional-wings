package pro.fessional.wings.slardar.spring.bean;

import org.jetbrains.annotations.NotNull;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import pro.fessional.mirana.time.DateParser;

import java.time.LocalDate;

/**
 * @author trydofor
 * @since 2021-10-29
 */
@Component
@ConfigurationPropertiesBinding
public class PropertiesBindingLocalDate implements Converter<String, LocalDate> {
    @Override
    public LocalDate convert(@NotNull String source) {
        return DateParser.parseDate(source);
    }
}
