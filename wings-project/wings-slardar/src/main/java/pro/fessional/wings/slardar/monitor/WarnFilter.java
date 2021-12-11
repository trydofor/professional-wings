package pro.fessional.wings.slardar.monitor;

import java.util.List;
import java.util.Map;

/**
 * @author trydofor
 * @since 2021-07-20
 */
public interface WarnFilter {

    /**
     * 修改warns
     *
     * @param warns 内容
     */
    void filter(Map<String, List<WarnMetric.Warn>> warns);
}
