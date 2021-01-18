package pro.fessional.wings.faceless.converter;

import pro.fessional.wings.faceless.enums.auto.StandardLanguage;
import pro.fessional.wings.faceless.enums.auto.StandardTimezone;

/**
 * @author trydofor
 * @since 2021-01-15
 */
public class WingsEnumConverters {

    public static final ConsEnumConverter<StandardLanguage> Id2Language = ConsEnumConverter.of(StandardLanguage.class);
    public static final ConsEnumConverter<StandardTimezone> Id2Timezone = ConsEnumConverter.of(StandardTimezone.class);

    public static final CodeEnumConverter<StandardLanguage> Code2Language = CodeEnumConverter.of(StandardLanguage.class);
    public static final CodeEnumConverter<StandardTimezone> Code2Timezone = CodeEnumConverter.of(StandardTimezone.class);
}
