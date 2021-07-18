package pro.fessional.wings.slardar.monitor;

import java.util.List;
import java.util.Map;

/**
 * @author trydofor
 * @since 2021-07-14
 */
public interface WarnReport {

    enum Sts {
        Skip,
        Fail,
        Done
    }

    /**
     * 发送报告
     *
     * @param title 当前jvm标识
     * @param warn    警告内容
     * @return 报告结果
     */
    Sts report(String title, Map<String, List<WarnMetric.Warn>> warn);
}
