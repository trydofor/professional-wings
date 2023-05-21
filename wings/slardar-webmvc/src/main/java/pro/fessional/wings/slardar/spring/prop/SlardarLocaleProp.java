package pro.fessional.wings.slardar.spring.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

import static java.util.Collections.emptyList;

/**
 * Get the language/timezone by key from param, cookie and header respectively
 *
 * @author trydofor
 * @see #Key
 * @since 2021-02-14
 */
@Data
@ConfigurationProperties(SlardarLocaleProp.Key)
public class SlardarLocaleProp {

    public static final String Key = "wings.slardar.locale";

    /**
     * Get the language by key from param
     *
     * @see #Key$localeParam
     */
    private List<String> localeParam = emptyList();
    public static final String Key$localeParam = Key + ".locale-param";

    /**
     * Get the language by key from cookie
     *
     * @see #Key$localeCookie
     */
    private List<String> localeCookie = emptyList();
    public static final String Key$localeCookie = Key + ".locale-cookie";

    /**
     * Get the language by key from header
     *
     * @see #Key$localeHeader
     */
    private List<String> localeHeader = emptyList();
    public static final String Key$localeHeader = Key + ".locale-header";

    /**
     * Get the timezone by key from param
     *
     * @see #Key$zoneidParam
     */
    private List<String> zoneidParam = emptyList();
    public static final String Key$zoneidParam = Key + ".zoneid-param";

    /**
     * Get the timezone by key from cookie
     *
     * @see #Key$zoneidCookie
     */
    private List<String> zoneidCookie = emptyList();
    public static final String Key$zoneidCookie = Key + ".zoneid-cookie";

    /**
     * Get the timezone by key from header
     *
     * @see #Key$zoneidHeader
     */
    private List<String> zoneidHeader = emptyList();
    public static final String Key$zoneidHeader = Key + ".zoneid-header";

}
