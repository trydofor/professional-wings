package pro.fessional.wings.slardar.jackson;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import lombok.extern.slf4j.Slf4j;
import pro.fessional.mirana.best.ReadOnly;
import pro.fessional.mirana.data.R;
import pro.fessional.mirana.i18n.I18nAware;
import pro.fessional.mirana.i18n.I18nNotice;
import pro.fessional.wings.slardar.context.LocaleZoneIdUtil;

import java.util.List;
import java.util.Locale;

/**
 * @author trydofor
 * @since 2019-10-14
 */
@Slf4j
public class I18nAwarePropertyFilter implements AutoRegisterPropertyFilter {

    public static final String Id = "I18nAware";

    @JsonFilter(Id)
    public static class I18nAwareMixin {
    }

    public static final Class<?> MixinClass = I18nAware.class;

    private final I18nAware.I18nSource i18nSource;
    private final int resultCompatible;

    public I18nAwarePropertyFilter(I18nAware.I18nSource i18nSource, Integer resultCompatible) {
        this.i18nSource = i18nSource;
        this.resultCompatible = resultCompatible == null ? 0 : resultCompatible;
    }

    @Override
    public String getId() {
        return Id;
    }

    @Override
    public void serializeAsField(Object pojo, JsonGenerator gen, SerializerProvider prov, PropertyWriter writer) throws Exception {
        applyLocale((I18nAware) pojo);
        writer.serializeAsField(pojo, gen, prov);
    }

    private void applyLocale(I18nAware ia) {
        if (ia instanceof ReadOnly) return;

        Locale locale = LocaleZoneIdUtil.LocaleNonnull();
        ia.applyLocale(locale, i18nSource);

        if (resultCompatible <= 0 || !(ia instanceof R<?> rs)) return;
        if (rs.getMessage() != null) return;

        List<I18nNotice> errors = rs.getErrors();
        if (errors == null || errors.isEmpty()) return;

        I18nNotice err = errors.getFirst();
        rs.setMessage(err.getMessage());
        if (resultCompatible > 1) {
            rs.setI18nCode(err.getI18nCode());
            rs.setI18nArgs(err.getI18nArgs());
        }
    }
}
