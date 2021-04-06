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
     * 是否默认配置jackson
     *
     * @see #Key$jackson
     */
    private boolean jackson = true;
    public static final String Key$jackson = Key + ".jackson";

    /**
     * 是否默认配置okhttp3
     *
     * @see #Key$okhttp
     */
    private boolean okhttp = true;
    public static final String Key$okhttp = Key + ".okhttp";

    /**
     * 是否开启cache配置
     *
     * @see #Key$caching
     */
    private boolean caching = true;
    public static final String Key$caching = Key + ".caching";

    /**
     * 是否默认配置session
     *
     * @see #Key$session
     */
    private boolean session = true;
    public static final String Key$session = Key + ".session";

    /**
     * 是否默认配置session hazelcast
     *
     * @see #Key$sessionHazelcast
     */
    private boolean sessionHazelcast = true;
    public static final String Key$sessionHazelcast = Key + ".session-hazelcast";

    /**
     * 是否spring Async和线程池配置
     *
     * @see #Key$async
     */
    private boolean async = true;
    public static final String Key$async = Key + ".async";

    /**
     * 是否防范编辑权限提升
     *
     * @see #Key$righter
     */
    private boolean righter = true;
    public static final String Key$righter = Key + ".righter";

    /**
     * 是否开启FirstBlood默认的Interceptor和图形验证码
     *
     * @see #Key$firstBlood
     */
    private boolean firstBlood = true;
    public static final String Key$firstBlood = Key + ".first-blood";

    /**
     * @see #Key$firstBloodImage
     */
    private String firstBloodImage = "";
    public static final String Key$firstBloodImage = Key + ".first-blood-image";

    /**
     * 是否开启DoubleKill默认的AOP
     *
     * @see #Key$doubleKill
     */
    private boolean doubleKill = true;
    public static final String Key$doubleKill = Key + ".double-kill";

    /**
     * 是否开启DoubleKill默认的ExceptionHandle
     *
     * @see #Key$doubleKillHandler
     */
    private boolean doubleKillHandler = true;
    public static final String Key$doubleKillHandler = Key + ".double-kill-handler";

    /**
     * 是否开启wings的PageQuery webmvc resolver
     *
     * @see #Key$pagequery
     */
    private boolean pagequery = true;
    public static final String Key$pagequery = Key + ".pagequery";

    /**
     * 是否开启wings的 webmvc local datetime converter
     *
     * @see #Key$datetime
     */
    private boolean datetime = true;
    public static final String Key$datetime = Key + ".datetime";

    /**
     * 是否配置undertow ws for UT026010: Buffer pool
     *
     * @see #Key$undertowWs
     */
    private boolean undertowWs = false;
    public static final String Key$undertowWs = Key + ".undertow-ws";

    /**
     * 是否开启terminal Resolver
     *
     * @see #Key$remote
     */
    private boolean remote = true;
    public static final String Key$remote = Key + ".remote";

    /**
     * 是否开启i18n Resolver
     *
     * @see #Key$locale
     */
    private boolean locale = true;
    public static final String Key$locale = Key + ".locale";

    /**
     * 是否解析 WingsTerminalContext
     *
     * @see #Key$terminal
     */
    private boolean terminal = true;
    public static final String Key$terminal = Key + ".terminal";

    /**
     * 是否开启captcha配置
     *
     * @see #Key$captcha
     */
    private boolean captcha = false;
    public static final String Key$captcha = Key + ".captcha";

    /**
     * 是否开启熔断设置
     *
     * @see #Key$overload
     */
    private boolean overload = false;
    public static final String Key$overload = Key + ".overload";

    /**
     * 是否支持 domain-extend
     *
     * @see #Key$domainExtend
     */
    private boolean domainExtend = false;
    public static final String Key$domainExtend = Key + ".domain-extend";

}
