package pro.fessional.wings.faceless.database.jooq.converter.impl;

import pro.fessional.wings.faceless.database.jooq.converter.JooqCodeEnumConverter;
import pro.fessional.wings.faceless.enums.auto.StandardLanguage;

/**
 * @author trydofor
 * @since 2021-01-18
 */
public class JooqCodeLanguageConverter extends JooqCodeEnumConverter<StandardLanguage> {
    public static final JooqCodeLanguageConverter Instance = new JooqCodeLanguageConverter();

    public JooqCodeLanguageConverter() {
        super(StandardLanguage.class, StandardLanguage.values());
    }
}
