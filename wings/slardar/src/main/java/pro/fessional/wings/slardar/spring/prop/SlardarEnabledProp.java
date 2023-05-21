package pro.fessional.wings.slardar.spring.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * spring-wings-enabled-79.properties
 *
 * @author trydofor
 * @see #Key
 * @since 2021-02-13
 */
@Data
@ConfigurationProperties(SlardarEnabledProp.Key)
public class SlardarEnabledProp {

    public static final String Key = "spring.wings.slardar.enabled";

    /**
     * whether to enable auto-config
     *
     * @see #Key$autoconf
     */
    private boolean autoconf = true;
    public static final String Key$autoconf = Key + ".autoconf";

    /**
     * whether to enable jackson default config
     *
     * @see #Key$jackson
     */
    private boolean jackson = true;
    public static final String Key$jackson = Key + ".jackson";

    /**
     * whether to enable okhttp3 default config
     *
     * @see #Key$okhttp
     */
    private boolean okhttp = true;
    public static final String Key$okhttp = Key + ".okhttp";

    /**
     * whether to enable cache config
     *
     * @see #Key$caching
     */
    private boolean caching = true;
    public static final String Key$caching = Key + ".caching";
    /**
     * whether to enable cache enhancement of aop
     *
     * @see #Key$cachingAop
     */
    private boolean cachingAop = true;
    public static final String Key$cachingAop = Key + ".caching-aop";

    /**
     * whether to enable cookie customization
     *
     * @see #Key$cookie
     */
    private boolean cookie = true;
    public static final String Key$cookie = Key + ".cookie";

    /**
     * whether to enable session default config
     *
     * @see #Key$session
     */
    private boolean session = true;
    public static final String Key$session = Key + ".session";

    /**
     * whether to enable session hazelcast config
     *
     * @see #Key$sessionHazelcast
     */
    private boolean sessionHazelcast = true;
    public static final String Key$sessionHazelcast = Key + ".session-hazelcast";

    /**
     * whether to enable spring Async and thread pool
     *
     * @see #Key$async
     */
    private boolean async = true;
    public static final String Key$async = Key + ".async";

    /**
     * whether to prevent forgery editing
     *
     * @see #Key$righter
     */
    private boolean righter = true;
    public static final String Key$righter = Key + ".righter";

    /**
     * whether to enable Interceptor of debounce
     *
     * @see #Key$debounce
     */
    private boolean debounce = true;
    public static final String Key$debounce = Key + ".debounce";

    /**
     * whether to enable reuse stream filter
     *
     * @see #Key$restream
     */
    private boolean restream = true;
    public static final String Key$restream = Key + ".restream";

    /**
     * whether to enable Interceptor of FirstBlood
     *
     * @see #Key$firstBlood
     */
    private boolean firstBlood = true;
    public static final String Key$firstBlood = Key + ".first-blood";

    /**
     * whether to enable image captcha Interceptor of FirstBlood
     *
     * @see #Key$firstBloodImage
     */
    private String firstBloodImage = "";
    public static final String Key$firstBloodImage = Key + ".first-blood-image";

    /**
     * whether to enable DoubleKill default AOP
     *
     * @see #Key$doubleKill
     */
    private boolean doubleKill = true;
    public static final String Key$doubleKill = Key + ".double-kill";

    /**
     * whether to enable PageQuery webmvc resolver of Wings
     *
     * @see #Key$pagequery
     */
    private boolean pagequery = true;
    public static final String Key$pagequery = Key + ".pagequery";

    /**
     * whether to enable webmvc localdatetime converter of Wings
     *
     * @see #Key$datetime
     */
    private boolean datetime = true;
    public static final String Key$datetime = Key + ".datetime";

    /**
     * whether to enable the Jackson precision limit of wings for Double, Float, BigDecimal
     *
     * @see #Key$number
     */
    private boolean number = true;
    public static final String Key$number = Key + ".number";

    /**
     * whether to enable serialization of the resource into a readable URL with Wings' Jackson config
     *
     * @see #Key$resource
     */
    private boolean resource = true;
    public static final String Key$resource = Key + ".resource";

    /**
     * whether to disable "undertow ws for UT026010: Buffer pool"
     *
     * @see #Key$undertowWs
     */
    private boolean undertowWs = true;
    public static final String Key$undertowWs = Key + ".undertow-ws";

    /**
     * whether to enable remote Resolver
     *
     * @see #Key$remote
     */
    private boolean remote = true;
    public static final String Key$remote = Key + ".remote";

    /**
     * whether to enable i18n Resolver
     *
     * @see #Key$locale
     */
    private boolean locale = true;
    public static final String Key$locale = Key + ".locale";

    /**
     * whether to enable WingsTerminalContext
     *
     * @see #Key$terminal
     */
    private boolean terminal = true;
    public static final String Key$terminal = Key + ".terminal";

    /**
     * whether to enable captcha config
     *
     * @see #Key$captcha
     */
    private boolean captcha = false;
    public static final String Key$captcha = Key + ".captcha";

    /**
     * whether to enable overload filter
     *
     * @see #Key$overload
     */
    private boolean overload = false;
    public static final String Key$overload = Key + ".overload";

    /**
     * whether to enable domain-extend
     *
     * @see #Key$domainExtend
     */
    private boolean domainExtend = false;
    public static final String Key$domainExtend = Key + ".domain-extend";

    /**
     * whether to enable swagger config
     *
     * @see #Key$swagger
     */
    private boolean swagger = true;
    public static final String Key$swagger = Key + ".swagger";

    /**
     * Start as standalone at devtool to reduce cluster impact
     * see <a href="https://github.com/hazelcast/hazelcast-simulator/blob/master/README.md">hazelcast-simulator</a>
     *
     * @see #Key$mockHazelcast
     */
    private boolean mockHazelcast = false;
    public static final String Key$mockHazelcast = Key + ".mock-hazelcast";

    /**
     * whether to enable monitor
     *
     * @see #Key$monitor
     */
    private boolean monitor = true;
    public static final String Key$monitor = Key + ".monitor";

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
     * whether to enable SpringBootAdmin config
     *
     * @see #Key$bootAdmin
     */
    private boolean bootAdmin = true;
    public static final String Key$bootAdmin = Key + ".boot-admin";

    /**
     * whether to enable dynamic tweaking
     *
     * @see #Key$tweaking
     */
    private boolean tweaking = true;
    public static final String Key$tweaking = Key + ".tweaking";
}
