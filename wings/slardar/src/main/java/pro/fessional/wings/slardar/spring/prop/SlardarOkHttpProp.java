package pro.fessional.wings.slardar.spring.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.File;

/**
 * @author trydofor
 * @see #Key
 * @since 2021-02-14
 */
@Data
@ConfigurationProperties(SlardarOkHttpProp.Key)
public class SlardarOkHttpProp {

    public static final String Key = "wings.slardar.okhttp";

    /**
     * 链接超时秒数
     *
     * @see #Key$timeoutConn
     */
    private int timeoutConn = 10;
    public static final String Key$timeoutConn = Key + ".timeout-conn";

    /**
     * 读取超时秒数
     *
     * @see #Key$timeoutRead
     */
    private int timeoutRead = 60;
    public static final String Key$timeoutRead = Key + ".timeout-read";

    /**
     * 写入超时秒数
     *
     * @see #Key$timeoutWrite
     */
    private int timeoutWrite = 60;
    public static final String Key$timeoutWrite = Key + ".timeout-write";

    /**
     * ping的间隔秒数，0为关闭
     *
     * @see #Key$pingInterval
     */
    private int pingInterval = 0;
    public static final String Key$pingInterval = Key + ".ping-interval";

    /**
     * 缓存大小，0表示不缓存
     *
     * @see #Key$cacheMegabyte
     */
    private int cacheMegabyte = 0;
    public static final String Key$cacheMegabyte = Key + ".cache-megabyte";

    /**
     * 缓存目录，默认在temp下创建 `wings-okhttp-cache`
     *
     * @see #Key$cacheDirectory
     */
    private File cacheDirectory = null;
    public static final String Key$cacheDirectory = Key + ".cache-directory";

    /**
     * 是否跟着跳转
     *
     * @see #Key$followRedirectSsl
     */
    private boolean followRedirectSsl = true;
    public static final String Key$followRedirectSsl = Key + ".follow-redirect-ssl";

    /**
     * 是否跟着跳转
     *
     * @see #Key$followRedirect
     */
    private boolean followRedirect = true;
    public static final String Key$followRedirect = Key + ".follow-redirect";

    /**
     * conn失败是否重试
     *
     * @see #Key$retryFailure
     */
    private boolean retryFailure = true;
    public static final String Key$retryFailure = Key + ".retry-failure";

    /**
     * 最大空闲conn数量
     *
     * @see #Key$maxIdle
     */
    private int maxIdle = 5;
    public static final String Key$maxIdle = Key + ".max-idle";

    /**
     * conn keep-alive秒数
     *
     * @see #Key$keepAlive
     */
    private int keepAlive = 300;
    public static final String Key$keepAlive = Key + ".keep-alive";

    /**
     * trust all ssl
     *
     * @see #Key$sslTrustAll
     */
    private boolean sslTrustAll = true;
    public static final String Key$sslTrustAll = Key + ".ssl-trust-all";

    /**
     * 是否以host为单位保留cookie
     *
     * @see #Key$hostCookie
     */
    private boolean hostCookie = true;
    public static final String Key$hostCookie = Key + ".host-cookie";

    /**
     * 是否在follow-redirect时，暂时不follow
     *
     * @see #Key$redirectNop
     */
    private boolean redirectNop = false;
    public static final String Key$redirectNop = Key + ".redirect-nop";
}
