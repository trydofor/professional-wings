package pro.fessional.wings.warlock.spring.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Collections;
import java.util.Set;

/**
 * wings-warlock-i18n-77.properties
 *
 * @author trydofor
 * @see #Key
 * @since 2021-02-17
 */
@Data
@ConfigurationProperties(WarlockI18nProp.Key)
public class WarlockI18nProp {

    public static final String Key = "wings.warlock.i18n";

    /**
     * init ZoneId by StandardTimezoneEnum.
     *
     * @see #Key$zoneidEnum
     */
    private Set<String> zoneidEnum = Collections.emptySet();
    public static final String Key$zoneidEnum = Key + ".zoneid-enum";

    /**
     * init Locale by StandardLanguageEnum.
     *
     * @see #Key$localeEnum
     */
    private Set<String> localeEnum = Collections.emptySet();
    public static final String Key$localeEnum = Key + ".locale-enum";
}
