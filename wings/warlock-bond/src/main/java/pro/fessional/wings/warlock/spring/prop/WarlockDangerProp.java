package pro.fessional.wings.warlock.spring.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * wings-warlock-ticket-77.properties
 *
 * @author trydofor
 * @see #Key
 * @since 2021-02-17
 */
@Data
@ConfigurationProperties(WarlockDangerProp.Key)
public class WarlockDangerProp {

    public static final String Key = "wings.warlock.danger";

    /**
     * Whether to switch the account status to danger when the maximum failure is reached.
     *
     * @see #Key$maxFailure
     */
    private boolean maxFailure = true;
    public static final String Key$maxFailure = Key + ".max-failure";

    /**
     * Retry interval when bad badCredentials.
     *
     * @see #Key$retryStep
     */
    private Duration retryStep = Duration.ofSeconds(5);
    public static final String Key$retryStep = Key + ".retry-step";

    /**
     * cache size for danger
     *
     * @see #Key$cacheSize
     */
    private int cacheSize = 10_000;
    public static final String Key$cacheSize = Key + ".cache-size";

    /**
     * cache ttl for danger
     *
     * @see #Key$cacheTtl
     */
    private Duration cacheTtl = Duration.ofSeconds(300);
    public static final String Key$cacheTtl = Key + ".cache-ttl";

}
