package pro.fessional.wings.slardar.spring.prop;

import lombok.Data;
import org.springframework.boot.autoconfigure.task.TaskExecutionProperties;
import org.springframework.boot.autoconfigure.task.TaskSchedulingProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author trydofor
 * @since 2022-12-05
 */
@Data
@ConfigurationProperties(SlardarAsyncProp.Key)
public class SlardarAsyncProp {

    public static final String Key = "wings.slardar.async";

    /**
     * event thread pool
     *
     * @see #Key$event
     */
    private TaskExecutionProperties event;
    public static final String Key$event = Key + ".event";


    /**
     * heavy thread pool
     *
     * @see #Key$heavy
     */
    private TaskSchedulingProperties heavy;
    public static final String Key$heavy = Key + ".heavy";
}
