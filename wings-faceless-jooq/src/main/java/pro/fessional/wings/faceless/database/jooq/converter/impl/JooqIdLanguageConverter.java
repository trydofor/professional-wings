package pro.fessional.wings.faceless.database.jooq.converter.impl;

import pro.fessional.wings.faceless.database.jooq.converter.JooqConsEnumConverter;
import pro.fessional.wings.faceless.enums.auto.StandardLanguage;

/**
 * @author trydofor
 * @since 2021-01-18
 */
public class JooqIdLanguageConverter extends JooqConsEnumConverter<StandardLanguage> {
    public static final JooqIdLanguageConverter Instance = new JooqIdLanguageConverter();

    public JooqIdLanguageConverter() {
        super(StandardLanguage.class, StandardLanguage.values());
    }
}
