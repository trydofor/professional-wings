package pro.fessional.wings.slardar.jackson;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pro.fessional.mirana.data.R;
import pro.fessional.mirana.i18n.I18nAware;
import pro.fessional.mirana.i18n.I18nMessage;
import pro.fessional.wings.slardar.context.LocaleZoneIdUtil;

import java.util.Locale;

/**
 * @author trydofor
 * @since 2019-10-14
 */
@Slf4j
@RequiredArgsConstructor
public class I18nMessagePropertyFilter implements AutoRegisterPropertyFilter {

    public static final String Id = "I18nMessage";

    @JsonFilter(Id)
    public static class I18nMessageMixin {
    }

    public static final Class<?> MixinClass = I18nMessage.class;

    private final I18nAware.I18nSource i18nSource;

    @Override
    public String getId() {
        return Id;
    }

    @Override
    public void serializeAsField(Object pojo, JsonGenerator gen, SerializerProvider prov, PropertyWriter writer) throws Exception {

        if (!(pojo instanceof R.Immutable)) {
            Locale locale = LocaleZoneIdUtil.LocaleNonnull();
            ((I18nMessage) pojo).setMessageBy(locale, i18nSource);
        }
        writer.serializeAsField(pojo, gen, prov);
    }
}
