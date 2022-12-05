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
     * 事件配置
     *
     * @see #Key$event
     */
    private TaskExecutionProperties event;
    public static final String Key$event = Key + ".event";


    /**
     * 重任务线程池
     *
     * @see #Key$heavy
     */
    private TaskSchedulingProperties heavy;
    public static final String Key$heavy = Key + ".heavy";
}
