package pro.fessional.wings.slardar.monitor;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author trydofor
 * @since 2021-07-14
 */
public interface WarnMetric {

    /**
     * 获得配置所在的key
     * @return key
     */
    @NotNull
    String getKey();

    @NotNull
    List<Warn> check();

    enum Type {
        Text,
        Link,
        File
    }

    @Data
    class Warn {
        private Type type;
        private String key;
        private String rule;
        private String warn;
    }
}
