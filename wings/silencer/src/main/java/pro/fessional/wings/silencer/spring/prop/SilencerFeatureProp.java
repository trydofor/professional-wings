package pro.fessional.wings.silencer.spring.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import pro.fessional.wings.silencer.spring.boot.WingsEnabledContext;

import java.util.Collections;
import java.util.Map;

/**
 * WingsEnabledCondition properties wings-feature-79.properties
 *
 * @author trydofor
 * @see #Key
 * @since 2023-12-04
 */
@Data
@ConfigurationProperties(SilencerFeatureProp.Key)
public class SilencerFeatureProp {

    public static final String Key = WingsEnabledContext.PrefixFeature;

    /**
     * the mapping of qualified-key (ant-matcher) and its error handling
     * `true`: match; `false`: not match; otherwise: throw exception
     *
     * @see #Key$error
     */
    private Map<String, Boolean> error = Collections.emptyMap();
    public static final String Key$error = Key + ".error";

    /**
     * the mapping of qualified-key (ant-matcher) and its prefix
     *
     * @see #Key$prefix
     */
    private Map<String, String> prefix = Collections.emptyMap();
    public static final String Key$prefix = Key + ".prefix";


    /**
     * the mapping of qualified-key (ant-matcher) and its enable
     *
     * @see #Key$enable
     */
    private Map<String, Boolean> enable = Collections.emptyMap();
    public static final String Key$enable = Key + ".enable";
}
