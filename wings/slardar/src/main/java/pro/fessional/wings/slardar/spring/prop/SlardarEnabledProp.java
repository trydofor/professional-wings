package pro.fessional.wings.slardar.spring.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import pro.fessional.wings.silencer.spring.boot.WingsEnabledCondition;

/**
 * wings-enabled-79.properties
 *
 * @author trydofor
 * @see #Key
 * @since 2021-02-13
 */
@Data
@ConfigurationProperties(SlardarEnabledProp.Key)
public class SlardarEnabledProp {

    public static final String Key = WingsEnabledCondition.Prefix + ".slardar";

    /**
     * whether to enable DoubleKill by DoubleKillAround AOP
     *
     * @see #Key$doubleKill
     */
    private boolean doubleKill = true;
    public static final String Key$doubleKill = Key + ".double-kill";

    /**
     * whether to enable Jvm monitor
     *
     * @see #Key$monitorJvm
     */
    private boolean monitorJvm = true;
    public static final String Key$monitorJvm = Key + ".monitor-jvm";

    /**
     * whether to enable Log monitor
     *
     * @see #Key$monitorLog
     */
    private boolean monitorLog = true;
    public static final String Key$monitorLog = Key + ".monitor-log";


    /**
     * Start as standalone at devtool to reduce cluster impact
     * see <a href="https://github.com/hazelcast/hazelcast-simulator/blob/master/README.md">hazelcast-simulator</a>
     *
     * @see #Key$hazelcastStandalone
     */
    private boolean hazelcastStandalone = false;
    public static final String Key$hazelcastStandalone = Key + ".hazelcast-standalone";


    /**
     * whether to enable cookie customization
     *
     * @see #Key$cookie
     */
    private boolean cookie = false;
    public static final String Key$cookie = Key + ".cookie";

    /**
     * whether to enable Interceptor of debounce
     *
     * @see #Key$debounce
     */
    private boolean debounce = true;
    public static final String Key$debounce = Key + ".debounce";

    /**
     * whether to enable domain-extend
     *
     * @see #Key$domainx
     */
    private boolean domainx = false;
    public static final String Key$domainx = Key + ".domainx";

    /**
     * whether to enable Interceptor of FirstBlood
     *
     * @see #Key$firstBlood
     */
    private boolean firstBlood = false;
    public static final String Key$firstBlood = Key + ".first-blood";

    /**
     * whether to enable image captcha Interceptor of FirstBlood
     *
     * @see #Key$firstBloodImage
     */
    private boolean firstBloodImage = true;
    public static final String Key$firstBloodImage = Key + ".first-blood-image";

    /**
     * whether to enable webmvc date/time converter of Wings
     *
     * @see #Key$jacksonDatetime
     */
    private boolean jacksonDatetime = true;
    public static final String Key$jacksonDatetime = Key + ".jackson-datetime";

    /**
     * whether to enable webmvc empty converter of Wings
     *
     * @see #Key$jacksonEmpty
     */
    private boolean jacksonEmpty = true;
    public static final String Key$jacksonEmpty = Key + ".jackson-empty";

    /**
     * whether to enable the Jackson precision limit of wings for Double, Float, BigDecimal
     *
     * @see #Key$jacksonNumber
     */
    private boolean jacksonNumber = true;
    public static final String Key$jacksonNumber = Key + ".jackson-number";

    /**
     * whether to enable serialization of the resource into a readable URL with Wings' Jackson config
     *
     * @see #Key$jacksonResource
     */
    private boolean jacksonResource = true;
    public static final String Key$jacksonResource = Key + ".jackson-resource";

    /**
     * whether to enable webmvc result converter of Wings
     *
     * @see #Key$jacksonResult
     */
    private boolean jacksonResult = true;
    public static final String Key$jacksonResult = Key + ".jackson-result";

    /**
     * whether to enable PageQuery webmvc resolver of Wings
     *
     * @see #Key$pagequery
     */
    private boolean pagequery = true;
    public static final String Key$pagequery = Key + ".pagequery";

    /**
     * whether to enable reuse stream filter
     *
     * @see #Key$restream
     */
    private boolean restream = true;
    public static final String Key$restream = Key + ".restream";


    /**
     * whether to prevent forgery editing
     *
     * @see #Key$righter
     */
    private boolean righter = true;
    public static final String Key$righter = Key + ".righter";

    /**
     * whether to enable wings session customization
     *
     * @see #Key$session
     */
    private boolean session = true;
    public static final String Key$session = Key + ".session";

    /**
     * whether to enable swagger config
     *
     * @see #Key$swagger
     */
    private boolean swagger = true;
    public static final String Key$swagger = Key + ".swagger";

    /**
     * whether to enable WingsTerminalContext
     *
     * @see #Key$terminal
     */
    private boolean terminal = true;
    public static final String Key$terminal = Key + ".terminal";
}
