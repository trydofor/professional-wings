/*
 * This file is generated by jOOQ.
 */
package pro.fessional.wings.tiny.task.database.autogen.tables.records;


import java.time.LocalDateTime;

import javax.annotation.processing.Generated;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record9;
import org.jooq.Row9;
import org.jooq.impl.UpdatableRecordImpl;

import pro.fessional.wings.tiny.task.database.autogen.tables.WinTaskResultTable;
import pro.fessional.wings.tiny.task.database.autogen.tables.interfaces.IWinTaskResult;
import pro.fessional.wings.tiny.task.database.autogen.tables.pojos.WinTaskResult;


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
public class WinTaskResultRecord extends UpdatableRecordImpl<WinTaskResultRecord> implements Record9<Long, Long, String, Integer, String, Boolean, LocalDateTime, LocalDateTime, Integer>, IWinTaskResult {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>win_task_result.id</code>.
     */
    @Override
    public void setId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>win_task_result.id</code>.
     */
    @Override
    public Long getId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>win_task_result.task_id</code>.
     */
    @Override
    public void setTaskId(Long value) {
        set(1, value);
    }

    /**
     * Getter for <code>win_task_result.task_id</code>.
     */
    @Override
    public Long getTaskId() {
        return (Long) get(1);
    }

    /**
     * Setter for <code>win_task_result.task_app</code>.
     */
    @Override
    public void setTaskApp(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>win_task_result.task_app</code>.
     */
    @Override
    public String getTaskApp() {
        return (String) get(2);
    }

    /**
     * Setter for <code>win_task_result.task_pid</code>.
     */
    @Override
    public void setTaskPid(Integer value) {
        set(3, value);
    }

    /**
     * Getter for <code>win_task_result.task_pid</code>.
     */
    @Override
    public Integer getTaskPid() {
        return (Integer) get(3);
    }

    /**
     * Setter for <code>win_task_result.exit_data</code>.
     */
    @Override
    public void setExitData(String value) {
        set(4, value);
    }

    /**
     * Getter for <code>win_task_result.exit_data</code>.
     */
    @Override
    public String getExitData() {
        return (String) get(4);
    }

    /**
     * Setter for <code>win_task_result.exit_fail</code>.
     */
    @Override
    public void setExitFail(Boolean value) {
        set(5, value);
    }

    /**
     * Getter for <code>win_task_result.exit_fail</code>.
     */
    @Override
    public Boolean getExitFail() {
        return (Boolean) get(5);
    }

    /**
     * Setter for <code>win_task_result.time_exec</code>.
     */
    @Override
    public void setTimeExec(LocalDateTime value) {
        set(6, value);
    }

    /**
     * Getter for <code>win_task_result.time_exec</code>.
     */
    @Override
    public LocalDateTime getTimeExec() {
        return (LocalDateTime) get(6);
    }

    /**
     * Setter for <code>win_task_result.time_exit</code>.
     */
    @Override
    public void setTimeExit(LocalDateTime value) {
        set(7, value);
    }

    /**
     * Getter for <code>win_task_result.time_exit</code>.
     */
    @Override
    public LocalDateTime getTimeExit() {
        return (LocalDateTime) get(7);
    }

    /**
     * Setter for <code>win_task_result.time_cost</code>.
     */
    @Override
    public void setTimeCost(Integer value) {
        set(8, value);
    }

    /**
     * Getter for <code>win_task_result.time_cost</code>.
     */
    @Override
    public Integer getTimeCost() {
        return (Integer) get(8);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<Long> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record9 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row9<Long, Long, String, Integer, String, Boolean, LocalDateTime, LocalDateTime, Integer> fieldsRow() {
        return (Row9) super.fieldsRow();
    }

    @Override
    public Row9<Long, Long, String, Integer, String, Boolean, LocalDateTime, LocalDateTime, Integer> valuesRow() {
        return (Row9) super.valuesRow();
    }

    @Override
    public Field<Long> field1() {
        return WinTaskResultTable.WinTaskResult.Id;
    }

    @Override
    public Field<Long> field2() {
        return WinTaskResultTable.WinTaskResult.TaskId;
    }

    @Override
    public Field<String> field3() {
        return WinTaskResultTable.WinTaskResult.TaskApp;
    }

    @Override
    public Field<Integer> field4() {
        return WinTaskResultTable.WinTaskResult.TaskPid;
    }

    @Override
    public Field<String> field5() {
        return WinTaskResultTable.WinTaskResult.ExitData;
    }

    @Override
    public Field<Boolean> field6() {
        return WinTaskResultTable.WinTaskResult.ExitFail;
    }

    @Override
    public Field<LocalDateTime> field7() {
        return WinTaskResultTable.WinTaskResult.TimeExec;
    }

    @Override
    public Field<LocalDateTime> field8() {
        return WinTaskResultTable.WinTaskResult.TimeExit;
    }

    @Override
    public Field<Integer> field9() {
        return WinTaskResultTable.WinTaskResult.TimeCost;
    }

    @Override
    public Long component1() {
        return getId();
    }

    @Override
    public Long component2() {
        return getTaskId();
    }

    @Override
    public String component3() {
        return getTaskApp();
    }

    @Override
    public Integer component4() {
        return getTaskPid();
    }

    @Override
    public String component5() {
        return getExitData();
    }

    @Override
    public Boolean component6() {
        return getExitFail();
    }

    @Override
    public LocalDateTime component7() {
        return getTimeExec();
    }

    @Override
    public LocalDateTime component8() {
        return getTimeExit();
    }

    @Override
    public Integer component9() {
        return getTimeCost();
    }

    @Override
    public Long value1() {
        return getId();
    }

    @Override
    public Long value2() {
        return getTaskId();
    }

    @Override
    public String value3() {
        return getTaskApp();
    }

    @Override
    public Integer value4() {
        return getTaskPid();
    }

    @Override
    public String value5() {
        return getExitData();
    }

    @Override
    public Boolean value6() {
        return getExitFail();
    }

    @Override
    public LocalDateTime value7() {
        return getTimeExec();
    }

    @Override
    public LocalDateTime value8() {
        return getTimeExit();
    }

    @Override
    public Integer value9() {
        return getTimeCost();
    }

    @Override
    public WinTaskResultRecord value1(Long value) {
        setId(value);
        return this;
    }

    @Override
    public WinTaskResultRecord value2(Long value) {
        setTaskId(value);
        return this;
    }

    @Override
    public WinTaskResultRecord value3(String value) {
        setTaskApp(value);
        return this;
    }

    @Override
    public WinTaskResultRecord value4(Integer value) {
        setTaskPid(value);
        return this;
    }

    @Override
    public WinTaskResultRecord value5(String value) {
        setExitData(value);
        return this;
    }

    @Override
    public WinTaskResultRecord value6(Boolean value) {
        setExitFail(value);
        return this;
    }

    @Override
    public WinTaskResultRecord value7(LocalDateTime value) {
        setTimeExec(value);
        return this;
    }

    @Override
    public WinTaskResultRecord value8(LocalDateTime value) {
        setTimeExit(value);
        return this;
    }

    @Override
    public WinTaskResultRecord value9(Integer value) {
        setTimeCost(value);
        return this;
    }

    @Override
    public WinTaskResultRecord values(Long value1, Long value2, String value3, Integer value4, String value5, Boolean value6, LocalDateTime value7, LocalDateTime value8, Integer value9) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        value8(value8);
        value9(value9);
        return this;
    }

    // -------------------------------------------------------------------------
    // FROM and INTO
    // -------------------------------------------------------------------------

    @Override
    public void from(IWinTaskResult from) {
        setId(from.getId());
        setTaskId(from.getTaskId());
        setTaskApp(from.getTaskApp());
        setTaskPid(from.getTaskPid());
        setExitData(from.getExitData());
        setExitFail(from.getExitFail());
        setTimeExec(from.getTimeExec());
        setTimeExit(from.getTimeExit());
        setTimeCost(from.getTimeCost());
        resetChangedOnNotNull();
    }

    @Override
    public <E extends IWinTaskResult> E into(E into) {
        into.from(this);
        return into;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached WinTaskResultRecord
     */
    public WinTaskResultRecord() {
        super(WinTaskResultTable.WinTaskResult);
    }

    /**
     * Create a detached, initialised WinTaskResultRecord
     */
    public WinTaskResultRecord(Long id, Long taskId, String taskApp, Integer taskPid, String exitData, Boolean exitFail, LocalDateTime timeExec, LocalDateTime timeExit, Integer timeCost) {
        super(WinTaskResultTable.WinTaskResult);

        setId(id);
        setTaskId(taskId);
        setTaskApp(taskApp);
        setTaskPid(taskPid);
        setExitData(exitData);
        setExitFail(exitFail);
        setTimeExec(timeExec);
        setTimeExit(timeExit);
        setTimeCost(timeCost);
        resetChangedOnNotNull();
    }

    /**
     * Create a detached, initialised WinTaskResultRecord
     */
    public WinTaskResultRecord(WinTaskResult value) {
        super(WinTaskResultTable.WinTaskResult);

        if (value != null) {
            setId(value.getId());
            setTaskId(value.getTaskId());
            setTaskApp(value.getTaskApp());
            setTaskPid(value.getTaskPid());
            setExitData(value.getExitData());
            setExitFail(value.getExitFail());
            setTimeExec(value.getTimeExec());
            setTimeExit(value.getTimeExit());
            setTimeCost(value.getTimeCost());
            resetChangedOnNotNull();
        }
    }
}
