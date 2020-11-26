package pro.fessional.wings.slardar.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.context.MessageSource;
import org.springframework.util.StringUtils;
import pro.fessional.mirana.data.R;
import pro.fessional.wings.silencer.context.WingsI18nContext;

import java.io.IOException;
import java.util.Locale;

/**
 * @author trydofor
 * @since 2019-10-14
 */
public class I18nResultSerializer extends JsonSerializer<R.I<?>> {

    private final MessageSource messageSource;
    private final WingsI18nContext i18nContext;

    public I18nResultSerializer(MessageSource messageSource, WingsI18nContext i18nContext) {
        this.messageSource = messageSource;
        this.i18nContext = i18nContext;
    }

    @Override
    public void serialize(R.I<?> value, JsonGenerator gen, SerializerProvider provider) throws IOException {

        String i18nCode = value.getI18nCode();
        String message = value.getMessage();

        if (StringUtils.hasText(i18nCode)) {
            Locale locale = null;
            if (i18nContext != null) locale = i18nContext.getLocale();
            if (locale == null) locale = provider.getLocale();

            String i18n = messageSource.getMessage(i18nCode, value.getI18nArgs(), locale);
            if (StringUtils.hasText(i18n) && !i18n.equalsIgnoreCase(i18nCode)) {
                message = i18n;
            }
        }

        serialize(value, gen, provider, message);
    }


    private void serialize(R<?> value, JsonGenerator gen, SerializerProvider provider, String message) throws IOException {
        gen.writeStartObject();
        gen.writeBooleanField("success", value.isSuccess());
        //
        if (StringUtils.hasText(message)) {
            gen.writeStringField("message", message);
        }
        String code = value.getCode();
        if (StringUtils.hasText(code)) {
            gen.writeStringField("code", code);
        }
        gen.writeFieldName("data");
        provider.defaultSerializeValue(value.getData(), gen);
        gen.writeEndObject();
    }
}
