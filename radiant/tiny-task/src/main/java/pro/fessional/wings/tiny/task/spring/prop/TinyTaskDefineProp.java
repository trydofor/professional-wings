package pro.fessional.wings.tiny.task.spring.prop;

import lombok.Getter;
import org.springframework.beans.factory.InitializingBean;
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
public class TinyTaskDefineProp extends LinkedHashMap<String, TaskerProp> implements InitializingBean {
    public static final String Key = "wings.tiny.task.define";

    /**
     * Default Config
     *
     * @see #Key$default
     */
    @Getter
    private TaskerProp Default;
    public static final String Key$default = Key + ".default";


    @Override
    public void afterPropertiesSet() {
        Default = get("default");
        if (Default == null) {
            throw new IllegalStateException("must have 'default' define");
        }
    }
}
