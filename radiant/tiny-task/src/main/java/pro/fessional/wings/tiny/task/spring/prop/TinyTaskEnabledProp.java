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
     * whether to enable auto config.
     *
     * @see #Key$autoconf
     */
    private boolean autoconf = true;
    public static final String Key$autoconf = Key + ".autoconf";


    /**
     * whether to auto register TinyTask.Auto.
     *
     * @see #Key$autorun
     */
    private boolean autorun = true;
    public static final String Key$autorun = Key + ".autorun";

    /**
     * whether to dry run, log only without realy exec the task.
     *
     * @see #Key$dryrun
     */
    private boolean dryrun = false;
    public static final String Key$dryrun = Key + ".dryrun";

    /**
     * whether to enable TaskConfController.
     *
     * @see #Key$controllerConf
     */
    private boolean controllerConf = true;
    public static final String Key$controllerConf = Key + ".controller-conf";

    /**
     * whether to enable TaskExecController.
     *
     * @see #Key$controllerExec
     */
    private boolean controllerExec = true;
    public static final String Key$controllerExec = Key + ".controller-exec";

    /**
     * whether to enable TaskListController.
     *
     * @see #Key$controllerList
     */
    private boolean controllerList = true;
    public static final String Key$controllerList = Key + ".controller-list";

}
