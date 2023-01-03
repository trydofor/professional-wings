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
     * @see #Key$autorun
     */
    private boolean autorun = true;
    public static final String Key$autorun = Key + ".autorun";

    /**
     * 是否干跑，仅记录日志不真正执行任务
     *
     * @see #Key$dryrun
     */
    private boolean dryrun = false;
    public static final String Key$dryrun = Key + ".dryrun";
}
