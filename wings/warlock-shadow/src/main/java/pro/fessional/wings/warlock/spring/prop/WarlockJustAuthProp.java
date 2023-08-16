package pro.fessional.wings.warlock.spring.prop;

import lombok.Data;
import me.zhyd.oauth.config.AuthConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.Proxy;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Oauth login support, use just-auth.
 * wings-warlock-justauth-77.properties
 *
 * @author trydofor
 * @see #Key
 * @since 2021-02-17
 */
@Data
@ConfigurationProperties(WarlockJustAuthProp.Key)
public class WarlockJustAuthProp {

    public static final String Key = "wings.warlock.just-auth";

    /**
     * cache capacity
     *
     * @see #Key$cacheSize
     */
    private int cacheSize = 5000;
    public static final String Key$cacheSize = Key + ".cache-size";

    /**
     * ttl seconds, expireAfterWrite
     *
     * @see #Key$cacheLive
     */
    private int cacheLive = 300;
    public static final String Key$cacheLive = Key + ".cache-live";

    /**
     * default `/login`=`{1}/#{0}{2}`
     * <p>
     * Set secure state, get content by key, perform redirects (starting with `http` or `/`) or write-back.
     * The content supports a placeholder template in `MessageFormat` format, with `{0}` as the key.
     * If it starts with `http`, then it detects if it is safe-host.
     *
     * @see #Key$safeState
     */
    private Map<String, String> safeState = new HashMap<>();
    public static final String Key$safeState = Key + ".safe-state";


    /**
     * Set secure host, reduce cross-domain when dev, can raise `redirect_uri_mismatch` error.
     *
     * @see #Key$safeHost
     */
    private Set<String> safeHost = new HashSet<>();
    public static final String Key$safeHost = Key + ".safe-host";

    /**
     * key use `wings.warlock.security.auth-type.*`,
     * support `{host}`,`{scheme}`,`{authType}`,`{authZone}` variables, according to request.
     *
     * @see WarlockSecurityProp#Key$authType
     * @see #Key$authType
     */
    private Map<String, AuthConfig> authType = new HashMap<>();
    public static final String Key$authType = Key + ".auth-type";


    /**
     * if you don't need a proxy, just set proxy-type=DIRECT or host=null.
     *
     * @see WarlockSecurityProp#Key$authType
     * @see #Key$httpConf
     */
    private Map<String, Http> httpConf = new HashMap<>();
    public static final String Key$httpConf = Key + ".http-conf";


    @Data
    public static class Http {
        /**
         * in seconds, NOT just-auth's millis.
         */
        private int timeout;
        /**
         * proxy type
         */
        private String proxyType = Proxy.Type.HTTP.name();
        /**
         * proxy host
         */
        private String proxyHost;
        /**
         * proxy port
         */
        private int proxyPort;
    }
}
