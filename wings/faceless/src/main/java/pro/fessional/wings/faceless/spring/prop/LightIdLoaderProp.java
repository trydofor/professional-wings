package pro.fessional.wings.faceless.spring.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author trydofor
 * @see #Key
 * @since 2019-05-30
 */
@Data
@ConfigurationProperties(LightIdLoaderProp.Key)
public class LightIdLoaderProp {

    public static final String Key = "wings.faceless.lightid.loader";

    /**
     * 超时毫秒数
     *
     * @see #Key$timeout
     */
    private long timeout = 1000;
    public static final String Key$timeout = Key + ".timeout";

    /**
     * 错误时最大尝试次数
     *
     * @see #Key$maxError
     */
    private int maxError = 5;
    public static final String Key$maxError = Key + ".max-error";

    /**
     * 加载最大数量
     *
     * @see #Key$maxCount
     */
    private int maxCount = 10000;
    public static final String Key$maxCount = Key + ".max-count";

    /**
     * 错误存在毫秒数
     *
     * @see #Key$errAlive
     */
    private long errAlive = 120000;
    public static final String Key$errAlive = Key + ".err-alive";
}
