package pro.fessional.wings.slardar.monitor.report;

import io.qameta.allure.TmsLink;
import lombok.Setter;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pro.fessional.wings.slardar.monitor.WarnMetric;
import pro.fessional.wings.slardar.notice.DingTalkConf;
import pro.fessional.wings.slardar.notice.DingTalkNotice;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author trydofor
 * @since 2021-07-15
 */
@SpringBootTest(properties = {
        "wings.slardar.ding-notice.default.access-token=${DING_TALK_TOKEN:}",
        "wings.slardar.ding-notice.default.notice-mobiles.god9=155XXXX1991",
})
@Disabled("Avoid frequent calls")
class DingTalkReportTest {

    @Setter(onMethod_ = {@Autowired})
    private DingTalkReport dingTalkReport;

    @Setter(onMethod_ = {@Autowired})
    private DingTalkNotice dingTalkNotice;

    @Test
    @TmsLink("C13078")
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
    @TmsLink("C13079")
    void postNotice() {
        final DingTalkConf conf = dingTalkNotice.provideConfig("monitor", true);
        conf.setNoticeMobiles(Map.of("a9", "155XXXX1992"));
        dingTalkNotice.post(conf, "MARKDOWN Title", "## Test context\n\n- **List** Text");
        conf.setMsgType(DingTalkConf.MsgText);
        dingTalkNotice.post(conf, "Text Title", "## Test context\n\n- **List** Text");
    }
}
