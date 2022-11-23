package pro.fessional.wings.warlock.spring.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * wings-warlock-watching-77.properties
 *
 * @author trydofor
 * @see #Key
 * @since 2021-10-09
 */
@Data
@ConfigurationProperties(WarlockWatchingProp.Key)
public class WarlockWatchingProp {

    public static final String Key = "wings.warlock.watching";

    /**
     * jooq执行的阈值毫秒
     *
     * @see #Key$jooqThreshold
     */
    private long jooqThreshold = -1;
    public static final String Key$jooqThreshold = Key + ".jooq-threshold";

    /**
     * Watching注解的阈值毫秒
     *
     * @see #Key$serviceThreshold
     */
    private long serviceThreshold = -1;
    public static final String Key$serviceThreshold = Key + ".service-threshold";

    /**
     * controller的阈值毫秒
     *
     * @see #Key$controllerThreshold
     */
    private long controllerThreshold = -1;
    public static final String Key$controllerThreshold = Key + ".controller-threshold";
}
