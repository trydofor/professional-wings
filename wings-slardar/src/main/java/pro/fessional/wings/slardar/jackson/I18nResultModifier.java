package pro.fessional.wings.slardar.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.util.StringUtils;
import pro.fessional.mirana.data.R;

import java.io.IOException;
import java.util.Locale;

/**
 * @author trydofor
 * @since 2019-10-14
 */
@Slf4j
public class I18nResultModifier extends BeanSerializerModifier {

    private final MessageSource messageSource;

    public I18nResultModifier(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    @SuppressWarnings("unchecked")
    public JsonSerializer<?> modifySerializer(SerializationConfig config, BeanDescription beanDesc, JsonSerializer<?> serializer) {
        if (beanDesc.getBeanClass().isAssignableFrom(R.class)) {
            return new I18nResultSerializer((JsonSerializer<Object>) serializer, messageSource);
        }
        else {
            return super.modifySerializer(config, beanDesc, serializer);
        }
    }

    @RequiredArgsConstructor
    public static class I18nResultSerializer extends JsonSerializer<R<?>> {
        private final JsonSerializer<Object> serializer;
        private final MessageSource messageSource;

        @Override
        public void serialize(R<?> value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            String i18nCode = value.getI18nCode();
            if (StringUtils.hasText(i18nCode)) {
                Locale locale = LocaleContextHolder.getLocale();
                try {
                    String i18n = messageSource.getMessage(i18nCode, value.getI18nArgs(), locale);
                    if (StringUtils.hasText(i18n) && !i18n.equalsIgnoreCase(i18nCode)) {
                        value.setMessage(i18n);
                    }
                }
                catch (NoSuchMessageException e) {
                    log.error("failed to i18n code=" + i18nCode, e);
                    throw e;
                }
            }
            serializer.serialize(value, gen, provider);
        }
    }
}
