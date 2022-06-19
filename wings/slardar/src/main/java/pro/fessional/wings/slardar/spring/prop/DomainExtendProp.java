package pro.fessional.wings.slardar.spring.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 构造 prefix+host+url的新路径
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
     * 匹配和未匹配的url缓存size，restfull 慎用
     *
     * @see #Key$cacheSize
     */
    private int cacheSize = 4096;
    public static final String Key$cacheSize = Key + ".cache-size";

    /**
     * mapping和resource的URL统一的domain的前缀
     *
     * @see #Key$prefix
     */
    private String prefix;
    public static final String Key$prefix = Key + ".prefix";

    /**
     * host映射关系，FilenameUtils.wildcardMatch
     *
     * @see #Key$host
     */
    private Map<String, Set<String>> host = Collections.emptyMap();
    public static final String Key$host = Key + ".host";

    /**
     * 指定domain url 不自动探测，支持ant风格。
     *
     * @see #Key$otherUrl
     */
    private List<String> otherUrl = Collections.emptyList();
    public static final String Key$otherUrl = Key + ".other-url";
}
