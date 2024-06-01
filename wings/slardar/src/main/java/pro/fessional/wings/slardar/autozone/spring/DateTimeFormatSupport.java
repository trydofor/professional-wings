package pro.fessional.wings.slardar.autozone.spring;

import org.springframework.context.support.EmbeddedValueResolutionSupport;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.datetime.standard.DateTimeFormatterFactory;
import org.springframework.util.StringUtils;

import java.time.format.DateTimeFormatter;

/**
 * @author trydofor
 * @since 2021-05-19
 */
public abstract class DateTimeFormatSupport extends EmbeddedValueResolutionSupport implements GenericConverter {

    protected DateTimeFormatter getFormatter(TypeDescriptor descriptor) {
        final DateTimeFormat annotation = descriptor.getAnnotation(DateTimeFormat.class);
        if (annotation == null) return null;

        DateTimeFormatterFactory factory = new DateTimeFormatterFactory();
        String style = resolveEmbeddedValue(annotation.style());
        if (StringUtils.hasLength(style)) {
            factory.setStylePattern(style);
        }

        factory.setIso(annotation.iso());
        String pattern = resolveEmbeddedValue(annotation.pattern());
        if (StringUtils.hasLength(pattern)) {
            factory.setPattern(pattern);
        }
        return factory.createDateTimeFormatter();
    }
}
