package pro.fessional.wings.slardar.spring.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Collections;
import java.util.Map;

/**
 * @author trydofor
 * @see #Key
 * @since 2021-02-14
 */
@Data
@ConfigurationProperties(SlardarTerminalProp.Key)
public class SlardarTerminalProp {

    public static final String Key = "wings.slardar.terminal";

    /**
     * URLs not processed by TerminalInterceptor.
     *
     * @see #Key$excludePatterns
     */
    private Map<String, String> excludePatterns = Collections.emptyMap();
    public static final String Key$excludePatterns = Key + ".exclude-patterns";

    /**
     * exclude takes precedence over include
     *
     * @see #Key$includePatterns
     */
    private Map<String, String> includePatterns = Collections.emptyMap();
    public static final String Key$includePatterns = Key + ".include-patterns";

    /**
     * whether to set locale from request first than server
     *
     * @see #Key$localeRequest
     */
    private boolean localeRequest = true;
    public static final String Key$localeRequest = Key + ".locale-request";

    /**
     * whether to set timezone from request first than server
     *
     * @see #Key$timezoneRequest
     */
    private boolean timezoneRequest = false;
    public static final String Key$timezoneRequest = Key + ".timezone-request";
}
