package pro.fessional.wings.slardar.autozone.spring;


import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.convert.converter.Converter;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

/**
 * ConversionService
 *
 * @author trydofor
 * @since 2021-03-22
 */

@RequiredArgsConstructor
public class ZonedStringConverter implements Converter<ZonedDateTime, String> {

    private final DateTimeFormatter formatter;

    @Override
    public String convert(@NotNull ZonedDateTime source) {
        final TimeZone tz = LocaleContextHolder.getTimeZone();
        final ZonedDateTime zdt = source.withZoneSameInstant(tz.toZoneId());
        return zdt.format(formatter);
    }
}
