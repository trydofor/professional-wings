package pro.fessional.wings.slardar.spring.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import pro.fessional.wings.slardar.servlet.cookie.WingsCookieInterceptor;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import static pro.fessional.wings.slardar.servlet.cookie.WingsCookieInterceptor.Coder.Aes;

/**
 * All the following name settings are the original, that is, without prefix and alias.
 *
 * @author trydofor
 * @see #Key
 * @since 2021-02-14
 */
@Data
@ConfigurationProperties(SlardarCookieProp.Key)
public class SlardarCookieProp {

    public static final String Key = "wings.slardar.cookie";

    /**
     * Cookie prefix, empty by default.
     *
     * @see #Key$prefix
     */
    private String prefix = "";
    public static final String Key$prefix = Key + ".prefix";

    /**
     * cookie alias, affected by the prefix, eg.
     * `session`=`o_0`, `session` eventually is `${prefix}o_0`
     *
     * @see #Key$alias
     */
    private Map<String, String> alias = Collections.emptyMap();
    public static final String Key$alias = Key + ".alias";

    /**
     * <pre>
     * cookie encoding, default
     * - `aes` - aes256
     * - `b64` - base64
     * - `nop` - no encoding
     * For the same config name, the encoding priority `aes` > `b64` > `nop`
     * </pre>
     *
     * @see #Key$coder
     */
    private WingsCookieInterceptor.Coder coder = Aes;
    public static final String Key$coder = Key + ".coder";

    /**
     * @see #Key$noop
     */
    private Set<String> nop = Collections.emptySet();
    public static final String Key$noop = Key + ".nop";

    /**
     * @see #Key$b64
     */
    private Set<String> b64 = Collections.emptySet();
    public static final String Key$b64 = Key + ".b64";

    /**
     * @see #Key$aes
     */
    private Set<String> aes = Collections.emptySet();
    public static final String Key$aes = Key + ".aes";

    /**
     * HttpOnly, js cannot be read, do not process if not set
     *
     * @see #Key$httpOnly
     */
    private Map<Boolean, Set<String>> httpOnly = Collections.emptyMap();
    public static final String Key$httpOnly = Key + ".http-only";

    /**
     * transfer by https, do not process if not set
     *
     * @see #Key$secure
     */
    private Map<Boolean, Set<String>> secure = Collections.emptyMap();
    public static final String Key$secure = Key + ".secure";

    /**
     * bind domain to cookie,
     * eg. `wings.slardar.cookie.domain[a.com]`=`b,c`,
     * means cookie with `name` of `b` or `c`, its `domain` is `a.com`
     *
     * @see #Key$domain
     */
    private Map<String, Set<String>> domain = Collections.emptyMap();
    public static final String Key$domain = Key + ".domain";

    /**
     * bind cookie to path,
     * eg. `wings.slardar.cookie.path[/admin]`=`b,c`,
     * means cookie with `name` of `b` or `c`, its `path` is `/admin`
     *
     * @see #Key$path
     */
    private Map<String, Set<String>> path = Collections.emptyMap();
    public static final String Key$path = Key + ".path";
}
