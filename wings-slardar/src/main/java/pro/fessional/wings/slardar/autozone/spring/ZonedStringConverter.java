package pro.fessional.wings.slardar.autozone.spring;


import org.jetbrains.annotations.NotNull;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.convert.converter.Converter;
import pro.fessional.mirana.time.DateFormatter;

import java.time.ZonedDateTime;
import java.util.TimeZone;

/**
 * ConversionService
 *
 * @author trydofor
 * @since 2021-03-22
 */

public class ZonedStringConverter implements Converter<ZonedDateTime, String> {
    @Override
    public String convert(@NotNull ZonedDateTime source) {
        final TimeZone tz = LocaleContextHolder.getTimeZone();
        return DateFormatter.full19(source, tz.toZoneId());
    }
}
