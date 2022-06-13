package pro.fessional.wings.slardar.monitor.report;

import lombok.Setter;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author trydofor
 * @since 2021-07-15
 */
@SpringBootTest
@Disabled
class DingTalkReportTest {

    @Setter(onMethod_ = {@Autowired})
    private DingTalkReport dingTalkReport;

    @Test
    void post() {
        String text = dingTalkReport.buildMarkdown("测试", "jvm",
                sb -> sb.append("## 标题\n- **列表** 正常"));
        dingTalkReport.post(text);
    }
}
