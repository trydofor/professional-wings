package pro.fessional.wings.faceless.database.jooq.converter;

import pro.fessional.wings.faceless.enums.auto.StandardLanguage;
import pro.fessional.wings.faceless.enums.auto.StandardTimezone;

/**
 * @author trydofor
 * @since 2021-01-15
 */
public class WingsEnumConverters {

    public static final ConsEnumConverter<StandardLanguage> LanguageIdConverter = ConsEnumConverter.of(StandardLanguage.class);
    public static final ConsEnumConverter<StandardTimezone> TimezoneIdConverter = ConsEnumConverter.of(StandardTimezone.class);

    public static final CodeEnumConverter<StandardLanguage> LanguageCodeConverter = CodeEnumConverter.of(StandardLanguage.class);
    public static final CodeEnumConverter<StandardTimezone> TimezoneCodeConverter = CodeEnumConverter.of(StandardTimezone.class);
}
