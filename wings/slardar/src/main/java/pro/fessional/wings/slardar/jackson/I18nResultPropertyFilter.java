package pro.fessional.wings.slardar.jackson;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.util.StringUtils;
import pro.fessional.mirana.data.R;
import pro.fessional.wings.slardar.context.LocaleZoneIdUtil;

import java.util.Locale;

/**
 * @author trydofor
 * @since 2019-10-14
 */
@Slf4j
@RequiredArgsConstructor
public class I18nResultPropertyFilter implements AutoRegisterPropertyFilter {

    public static final String Id = "I18nResult";

    @JsonFilter(Id)
    public static class I18nResultMixin {
    }

    private final MessageSource messageSource;

    @Override
    public String getId() {
        return Id;
    }

    @Override
    public void serializeAsField(Object pojo, JsonGenerator gen, SerializerProvider prov, PropertyWriter writer) throws Exception {
        if ("message".equals(writer.getName())) {
            try {
                final R<?> value = (R<?>) pojo;
                String i18nCode = value.getI18nCode();
                if (StringUtils.hasText(i18nCode)) {
                    Locale locale = LocaleZoneIdUtil.LocaleNonnull.get();
                    String i18n = messageSource.getMessage(i18nCode, value.getI18nArgs(), locale);
                    if (StringUtils.hasText(i18n) && !i18n.equalsIgnoreCase(i18nCode)) {
                        value.setMessage(i18n);
                    }
                }
            }
            catch (Exception ex) {
                if (log.isDebugEnabled()) {
                    log.warn("failed to trans i18n code to message", ex);
                }
            }
        }
        writer.serializeAsField(pojo, gen, prov);
    }
}
