package pro.fessional.wings.slardar.monitor.report;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import pro.fessional.wings.slardar.context.Now;
import pro.fessional.wings.slardar.monitor.WarnMetric;
import pro.fessional.wings.slardar.monitor.WarnReport;
import pro.fessional.wings.slardar.notice.DingTalkNotice;
import pro.fessional.wings.slardar.spring.prop.SlardarMonitorProp;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * 钉钉机器人 https://developers.dingtalk.com/document/app/custom-robot-access
 *
 * @author trydofor
 * @since 2021-07-14
 */
@Slf4j
@Getter @Setter
public class DingTalkReport implements WarnReport {

    private final DingTalkNotice.Conf conf;
    private final DingTalkNotice dingTalkNotice;

    public DingTalkReport(SlardarMonitorProp.DingTalkConf conf, DingTalkNotice notice) {
        this.dingTalkNotice = notice;
        this.conf = conf;
    }

    @Override
    public Sts report(String appName, String jvmName, Map<String, List<WarnMetric.Warn>> warn) {
        final String atk = conf.getAccessToken();
        if (!StringUtils.hasText(atk)) {
            log.info("accessToken is empty, skip");
            return Sts.Skip;
        }

        if (atk.contains("${")) {
            log.info("accessToken has placeholder, skip");
            return Sts.Skip;
        }

        if (warn.isEmpty()) {
            log.info("warning is empty, skip");
            return Sts.Skip;
        }

        String text = buildMarkdown(appName, jvmName, sb -> {
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

        final boolean rst = dingTalkNotice.send(conf, text);
        return rst ? Sts.Done : Sts.Fail;
    }

    public void post(String text) {
        dingTalkNotice.post(conf, text);
    }

    public String buildMarkdown(String app, String jvm, Consumer<StringBuilder> text) {
        StringBuilder sb = new StringBuilder();
        mkTitleH2(sb, app);
        mkItemText(sb, jvm, "jvm-name");
        mkItemText(sb, Now.zonedDateTime().toString(), "rpt-time");
        text.accept(sb);
        return dingTalkNotice.buildMarkdown(conf, app + " " + conf.getNoticeKeyword(), sb.toString());
    }


    protected void mkTitleH2(StringBuilder sb, String str) {
        sb.append("\n\n## ■ ")
          .append(str);
    }

    protected void mkItemText(StringBuilder sb, String value, String key) {
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
