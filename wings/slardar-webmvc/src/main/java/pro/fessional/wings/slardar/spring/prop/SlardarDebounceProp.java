package pro.fessional.wings.slardar.spring.prop;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import pro.fessional.wings.slardar.webmvc.SimpleResponse;

/**
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
     * 等待区大小
     *
     * @see #Key$capacity
     */
    private long capacity = 10_000;
    public static final String Key$capacity = Key + ".capacity";

    /**
     * 最大等待秒
     *
     * @see #Key$maxWait
     */
    private int maxWait = 300;
    public static final String Key$maxWait = Key + ".max-wait";
}
