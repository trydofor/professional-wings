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
     * timeout millis of loading.
     *
     * @see #Key$timeout
     */
    private long timeout = 1000;
    public static final String Key$timeout = Key + ".timeout";

    /**
     * max error count of loading.
     *
     * @see #Key$maxError
     */
    private int maxError = 5;
    public static final String Key$maxError = Key + ".max-error";

    /**
     * max id count of per loading.
     *
     * @see #Key$maxCount
     */
    private int maxCount = 10000;
    public static final String Key$maxCount = Key + ".max-count";

    /**
     * no attempt in number of millis if error exists.
     *
     * @see #Key$errAlive
     */
    private long errAlive = 120000;
    public static final String Key$errAlive = Key + ".err-alive";
}
