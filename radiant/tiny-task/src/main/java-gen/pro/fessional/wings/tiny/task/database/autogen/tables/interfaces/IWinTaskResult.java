/*
 * This file is generated by jOOQ.
 */
package pro.fessional.wings.tiny.task.database.autogen.tables.interfaces;


import javax.annotation.processing.Generated;
import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * The table <code>wings.win_task_result</code>.
 */
@Generated(
    value = {
        "https://www.jooq.org",
        "jOOQ version:3.18.9",
        "schema version:2020102701"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public interface IWinTaskResult extends Serializable {

    /**
     * Setter for <code>win_task_result.id</code>.
     */
    public void setId(Long value);

    /**
     * Getter for <code>win_task_result.id</code>.
     */
    public Long getId();

    /**
     * Setter for <code>win_task_result.task_id</code>.
     */
    public void setTaskId(Long value);

    /**
     * Getter for <code>win_task_result.task_id</code>.
     */
    public Long getTaskId();

    /**
     * Setter for <code>win_task_result.task_key</code>.
     */
    public void setTaskKey(String value);

    /**
     * Getter for <code>win_task_result.task_key</code>.
     */
    public String getTaskKey();

    /**
     * Setter for <code>win_task_result.task_app</code>.
     */
    public void setTaskApp(String value);

    /**
     * Getter for <code>win_task_result.task_app</code>.
     */
    public String getTaskApp();

    /**
     * Setter for <code>win_task_result.task_pid</code>.
     */
    public void setTaskPid(Integer value);

    /**
     * Getter for <code>win_task_result.task_pid</code>.
     */
    public Integer getTaskPid();

    /**
     * Setter for <code>win_task_result.exit_data</code>.
     */
    public void setExitData(String value);

    /**
     * Getter for <code>win_task_result.exit_data</code>.
     */
    public String getExitData();

    /**
     * Setter for <code>win_task_result.exit_fail</code>.
     */
    public void setExitFail(Boolean value);

    /**
     * Getter for <code>win_task_result.exit_fail</code>.
     */
    public Boolean getExitFail();

    /**
     * Setter for <code>win_task_result.time_exec</code>.
     */
    public void setTimeExec(LocalDateTime value);

    /**
     * Getter for <code>win_task_result.time_exec</code>.
     */
    public LocalDateTime getTimeExec();

    /**
     * Setter for <code>win_task_result.time_exit</code>.
     */
    public void setTimeExit(LocalDateTime value);

    /**
     * Getter for <code>win_task_result.time_exit</code>.
     */
    public LocalDateTime getTimeExit();

    /**
     * Setter for <code>win_task_result.time_cost</code>.
     */
    public void setTimeCost(Integer value);

    /**
     * Getter for <code>win_task_result.time_cost</code>.
     */
    public Integer getTimeCost();

    // -------------------------------------------------------------------------
    // FROM and INTO
    // -------------------------------------------------------------------------

    /**
     * Load data from another generated Record/POJO implementing the common
     * interface IWinTaskResult
     */
    public void from(IWinTaskResult from);

    /**
     * Copy data into another generated Record/POJO implementing the common
     * interface IWinTaskResult
     */
    public <E extends IWinTaskResult> E into(E into);
}
