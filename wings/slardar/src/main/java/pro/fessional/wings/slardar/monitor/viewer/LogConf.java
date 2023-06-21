package pro.fessional.wings.slardar.monitor.viewer;

import lombok.Data;
import org.springframework.util.unit.DataSize;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * @author trydofor
 * @since 2022-09-01
 */
@Data
public class LogConf {
    public static final String Key = "wings.slardar.monitor.view";

    /**
     * whether to enable the alert file viewer
     *
     * @see #Key$enable
     */
    private boolean enable = true;
    public static final String Key$enable = Key + ".enable";

    /**
     * UrlMapping, GET request, one `id` parameter only.
     *
     * @see #Key$mapping
     */
    private String mapping = "";
    public static final String Key$mapping = Key + ".mapping";

    /**
     * default alive time.
     *
     * @see #Key$alive
     */
    private Duration alive = Duration.ofHours(36);
    public static final String Key$alive = Key + ".alive";

    /**
     * how many bytes before current log is output by default, do not record sensitive data in the log.
     *
     * @see #Key$length
     */
    private DataSize length = DataSize.ofMegabytes(1);
    public static final String Key$length = Key + ".length";

    /**
     * host or ip for external access.
     *
     * @see #Key$domain
     */
    private String domain = "";
    public static final String Key$domain = Key + ".domain";

    /**
     * ignored alert string in logs.
     *
     * @see #Key$ignore
     */
    private Map<String, String> ignore = new HashMap<>();
    public static final String Key$ignore = Key + ".ignore";
}
