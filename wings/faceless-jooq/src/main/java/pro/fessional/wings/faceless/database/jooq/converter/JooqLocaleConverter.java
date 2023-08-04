package pro.fessional.wings.faceless.database.jooq.converter;

import org.jooq.impl.AbstractConverter;
import pro.fessional.mirana.i18n.LocaleResolver;

import java.util.Locale;

/**
 * Standard to en_US format
 *
 * @author trydofor
 * @since 2021-01-18
 */
public class JooqLocaleConverter extends AbstractConverter<String, Locale> {

    public JooqLocaleConverter() {
        super(String.class, Locale.class);
    }

    @Override
    public Locale from(String str) {
        return LocaleResolver.locale(str);
    }

    @Override
    public String to(Locale lcl) {
        // FastJson use sun.util.BaseLocale, `_` delimited.
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
}
