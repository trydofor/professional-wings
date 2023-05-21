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
     * connect timeout in seconds.
     *
     * @see #Key$timeoutConn
     */
    private int timeoutConn = 10;
    public static final String Key$timeoutConn = Key + ".timeout-conn";

    /**
     * read timeout in seconds.
     *
     * @see #Key$timeoutRead
     */
    private int timeoutRead = 60;
    public static final String Key$timeoutRead = Key + ".timeout-read";

    /**
     * write timeout in seconds.
     *
     * @see #Key$timeoutWrite
     */
    private int timeoutWrite = 60;
    public static final String Key$timeoutWrite = Key + ".timeout-write";

    /**
     * ping interval in seconds, `0` means disable
     *
     * @see #Key$pingInterval
     */
    private int pingInterval = 0;
    public static final String Key$pingInterval = Key + ".ping-interval";

    /**
     * cache size in `Mb`, `0` means disable
     *
     * @see #Key$cacheMegabyte
     */
    private int cacheMegabyte = 0;
    public static final String Key$cacheMegabyte = Key + ".cache-megabyte";

    /**
     * Cache directory, created under temp by default, `wings-okhttp-cache`
     *
     * @see #Key$cacheDirectory
     */
    private File cacheDirectory = null;
    public static final String Key$cacheDirectory = Key + ".cache-directory";

    /**
     * whether to follow the https redirect.
     *
     * @see #Key$followRedirectSsl
     */
    private boolean followRedirectSsl = true;
    public static final String Key$followRedirectSsl = Key + ".follow-redirect-ssl";

    /**
     * whether to follow the http redirect.
     *
     * @see #Key$followRedirect
     */
    private boolean followRedirect = true;
    public static final String Key$followRedirect = Key + ".follow-redirect";

    /**
     * whether to retry if connect failed.
     *
     * @see #Key$retryFailure
     */
    private boolean retryFailure = true;
    public static final String Key$retryFailure = Key + ".retry-failure";

    /**
     * max count of idle connection.
     *
     * @see #Key$maxIdle
     */
    private int maxIdle = 5;
    public static final String Key$maxIdle = Key + ".max-idle";

    /**
     * keep-alive in seconds.
     *
     * @see #Key$keepAlive
     */
    private int keepAlive = 300;
    public static final String Key$keepAlive = Key + ".keep-alive";

    /**
     * whether to trust all ssl certs.
     *
     * @see #Key$sslTrustAll
     */
    private boolean sslTrustAll = true;
    public static final String Key$sslTrustAll = Key + ".ssl-trust-all";

    /**
     * whether to keep cookies by host.
     *
     * @see #Key$hostCookie
     */
    private boolean hostCookie = true;
    public static final String Key$hostCookie = Key + ".host-cookie";

    /**
     * whether to temporarily do nothing when follow-redirect.
     *
     * @see #Key$redirectNop
     */
    private boolean redirectNop = false;
    public static final String Key$redirectNop = Key + ".redirect-nop";
}
