package pro.fessional.wings.silencer.spring.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Collections;
import java.util.Map;

/**
 * WingsEnabledCondition properties wings-conditional-79.properties
 *
 * @author trydofor
 * @see #Key
 * @since 2023-12-04
 */
@Data
@ConfigurationProperties(SilencerConditionalProp.Key)
public class SilencerConditionalProp {

    public static final String Key = "wings.silencer.conditional";

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
