package pro.fessional.wings.tiny.task.spring.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author trydofor
 * @since 2022-12-26
 */
@Data
@ConfigurationProperties(TinyTaskUrlmapProp.Key)
public class TinyTaskUrlmapProp {
    public static final String Key = "wings.tiny.task.urlmap";

    /**
     * list of running tasks.
     *
     * @see #Key$taskRunning
     */
    private String taskRunning = "";
    public static final String Key$taskRunning = Key + ".task-running";

    /**
     * list of defined tasks.
     *
     * @see #Key$taskDefined
     */
    private String taskDefined = "";
    public static final String Key$taskDefined = Key + ".task-defined";

    /**
     * list of task results.
     *
     * @see #Key$taskResult
     */
    private String taskResult = "";
    public static final String Key$taskResult = Key + ".task-result";

    /**
     * cancel a task.
     *
     * @see #Key$taskCancel
     */
    private String taskCancel = "";
    public static final String Key$taskCancel = Key + ".task-cancel";

    /**
     * start a task.
     *
     * @see #Key$taskLaunch
     */
    private String taskLaunch = "";
    public static final String Key$taskLaunch = Key + ".task-launch";

    /**
     * force to start a task.
     *
     * @see #Key$taskForce
     */
    private String taskForce = "";
    public static final String Key$taskForce = Key + ".task-force";

    /**
     * enable or disable a task.
     *
     * @see #Key$taskEnable
     */
    private String taskEnable = "";
    public static final String Key$taskEnable = Key + ".task-enable";

    /**
     * update the task config.
     *
     * @see #Key$taskPropSave
     */
    private String taskPropSave = "";
    public static final String Key$taskPropSave = Key + ".task-prop-save";

    /**
     * load the task config.
     *
     * @see #Key$taskPropLoad
     */
    private String taskPropLoad = "";
    public static final String Key$taskPropLoad = Key + ".task-prop-load";

    /**
     * show the prop of task conf.
     *
     * @see #Key$taskPropConf
     */
    private String taskPropConf = "";
    public static final String Key$taskPropConf = Key + ".task-prop-conf";

    /**
     * show the diff of task conf.
     *
     * @see #Key$taskPropDiff
     */
    private String taskPropDiff = "";
    public static final String Key$taskPropDiff = Key + ".task-prop-diff";

}
