package pro.fessional.wings.tiny.task.spring.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * wings-tinytask-exec-79.properties
 *
 * @author trydofor
 * @see #Key
 * @since 2021-02-13
 */
@Data
@ConfigurationProperties(TinyTaskExecProp.Key)
public class TinyTaskExecProp {

    public static final String Key = "wings.tiny.task.exec";

    /**
     * whether to dry run, log only without realy exec the task.
     *
     * @see #Key$dryrun
     */
    private boolean dryrun = false;
    public static final String Key$dryrun = Key + ".dryrun";

    /**
     * prefix of notice subject
     *
     * @see #Key$noticePrefix
     */
    private String noticePrefix = "tiny-task";
    public static final String Key$noticePrefix = Key + ".notice-prefix";
}
