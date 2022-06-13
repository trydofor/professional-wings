package pro.fessional.wings.slardar.spring.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import pro.fessional.wings.slardar.servlet.cookie.WingsCookieInterceptor;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import static pro.fessional.wings.slardar.servlet.cookie.WingsCookieInterceptor.Coder.Aes;

/**
 * @author trydofor
 * @see #Key
 * @since 2021-02-14
 */
@Data
@ConfigurationProperties(SlardarCookieProp.Key)
public class SlardarCookieProp {

    public static final String Key = "wings.slardar.cookie";

    /**
     * cookie前缀，默认空
     *
     * @see #Key$prefix
     */
    private String prefix = "";
    public static final String Key$prefix = Key + ".prefix";

    /**
     * cookie别名，受前缀影响
     *
     * @see #Key$alias
     */
    private Map<String, String> alias = Collections.emptyMap();
    public static final String Key$alias = Key + ".alias";

    /**
     * cookie编码，默认b64(base64)，可选aes(aes128)和noop(不加密)
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
     * @see #Key$aesKey
     */
    private String aesKey = "";
    public static final String Key$aesKey = Key + ".aes-key";

    /**
     * http only，js不可读取
     *
     * @see #Key$httpOnly
     */
    private Map<Boolean, Set<String>> httpOnly = Collections.emptyMap();
    public static final String Key$httpOnly = Key + ".http-only";

    /**
     * https 下发送
     *
     * @see #Key$secure
     */
    private Map<Boolean, Set<String>> secure = Collections.emptyMap();
    public static final String Key$secure = Key + ".secure";

    /**
     * @see #Key$domain
     */
    private Map<String, Set<String>> domain = Collections.emptyMap();
    public static final String Key$domain = Key + ".domain";

    /**
     * @see #Key$path
     */
    private Map<String, Set<String>> path = Collections.emptyMap();
    public static final String Key$path = Key + ".path";
}
