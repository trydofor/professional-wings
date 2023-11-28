package pro.fessional.wings.silencer.spring.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import pro.fessional.wings.silencer.spring.boot.WingsEnabledCondition;

/**
 * toggling the Silencer feature, wings-enabled-79.properties
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
     * Whether to automatically configure `wings-conf` and `wings-i18n`
     *
     * @see #Key$autoconf
     */
    private boolean autoconf = true;
    public static final String Key$autoconf = Key + ".autoconf";

    /**
     * Whether to display the conditional information of wings
     *
     * @see #Key$verbose
     */
    private boolean verbose = false;
    public static final String Key$verbose = Key + ".verbose";

    /**
     * Whether to Automatically scan component from `*&#42;/spring/bean/*&#42;/*.class` on ApplicationPreparedEvent before `@AutoConfiguration
     *
     * @see #Key$scanner
     */
    private boolean scanner = false;
    public static final String Key$scanner = Key + ".scanner";

    ////// abs-key for short /////////

    /**
     * Whether to audit the file and cascading relationship of properties key/value
     *
     * @see #Key$auditProp
     */
    private boolean auditProp = false;
    public static final String Key$auditProp = Key + ".audit-prop";

    /**
     * Whether to automatically switch the console log level when a log file is available
     *
     * @see #Key$muteConsole
     */
    private boolean muteConsole = true;
    public static final String Key$muteConsole = Key + ".mute-console";

    /**
     * Whether to tweak the clock in global or thread
     *
     * @see #Key$tweakClock
     */
    private boolean tweakClock = true;
    public static final String Key$tweakClock = Key + ".tweak-clock";

    /**
     * Whether to tweak log level of logback in global or thread
     *
     * @see #Key$tweakLogback
     */
    private boolean tweakLogback = true;
    public static final String Key$tweakLogback = Key + ".tweak-logback";

    /**
     * Whether to tweak the CodeException stack in global or thread
     *
     * @see #Key$tweakStack
     */
    private boolean tweakStack = true;
    public static final String Key$tweakStack = Key + ".tweak-stack";
}
