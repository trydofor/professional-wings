/*
 * This file is generated by jOOQ.
 */
package pro.fessional.wings.tiny.task.database.autogen.tables.interfaces;


import jakarta.annotation.Generated;

import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * The table <code>wings_radiant.win_task_result</code>.
 */
@Generated(
    value = {
        "https://www.jooq.org",
        "jOOQ version:3.14.16",
        "schema version:2020102601"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
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
     * Setter for <code>win_task_result.task_msg</code>.
     */
    public void setTaskMsg(String value);

    /**
     * Getter for <code>win_task_result.task_msg</code>.
     */
    public String getTaskMsg();

    /**
     * Setter for <code>win_task_result.time_exec</code>.
     */
    public void setTimeExec(LocalDateTime value);

    /**
     * Getter for <code>win_task_result.time_exec</code>.
     */
    public LocalDateTime getTimeExec();

    /**
     * Setter for <code>win_task_result.time_fail</code>.
     */
    public void setTimeFail(LocalDateTime value);

    /**
     * Getter for <code>win_task_result.time_fail</code>.
     */
    public LocalDateTime getTimeFail();

    /**
     * Setter for <code>win_task_result.time_done</code>.
     */
    public void setTimeDone(LocalDateTime value);

    /**
     * Getter for <code>win_task_result.time_done</code>.
     */
    public LocalDateTime getTimeDone();

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
     * Load data from another generated Record/POJO implementing the common interface IWinTaskResult
     */
    public void from(IWinTaskResult from);

    /**
     * Copy data into another generated Record/POJO implementing the common interface IWinTaskResult
     */
    public <E extends IWinTaskResult> E into(E into);
}
