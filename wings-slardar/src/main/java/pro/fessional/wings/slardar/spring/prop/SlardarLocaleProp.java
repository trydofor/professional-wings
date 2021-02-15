package pro.fessional.wings.slardar.spring.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Set;

import static java.util.Collections.emptySet;

/**
 * @author trydofor
 * @see #Key
 * @since 2021-02-14
 */
@Data
@ConfigurationProperties(SlardarLocaleProp.Key)
public class SlardarLocaleProp {

    public static final String Key = "wings.slardar.locale";

    /**
     * 获得locale,language参数的param key
     * @see #Key$localeParam
     */
    private Set<String> localeParam = emptySet();
    public static final String Key$localeParam = Key + ".locale-param";

    /**
     * 获得locale,language参数的cookie name
     * @see #Key$localeCookie
     */
    private Set<String> localeCookie = emptySet();
    public static final String Key$localeCookie = Key + ".locale-cookie";

    /**
     * 获得locale,language参数的header name
     * @see #Key$localeHeader
     */
    private Set<String> localeHeader = emptySet();
    public static final String Key$localeHeader = Key + ".locale-header";

    /**
     * 获得ltimezone, zoneid，获得param key
     * @see #Key$zoneidParam
     */
    private Set<String> zoneidParam = emptySet();
    public static final String Key$zoneidParam = Key + ".zoneid-param";

    /**
     * 获得ltimezone, zoneid的cookie name
     * @see #Key$zoneidCookie
     */
    private Set<String> zoneidCookie = emptySet();
    public static final String Key$zoneidCookie = Key + ".zoneid-cookie";

    /**
     * 获得ltimezone, zoneid的header name
     * @see #Key$zoneidHeader
     */
    private Set<String> zoneidHeader = emptySet();
    public static final String Key$zoneidHeader = Key + ".zoneid-header";

}
