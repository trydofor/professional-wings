package pro.fessional.wings.slardar.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import org.springframework.context.MessageSource;
import pro.fessional.mirana.i18n.I18nString;
import pro.fessional.wings.slardar.autodto.AutoI18nString;
import pro.fessional.wings.slardar.context.LocaleZoneIdUtil;

import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author trydofor
 * @since 2019-09-19
 */
public class I18nStringSerializer extends JsonSerializer<Object> implements ContextualSerializer {

    private final AtomicReference<I18nStringSerializer> oppositeOne = new AtomicReference<>();
    private final MessageSource messageSource;
    private final boolean enabled;

    public I18nStringSerializer(MessageSource messageSource, boolean enabled) {
        this.messageSource = messageSource;
        this.enabled = enabled;
    }

    @Override
    public void serialize(Object value, JsonGenerator gen, SerializerProvider provider) throws IOException {

        if (!(value instanceof CharSequence) && !(value instanceof I18nString)) {
            provider.defaultSerializeValue(value, gen);
            return;
        }

        if (value instanceof CharSequence) {
            String text = value.toString();
            if (enabled){
                Locale locale = LocaleZoneIdUtil.LocaleNonnull.get();
                text = messageSource.getMessage(text, new Object[]{}, locale);
            }
            gen.writeString(text);
        } else { // value instanceof I18nString
            I18nString i18n = (I18nString) value;
            if (enabled) {
                Locale locale = LocaleZoneIdUtil.LocaleNonnull.get();
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
        }
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider provider, BeanProperty property) {
        if (property == null) return this;
        AutoI18nString ann = property.getAnnotation(AutoI18nString.class);
        if (ann == null || ann.value() == enabled) return this;

        I18nStringSerializer that = oppositeOne.get();
        // 不需要同步，不影响结果
        if (that == null) {
            that = new I18nStringSerializer(messageSource, !enabled);
            oppositeOne.set(that);
        }
        return that;
    }
}
