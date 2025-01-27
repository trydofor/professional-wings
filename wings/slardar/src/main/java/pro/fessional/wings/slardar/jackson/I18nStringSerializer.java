package pro.fessional.wings.slardar.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import lombok.RequiredArgsConstructor;
import pro.fessional.mirana.data.Null;
import pro.fessional.mirana.i18n.I18nAware.I18nSource;
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
@RequiredArgsConstructor
public class I18nStringSerializer extends JsonSerializer<Object> implements ContextualSerializer {

    private final AtomicReference<I18nStringSerializer> oppositeOne = new AtomicReference<>();
    private final I18nSource i18nSource;
    private final boolean enabled;

    @Override
    public void serialize(Object value, JsonGenerator generator, SerializerProvider provider) throws IOException {

        if (!(value instanceof CharSequence) && !(value instanceof I18nString)) {
            provider.defaultSerializeValue(value, generator);
            return;
        }

        if (value instanceof CharSequence) {
            String text = value.toString();
            if (enabled) {
                Locale locale = LocaleZoneIdUtil.LocaleNonnull();
                text = i18nSource.getMessage(text, Null.Objects, text, locale);
            }
            generator.writeString(text);
        }
        else { // value instanceof I18nString
            I18nString i18n = (I18nString) value;
            if (enabled) {
                Locale locale = LocaleZoneIdUtil.LocaleNonnull();
                String text = i18n.toString(locale, i18nSource);
                if (text == null || text.equalsIgnoreCase(i18n.getI18nCode())) {
                    text = i18n.toString();
                }
                generator.writeString(text);
            }
            else {
                generator.writeStartObject();
                generator.writeStringField("code", i18n.getI18nCode());
                generator.writeStringField("hint", i18n.getI18nHint());
                generator.writeFieldName("args");
                generator.writeObject(i18n.getI18nArgs());
                generator.writeEndObject();
            }
        }
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider provider, BeanProperty property) {
        if (property == null) return this;
        AutoI18nString ann = property.getAnnotation(AutoI18nString.class);
        if (ann == null || ann.value() == enabled) return this;

        I18nStringSerializer that = oppositeOne.get();
        // No sync required, no impact on results
        if (that == null) {
            that = new I18nStringSerializer(i18nSource, !enabled);
            oppositeOne.set(that);
        }
        return that;
    }
}
