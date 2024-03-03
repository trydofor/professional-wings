package pro.fessional.wings.tiny.task.spring.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import pro.fessional.wings.silencer.spring.boot.WingsEnabledContext;

/**
 * wings-enabled-79.properties
 *
 * @author trydofor
 * @see #Key
 * @since 2021-02-13
 */
@Data
@ConfigurationProperties(TinyTaskEnabledProp.Key)
public class TinyTaskEnabledProp {

    public static final String Key = WingsEnabledContext.PrefixEnabled + ".tiny.task";

    /**
     * whether to auto register TinyTask.Auto.
     *
     * @see #Key$autorun
     */
    private boolean autorun = true;
    public static final String Key$autorun = Key + ".autorun";

    /**
     * whether to enable TaskConfController.
     *
     * @see #Key$mvcConf
     */
    private boolean mvcConf = true;
    public static final String Key$mvcConf = Key + ".mvc-conf";

    /**
     * whether to enable TaskExecController.
     *
     * @see #Key$mvcExec
     */
    private boolean mvcExec = true;
    public static final String Key$mvcExec = Key + ".mvc-exec";

    /**
     * whether to enable TaskListController.
     *
     * @see #Key$mvcList
     */
    private boolean mvcList = true;
    public static final String Key$mvcList = Key + ".mvc-list";

}
