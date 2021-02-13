package pro.fessional.wings.faceless.spring.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author trydofor
 * @since 2019-05-30
 */
@Data
@ConfigurationProperties("wings.faceless.lightid.loader")
public class LightIdLoaderProp {

    /**
     * 超时毫秒数
     */
    private long timeout = 1000;
    /**
     * 错误时最大尝试次数
     */
    private int maxError = 5;
    /**
     * 加载最大数量
     */
    private int maxCount = 10000;
    /**
     * 错误存在毫秒数
     */
    private long errAlive = 120000;
}
