package pro.fessional.wings.slardar.spring.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Host Extend and URL Override.
 * wings-domain-extend-79.properties
 *
 * @author trydofor
 * @see #Key
 * @since 2021-02-14
 */
@Data
@ConfigurationProperties(DomainExtendProp.Key)
public class DomainExtendProp {

    public static final String Key = "wings.slardar.domain-extend";

    /**
     * cache size of matched and unmatched url, caution when RESTful
     *
     * @see #Key$cacheSize
     */
    private int cacheSize = 4096;
    public static final String Key$cacheSize = Key + ".cache-size";

    /**
     * the uniform domain prefix of  the mapping and resource Url.
     *
     * @see #Key$prefix
     */
    private String prefix;
    public static final String Key$prefix = Key + ".prefix";

    /**
     * host mapping, FilenameUtils.wildcardMatch, eg. `trydofor`=`*.trydofor.com, trydofor.com`
     *
     * @see #Key$host
     */
    private Map<String, Set<String>> host = Collections.emptyMap();
    public static final String Key$host = Key + ".host";

    /**
     * specified domain url that is not automatically detected.
     * ant match style, eg. `other-url`=`/trydofor/b/c.html`
     *
     * @see #Key$otherUrl
     */
    private List<String> otherUrl = Collections.emptyList();
    public static final String Key$otherUrl = Key + ".other-url";
}
