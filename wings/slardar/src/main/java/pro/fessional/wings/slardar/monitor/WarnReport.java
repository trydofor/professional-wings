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
     * Send a report
     *
     * @param appName current app name
     * @param jvmName current jvm name
     * @param warn    wain details
     * @return the result of report
     */
    Sts report(String appName, String jvmName, Map<String, List<WarnMetric.Warn>> warn);
}
