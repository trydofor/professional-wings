package pro.fessional.wings.slardar.monitor.report;

import lombok.Setter;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pro.fessional.wings.slardar.monitor.WarnMetric;
import pro.fessional.wings.slardar.notice.DingTalkNotice;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author trydofor
 * @since 2021-07-15
 */
@SpringBootTest(properties = "wings.slardar.ding-notice.default.access-token=${DING_TALK_TOKEN:}")
@Disabled("钉钉通知，避免频繁调用")
class DingTalkReportTest {

    @Setter(onMethod_ = {@Autowired})
    private DingTalkReport dingTalkReport;

    @Setter(onMethod_ = {@Autowired})
    private DingTalkNotice dingTalkNotice;

    @Test
    void postReport() {
        Map<String, List<WarnMetric.Warn>> warns = new TreeMap<>();
        List<WarnMetric.Warn> list = new ArrayList<>();
        final WarnMetric.Warn w = new WarnMetric.Warn();
        w.setKey("testKey");
        w.setRule("hardcode");
        w.setWarn("wwwwwww");
        w.setType(WarnMetric.Type.Text);
        list.add(w);
        warns.put("test", list);
        dingTalkReport.report("test", "jvm1", warns);
    }

    @Test
    void postNotice() {
        final DingTalkNotice.Conf conf = dingTalkNotice.provideConfig("monitor", true);
        conf.setNoticeMobiles(Set.of("155XXXX1991"));
        dingTalkNotice.post(conf, "测试标题", "##测试正文\n\n- **列表** 正常");
    }
}
