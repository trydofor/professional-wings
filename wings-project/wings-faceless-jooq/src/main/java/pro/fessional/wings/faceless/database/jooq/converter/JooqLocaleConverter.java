package pro.fessional.wings.faceless.database.jooq.converter;

import org.jetbrains.annotations.NotNull;
import org.jooq.Converter;
import pro.fessional.mirana.i18n.LocaleResolver;

import java.util.Locale;

/**
 * 统一成 en_US格式
 *
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
        // FastJson使用sun.util.BaseLocale，为`_`分隔
        String lt = lcl.getLanguage();
        String ct = lcl.getCountry();
        final int ln = lt.length();
        final int cn = ct.length();

        if (ln != 2 || cn != 2) {
            if (ln != 0) lt = lt.replace('-', '_');
            if (cn != 0) ct = ct.replace('-', '_');

            if (ct.isEmpty()) return lt;
            if (lt.isEmpty()) return ct;
        }

        return lt + "_" + ct;
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
