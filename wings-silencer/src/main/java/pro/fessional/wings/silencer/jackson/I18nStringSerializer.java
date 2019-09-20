package pro.fessional.wings.silencer.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import org.springframework.context.MessageSource;
import pro.fessional.mirana.i18n.I18nString;
import pro.fessional.wings.silencer.context.WingsI18nContext;

import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author trydofor
 * @since 2019-09-19
 */
public class I18nStringSerializer extends JsonSerializer implements ContextualSerializer {

    private final AtomicReference<I18nStringSerializer> oppositeOne = new AtomicReference<>();
    private final MessageSource messageSource;
    private final WingsI18nContext i18nContext;
    private final boolean enabled;

    public I18nStringSerializer(MessageSource messageSource, WingsI18nContext i18nContext, boolean enabled) {
        this.messageSource = messageSource;
        this.i18nContext = i18nContext;
        this.enabled = enabled;
    }

    @Override
    public void serialize(Object value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        if (value == null) return;

        Locale locale = null;
        if (i18nContext != null) locale = i18nContext.getLocale();
        if (locale == null) locale = provider.getLocale();

        if (value instanceof CharSequence) {
            String text = value.toString();
            if (enabled) text = messageSource.getMessage(text, new Object[]{}, locale);
            gen.writeString(text);
        } else if (value instanceof I18nString) {
            I18nString i18n = (I18nString) value;
            if (enabled) {
                String text = messageSource.getMessage(i18n.getCode(), i18n.getArgs(), locale);
                if (text.equalsIgnoreCase(i18n.getCode())) {
                    text = i18n.toString(locale);
                }
                gen.writeString(text);
            } else {
                gen.writeStartObject();
                gen.writeStringField("code", i18n.getCode());
                gen.writeStringField("hint", i18n.getHint());
                gen.writeFieldName("args");
                gen.writeObject(i18n.getArgs());
                gen.writeEndObject();
            }
        } else {
            provider.defaultSerializeValue(value, gen);
        }
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider provider, BeanProperty property) throws JsonMappingException {
        if (property == null) return this;
        JsonI18nString ann = property.getAnnotation(JsonI18nString.class);
        if (ann == null || ann.value() == enabled) return this;

        I18nStringSerializer that = oppositeOne.get();
        if (that == null) {
            that = new I18nStringSerializer(messageSource, i18nContext, !enabled);
            oppositeOne.set(that);
        }
        return that;
    }
}
