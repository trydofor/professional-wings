package pro.fessional.wings.tiny.task.spring.prop;

import org.springframework.boot.context.properties.ConfigurationProperties;
import pro.fessional.wings.tiny.task.schedule.conf.TaskerProp;

import java.util.LinkedHashMap;

/**
 * wings-tiny-task-79.properties
 *
 * @author trydofor
 * @since 2022-12-09
 */
@ConfigurationProperties(TinyTaskDefineProp.Key)
public class TinyTaskDefineProp extends LinkedHashMap<String, TaskerProp> {
    public static final String Key = "wings.tiny.task.define";

    /**
     * 默认属性
     *
     * @see #Key$default
     */
    private TaskerProp Default;
    public static final String Key$default = Key + ".default";

    public TaskerProp getDefault() {
        if (Default == null) {
            Default = get("default");
        }
        return Default;
    }

    public void setDefault(TaskerProp defaults) {
        this.Default = defaults;
    }
}
