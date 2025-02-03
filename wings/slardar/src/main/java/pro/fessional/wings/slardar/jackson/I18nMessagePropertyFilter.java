package pro.fessional.wings.slardar.jackson;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import lombok.extern.slf4j.Slf4j;
import pro.fessional.mirana.best.ReadOnly;
import pro.fessional.mirana.data.R;
import pro.fessional.mirana.i18n.I18nAware;
import pro.fessional.mirana.i18n.I18nMessage;
import pro.fessional.mirana.i18n.I18nNotice;
import pro.fessional.wings.slardar.context.LocaleZoneIdUtil;

import java.util.List;
import java.util.Locale;

/**
 * @author trydofor
 * @since 2019-10-14
 */
@Slf4j
public class I18nMessagePropertyFilter implements AutoRegisterPropertyFilter {

    public static final String Id = "I18nMessage";

    @JsonFilter(Id)
    public static class I18nMessageMixin {
    }

    public static final Class<?> MixinClass = I18nMessage.class;

    private final I18nAware.I18nSource i18nSource;
    private final int resultCompatible;

    public I18nMessagePropertyFilter(I18nAware.I18nSource i18nSource, Integer resultCompatible) {
        this.i18nSource = i18nSource;
        this.resultCompatible = resultCompatible == null ? 0 : resultCompatible;
    }

    @Override
    public String getId() {
        return Id;
    }

    @Override
    public void serializeAsField(Object pojo, JsonGenerator gen, SerializerProvider prov, PropertyWriter writer) throws Exception {
        if (!(pojo instanceof ReadOnly)) {
            Locale locale = LocaleZoneIdUtil.LocaleNonnull();
            ((I18nMessage) pojo).setMessageBy(locale, i18nSource);

            if (resultCompatible > 0 && pojo instanceof R<?> rs && rs.getMessage() == null) {
                List<I18nNotice> errors = rs.getErrors();
                if (errors != null && !errors.isEmpty()) {
                    I18nNotice err = errors.getFirst();
                    rs.setMessage(err.getMessage());
                    if (resultCompatible > 1) {
                        rs.setI18nCode(err.getI18nCode());
                        rs.setI18nArgs(err.getI18nArgs());
                    }
                }
            }
        }
        writer.serializeAsField(pojo, gen, prov);
    }
}
