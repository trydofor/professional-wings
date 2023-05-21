package pro.fessional.wings.slardar.spring.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import pro.fessional.wings.slardar.monitor.metric.JvmMetric;
import pro.fessional.wings.slardar.monitor.metric.LogMetric;
import pro.fessional.wings.slardar.monitor.viewer.LogConf;

import java.util.HashMap;
import java.util.Map;

/**
 * Setting of app builtin simple monitoring, `-1` in the threshold value means ignore.
 *
 * @author trydofor
 * @see #Key
 * @since 2021-02-14
 */
@Data
@ConfigurationProperties(SlardarMonitorProp.Key)
public class SlardarMonitorProp {

    public static final String Key = "wings.slardar.monitor";

    /**
     * Monitor its own cron, `-` means stop this cron, default 10 minutes.
     *
     * @see #Key$cron
     */
    public static final String Key$cron = Key + ".cron";
    private String cron = "0 */10 * * * ?";

    /**
     * whether to send notice for the start and stop of its own jvm hook
     *
     * @see #Key$hook
     */
    private boolean hook = true;
    public static final String Key$hook = Key + ".hook";

    /**
     * log monitor config
     *
     * @see #Key$log
     */
    public static final String Key$log = Key + ".log";
    private Map<String, LogMetric.Rule> log = new HashMap<>();

    /**
     * jvm monitor config
     *
     * @see #Key$jvm
     */
    public static final String Key$jvm = JvmMetric.Rule.Key;
    private JvmMetric.Rule jvm = new JvmMetric.Rule();

    /**
     * alert file viewer
     *
     * @see #Key$view
     */
    private LogConf view = null;
    public static final String Key$view = LogConf.Key;

    /**
     * use DingTalk bot by default with the key `monitor`.
     * See wings-dingnotice-79.properties for details
     *
     * @see #Key$dingNotice
     */
    private String dingNotice = "monitor";
    public static final String Key$dingNotice = Key + ".ding-notice";
}
