package pro.fessional.wings.faceless.database.jooq.converter;

import org.jetbrains.annotations.NotNull;
import org.jooq.Converter;
import pro.fessional.mirana.i18n.LocaleResolver;

import java.util.Locale;

/**
 * @author trydofor
 * @since 2021-01-18
 */
public class JooqLocaleConverter implements Converter<String, Locale> {

    @Override
    public Locale from(String str) {
        return LocaleResolver.locale(str);
    }

    @Override
    public String to(Locale lcl) {
        return lcl.getLanguage() + "_" + lcl.getCountry();
    }

    @Override
    public @NotNull Class<String> fromType() {
        return String.class;
    }

    @Override
    public @NotNull Class<Locale> toType() {
        return Locale.class;
    }
}
