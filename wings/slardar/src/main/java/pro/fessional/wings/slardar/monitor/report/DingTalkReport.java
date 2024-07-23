package pro.fessional.wings.slardar.monitor.report;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import pro.fessional.wings.slardar.context.Now;
import pro.fessional.wings.slardar.monitor.WarnMetric;
import pro.fessional.wings.slardar.monitor.WarnReport;
import pro.fessional.wings.slardar.notice.DingTalkConf;
import pro.fessional.wings.slardar.notice.DingTalkNotice;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

import static pro.fessional.wings.silencer.datetime.DateTimePattern.FMT_FULL_19Z;

/**
 * <a href="https://developers.dingtalk.com/document/app/custom-robot-access">Dingtalk robot</a>
 *
 * @author trydofor
 * @since 2021-07-14
 */
@Slf4j
@Getter @Setter
public class DingTalkReport implements WarnReport {

    private final String dingConfig;
    private final DingTalkNotice dingTalkNotice;
    private final AtomicLong counter = new AtomicLong(1);

    protected String gitInfo = null;

    public DingTalkReport(DingTalkNotice notice, String config) {
        this.dingTalkNotice = notice;
        this.dingConfig = config;
    }

    /**
     * report warn to dingtalk robot
     *
     * @param appName app name as subject
     * @param jvmName jvm name to track
     * @param warn    warn details
     */
    @Override
    public Sts report(String appName, String jvmName, Map<String, List<WarnMetric.Warn>> warn) {
        final DingTalkConf conf = dingTalkNotice.provideConfig(dingConfig, true);
        final String host = conf.getValidWebhook();
        if (host == null) {
            log.debug("bad webhook, skip");
            return Sts.Skip;
        }

        if (host.contains("${")) {
            log.debug("accessToken has placeholder, skip");
            return Sts.Skip;
        }

        if (warn.isEmpty()) {
            log.debug("warning is empty, skip");
            return Sts.Skip;
        }

        String text = buildMkContent(appName, jvmName, sb -> {
            for (Map.Entry<String, List<WarnMetric.Warn>> entry : warn.entrySet()) {
                mkTitleH2(sb, entry.getKey());
                for (WarnMetric.Warn w : entry.getValue()) {
                    if (w.getType() == WarnMetric.Type.Link) {
                        mkItemLink(sb, w);
                    }
                    else {
                        mkItemText(sb, w);
                    }
                }
            }
        });

        String subject = appName;
        if (StringUtils.isNotEmpty(conf.getNoticeKeyword())) {
            subject = subject + " " + conf.getNoticeKeyword();
        }

        final boolean rst = dingTalkNotice.send(conf, subject + " #" + counter.getAndIncrement(), text);
        return rst ? Sts.Done : Sts.Fail;
    }

    public String buildMkContent(String app, String jvm, Consumer<StringBuilder> builder) {
        StringBuilder sb = new StringBuilder();
        mkTitleH2(sb, app);
        mkItemText(sb, jvm, "jvm-name");
        mkItemText(sb, gitInfo, "git-info");
        mkItemText(sb, FMT_FULL_19Z.format(Now.zonedDateTime()), "rpt-time");
        builder.accept(sb);
        return sb.toString();
    }

    protected void mkTitleH2(StringBuilder sb, String str) {
        if (str == null) return;
        sb.append("\n\n## ■ ")
          .append(str);
    }

    protected void mkItemText(StringBuilder sb, String value, String key) {
        if (value == null) return;
        sb.append("\n- ")
          .append(value)
          .append(" | ")
          .append(key);
    }

    protected void mkItemText(StringBuilder sb, WarnMetric.Warn warn) {
        sb.append("\n- ")
          .append("**").append(warn.getWarn()).append("** | ")
          .append(warn.getRule())
          .append(" | ")
          .append(warn.getKey());
    }

    protected void mkItemLink(StringBuilder sb, WarnMetric.Warn warn) {
        sb.append("\n- [")
          .append(warn.getRule())
          .append("](")
          .append(warn.getWarn())
          .append(") | ")
          .append(warn.getKey());
    }
}
