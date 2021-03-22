package pro.fessional.wings.slardar.autozone.spring;


import org.jetbrains.annotations.NotNull;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.convert.converter.Converter;
import pro.fessional.mirana.time.DateParser;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.TimeZone;

/**
 * ConversionService
 *
 * @author trydofor
 * @since 2021-03-22
 */

public class StringZonedConverter implements Converter<String, ZonedDateTime> {
    @Override
    public ZonedDateTime convert(@NotNull String source) {
        final LocalDateTime ldt = DateParser.parseDateTime(source);
        final TimeZone tz = LocaleContextHolder.getTimeZone();
        return ZonedDateTime.of(ldt, tz.toZoneId());
    }
}
