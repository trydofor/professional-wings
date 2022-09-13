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
     * @see #Key$enable
     */
    private boolean enable = true;
    public static final String Key$enable = Key + ".enable";

    /**
     * @see #Key$mapping
     */
    private String mapping = "";
    public static final String Key$mapping = Key + ".mapping";

    /**
     * 默认存活时间，36小时
     *
     * @see #Key$alive
     */
    private Duration alive = Duration.ofHours(36);
    public static final String Key$alive = Key + ".alive";

    /**
     * 默认输出日志前多少byte，默认1MB。主要日志中不要记录敏感信息
     *
     * @see #Key$length
     */
    private DataSize length = DataSize.ofMegabytes(1);
    public static final String Key$length = Key + ".length";

    /**
     * 外部访问的主机,ip等
     *
     * @see #Key$domain
     */
    private String domain = "";
    public static final String Key$domain = Key + ".domain";

    /**
     * 可以排除的日志中的警报，不用设置空值
     *
     * @see #Key$ignore
     */
    private Map<String, String> ignore = new HashMap<>();
    public static final String Key$ignore = Key + ".ignore";
}
