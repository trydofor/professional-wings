package pro.fessional.wings.faceless.database.jooq.converter.impl;

import pro.fessional.wings.faceless.database.jooq.converter.JooqCodeEnumConverter;
import pro.fessional.wings.faceless.enums.auto.StandardTimezone;

/**
 * @author trydofor
 * @since 2021-01-18
 */
public class JooqCodeTimezoneConverter extends JooqCodeEnumConverter<StandardTimezone> {
    public static final JooqCodeTimezoneConverter Instance = new JooqCodeTimezoneConverter();

    public JooqCodeTimezoneConverter() {
        super(StandardTimezone.class, StandardTimezone.values());
    }
}
