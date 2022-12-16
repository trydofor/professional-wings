package pro.fessional.wings.tiny.task.spring.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * spring-wings-enabled-79.properties
 *
 * @author trydofor
 * @see #Key
 * @since 2021-02-13
 */
@Data
@ConfigurationProperties(TinyTaskEnabledProp.Key)
public class TinyTaskEnabledProp {

    public static final String Key = "wings.tiny.task.enabled";

    /**
     * 是否允许自动注册TinyTask.Auto
     *
     * @see #Key$autoreg
     */
    private boolean autoreg = true;
    public static final String Key$autoreg = Key + ".autoreg";
}
