package pro.fessional.wings.slardar.monitor.report;

import lombok.Setter;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pro.fessional.wings.slardar.notice.DingTalkNotice;
import pro.fessional.wings.slardar.spring.prop.SlardarMonitorProp;

import java.util.Set;

/**
 * @author trydofor
 * @since 2021-07-15
 */
@SpringBootTest(properties = "wings.slardar.monitor.ding-talk.access-token=${DING_TALK_TOKEN:}")
@Disabled
class DingTalkReportTest {

    @Setter(onMethod_ = {@Autowired})
    private SlardarMonitorProp slardarMonitorProp;

    @Setter(onMethod_ = {@Autowired})
    private DingTalkReport dingTalkReport;

    @Setter(onMethod_ = {@Autowired})
    private DingTalkNotice dingTalkNotice;

    @Test
    void postReport() {
        String text = dingTalkReport.buildMarkdown("测试", "jvm",
                sb -> sb.append("## 标题\n- **列表** 正常"));
        dingTalkReport.post(text);
    }

    @Test
    void postNotice() {
        final SlardarMonitorProp.DingTalkConf conf = slardarMonitorProp.getDingTalk();
        conf.setNoticeMobiles(Set.of("155XXXX1991"));
        String text = dingTalkNotice.buildMarkdown(conf, "测试标题", "##测试正文\n\n- **列表** 正常");
        dingTalkReport.post(text);
    }
}
