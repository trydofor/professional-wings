package pro.fessional.wings.faceless.database.jooq.converter.impl;

import pro.fessional.wings.faceless.database.jooq.converter.JooqConsEnumConverter;
import pro.fessional.wings.faceless.enums.auto.StandardTimezone;

/**
 * @author trydofor
 * @since 2021-01-18
 */
public class JooqIdTimezoneConverter extends JooqConsEnumConverter<StandardTimezone> {
    public static final JooqIdTimezoneConverter Instance = new JooqIdTimezoneConverter();

    public JooqIdTimezoneConverter() {
        super(StandardTimezone.class, StandardTimezone.values());
    }
}
