package pro.fessional.wings.slardar.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.RequiredArgsConstructor;
import pro.fessional.mirana.i18n.I18nAware.I18nSource;
import pro.fessional.mirana.i18n.I18nMessage;
import pro.fessional.wings.slardar.context.LocaleZoneIdUtil;

import java.io.IOException;
import java.util.Locale;

/**
 * @author trydofor
 * @since 2019-09-19
 */
@RequiredArgsConstructor
public class I18nMessageSerializer extends JsonSerializer<Object> {

    private final I18nSource i18nSource;

    @Override
    public void serialize(Object value, JsonGenerator generator, SerializerProvider provider) throws IOException {
        if (value instanceof I18nMessage im) {
            Locale locale = LocaleZoneIdUtil.LocaleNonnull();
            im.setMessageBy(locale, i18nSource);
            generator.writeObject(im);
        }
        else {
            provider.defaultSerializeValue(value, generator);
        }
    }
}
