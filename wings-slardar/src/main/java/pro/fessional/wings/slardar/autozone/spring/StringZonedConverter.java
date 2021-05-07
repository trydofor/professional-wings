package pro.fessional.wings.slardar.autozone.spring;


import org.jetbrains.annotations.NotNull;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.convert.converter.Converter;
import pro.fessional.mirana.time.DateLocaling;
import pro.fessional.mirana.time.DateParser;

import java.time.ZoneId;
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
        final DateParser.Zdt pdt = DateParser.parseZoned(source);
        final ZonedDateTime zdt;
        if (pdt.zid != null) {
            zdt = ZonedDateTime.of(pdt.ldt, pdt.zid);
        }
        else {
            final TimeZone tz = LocaleContextHolder.getTimeZone();
            zdt = ZonedDateTime.of(pdt.ldt, tz.toZoneId());
        }
        return DateLocaling.zoneZone(zdt, ZoneId.systemDefault());
    }
}
