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

    public static final String Key = "spring.wings.tiny.task.enabled";

    /**
     * 是否启动自动配置
     *
     * @see #Key$autoconf
     */
    private boolean autoconf = true;
    public static final String Key$autoconf = Key + ".autoconf";


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

    /**
     * 是否开启 TaskConfController
     *
     * @see #Key$controllerConf
     */
    private boolean controllerConf = true;
    public static final String Key$controllerConf = Key + ".controller-conf";

    /**
     * 是否开启 TaskExecController
     *
     * @see #Key$controllerExec
     */
    private boolean controllerExec = true;
    public static final String Key$controllerExec = Key + ".controller-exec";

    /**
     * 是否开启 TaskListController
     *
     * @see #Key$controllerList
     */
    private boolean controllerList = true;
    public static final String Key$controllerList = Key + ".controller-list";

}
