package pro.fessional.wings.slardar.spring.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

import static java.util.Collections.emptyList;

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
    private List<String> localeParam = emptyList();
    public static final String Key$localeParam = Key + ".locale-param";

    /**
     * 获得locale,language参数的cookie name
     * @see #Key$localeCookie
     */
    private List<String> localeCookie = emptyList();
    public static final String Key$localeCookie = Key + ".locale-cookie";

    /**
     * 获得locale,language参数的header name
     * @see #Key$localeHeader
     */
    private List<String> localeHeader = emptyList();
    public static final String Key$localeHeader = Key + ".locale-header";

    /**
     * 获得ltimezone, zoneid，获得param key
     * @see #Key$zoneidParam
     */
    private List<String> zoneidParam = emptyList();
    public static final String Key$zoneidParam = Key + ".zoneid-param";

    /**
     * 获得ltimezone, zoneid的cookie name
     * @see #Key$zoneidCookie
     */
    private List<String> zoneidCookie = emptyList();
    public static final String Key$zoneidCookie = Key + ".zoneid-cookie";

    /**
     * 获得ltimezone, zoneid的header name
     * @see #Key$zoneidHeader
     */
    private List<String> zoneidHeader = emptyList();
    public static final String Key$zoneidHeader = Key + ".zoneid-header";

}
