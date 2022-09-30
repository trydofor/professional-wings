package pro.fessional.wings.slardar.spring.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import pro.fessional.wings.slardar.monitor.metric.JvmMetric;
import pro.fessional.wings.slardar.monitor.metric.LogMetric;
import pro.fessional.wings.slardar.monitor.viewer.LogConf;
import pro.fessional.wings.slardar.notice.DingTalkNotice;

import java.util.HashMap;
import java.util.Map;

/**
 * 可定制Number的精度和格式
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
     * monitor 自身设置
     *
     * @see #Key$conf
     */

    public static final String Key$conf = Key + ".conf";
    private Map<String, String> conf = new HashMap<>();

    /**
     * monitor自身的cron，默认每10分钟启动一次
     *
     * @see #Key$cron
     */
    public static final String Key$cron = Key + ".cron";
    private String cron = "0 */10 * * * ?";

    /**
     * 是否对jvm的启动和停止增加hook通知
     *
     * @see #Key$hook
     */
    private boolean hook = true;
    public static final String Key$hook = Key + ".hook";

    /**
     * 日志监控配置
     *
     * @see #Key$log
     */
    public static final String Key$log = Key + ".log";
    private Map<String, LogMetric.Rule> log = new HashMap<>();

    /**
     * 进程监控配置
     *
     * @see #Key$jvm
     */
    public static final String Key$jvm = JvmMetric.Rule.Key;
    private JvmMetric.Rule jvm = new JvmMetric.Rule();

    /**
     * 钉钉通知设置
     *
     * @see #Key$dingTalk
     */
    public static final String Key$dingTalk = DingTalkConf.Key;
    private DingTalkConf dingTalk = new DingTalkConf();


    /**
     * @see #Key$view
     */
    private LogConf view = null;
    public static final String Key$view = LogConf.Key;


    public static class DingTalkConf extends DingTalkNotice.Conf {
        public static final String Key = SlardarMonitorProp.Key + ".ding-talk";
        public static final String Key$webhookUrl = Key + ".webhook-url";
        public static final String Key$accessToken = Key + ".access-token";
        public static final String Key$digestSecret = Key + ".digest-secret";
        public static final String Key$noticeKeyword = Key + ".notice-keyword";
        public static final String Key$noticeMobiles = Key + ".notice-mobiles";
    }
}
