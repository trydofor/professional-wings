package pro.fessional.wings.silencer.spring.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import pro.fessional.wings.silencer.spring.boot.WingsEnabledCondition;

/**
 * The default switch for toggling the Silencer feature, as follows:
 * spring-wings-enabled-79.properties
 *
 * @author trydofor
 * @see #Key
 * @since 2021-02-13
 */
@Data
@ConfigurationProperties(SilencerEnabledProp.Key)
public class SilencerEnabledProp {

    public static final String Key = WingsEnabledCondition.Prefix + ".silencer";

    /**
     * Whether to automatically configure, default true
     *
     * @see #Key$autoconf
     */
    private boolean autoconf = true;
    public static final String Key$autoconf = Key + ".autoconf";

    /**
     * Whether to display the conditional information of wings, default false
     *
     * @see #Key$verbose
     */
    private boolean verbose = false;
    public static final String Key$verbose = Key + ".verbose";

    /**
     * Whether to automatically load all classpaths ** /spring/bean/**, default true
     *
     * @see #Key$scanner
     */
    private boolean scanner = true;
    public static final String Key$scanner = Key + ".scanner";
}
