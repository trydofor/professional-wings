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
     * fast thread pool
     *
     * @see #Key$fast
     */
    private TaskSchedulingProperties fast;
    public static final String Key$fast = Key + ".fast";

    /**
     * executor prefix
     *
     * @see #Key$execPrefix
     */
    private ExecPrefix execPrefix = new ExecPrefix();
    public static final String Key$execPrefix = Key + ".exec-prefix";

    @Data
    public static class ExecPrefix {
        /**
         * AsyncHelper lite Pool
         */
        private String lite = "lit-exec-";
        /**
         * Callable MVC mapping
         */
        private String application = "app-exec-";
    }
}
