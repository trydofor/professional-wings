package pro.fessional.wings.slardar.spring.prop;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import pro.fessional.wings.slardar.webmvc.SimpleResponse;

/**
 * backend debounce.
 * wings-debounce-79.properties
 *
 * @author trydofor
 * @see #Key
 * @since 2021-02-14
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ConfigurationProperties(SlardarDebounceProp.Key)
public class SlardarDebounceProp extends SimpleResponse {

    public static final String Key = "wings.slardar.debounce";

    /**
     * waiting capacity.
     *
     * @see #Key$capacity
     */
    private int capacity = 10_000;
    public static final String Key$capacity = Key + ".capacity";

    /**
     * max waiting seconds.
     *
     * @see #Key$maxWait
     */
    private int maxWait = 300;
    public static final String Key$maxWait = Key + ".max-wait";
}
