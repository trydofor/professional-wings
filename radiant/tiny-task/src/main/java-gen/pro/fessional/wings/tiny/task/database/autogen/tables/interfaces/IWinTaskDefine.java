/*
 * This file is generated by jOOQ.
 */
package pro.fessional.wings.tiny.task.database.autogen.tables.interfaces;


import pro.fessional.wings.faceless.service.journal.JournalAware;

import javax.annotation.processing.Generated;
import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * The table <code>wings.win_task_define</code>.
 */
@Generated(
    value = {
        "https://www.jooq.org",
        "jOOQ version:3.18.9",
        "schema version:2020102801"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public interface IWinTaskDefine extends JournalAware, Serializable {

    /**
     * Setter for <code>win_task_define.id</code>.
     */
    public void setId(Long value);

    /**
     * Getter for <code>win_task_define.id</code>.
     */
    public Long getId();

    /**
     * Setter for <code>win_task_define.create_dt</code>.
     */
    public void setCreateDt(LocalDateTime value);

    /**
     * Getter for <code>win_task_define.create_dt</code>.
     */
    public LocalDateTime getCreateDt();

    /**
     * Setter for <code>win_task_define.modify_dt</code>.
     */
    public void setModifyDt(LocalDateTime value);

    /**
     * Getter for <code>win_task_define.modify_dt</code>.
     */
    public LocalDateTime getModifyDt();

    /**
     * Setter for <code>win_task_define.delete_dt</code>.
     */
    public void setDeleteDt(LocalDateTime value);

    /**
     * Getter for <code>win_task_define.delete_dt</code>.
     */
    public LocalDateTime getDeleteDt();

    /**
     * Setter for <code>win_task_define.commit_id</code>.
     */
    public void setCommitId(Long value);

    /**
     * Getter for <code>win_task_define.commit_id</code>.
     */
    public Long getCommitId();

    /**
     * Setter for <code>win_task_define.propkey</code>.
     */
    public void setPropkey(String value);

    /**
     * Getter for <code>win_task_define.propkey</code>.
     */
    public String getPropkey();

    /**
     * Setter for <code>win_task_define.enabled</code>.
     */
    public void setEnabled(Boolean value);

    /**
     * Getter for <code>win_task_define.enabled</code>.
     */
    public Boolean getEnabled();

    /**
     * Setter for <code>win_task_define.autorun</code>.
     */
    public void setAutorun(Boolean value);

    /**
     * Getter for <code>win_task_define.autorun</code>.
     */
    public Boolean getAutorun();

    /**
     * Setter for <code>win_task_define.version</code>.
     */
    public void setVersion(Integer value);

    /**
     * Getter for <code>win_task_define.version</code>.
     */
    public Integer getVersion();

    /**
     * Setter for <code>win_task_define.tasker_bean</code>.
     */
    public void setTaskerBean(String value);

    /**
     * Getter for <code>win_task_define.tasker_bean</code>.
     */
    public String getTaskerBean();

    /**
     * Setter for <code>win_task_define.tasker_para</code>.
     */
    public void setTaskerPara(String value);

    /**
     * Getter for <code>win_task_define.tasker_para</code>.
     */
    public String getTaskerPara();

    /**
     * Setter for <code>win_task_define.tasker_name</code>.
     */
    public void setTaskerName(String value);

    /**
     * Getter for <code>win_task_define.tasker_name</code>.
     */
    public String getTaskerName();

    /**
     * Setter for <code>win_task_define.tasker_fast</code>.
     */
    public void setTaskerFast(Boolean value);

    /**
     * Getter for <code>win_task_define.tasker_fast</code>.
     */
    public Boolean getTaskerFast();

    /**
     * Setter for <code>win_task_define.tasker_apps</code>.
     */
    public void setTaskerApps(String value);

    /**
     * Getter for <code>win_task_define.tasker_apps</code>.
     */
    public String getTaskerApps();

    /**
     * Setter for <code>win_task_define.tasker_runs</code>.
     */
    public void setTaskerRuns(String value);

    /**
     * Getter for <code>win_task_define.tasker_runs</code>.
     */
    public String getTaskerRuns();

    /**
     * Setter for <code>win_task_define.notice_bean</code>.
     */
    public void setNoticeBean(String value);

    /**
     * Getter for <code>win_task_define.notice_bean</code>.
     */
    public String getNoticeBean();

    /**
     * Setter for <code>win_task_define.notice_when</code>.
     */
    public void setNoticeWhen(String value);

    /**
     * Getter for <code>win_task_define.notice_when</code>.
     */
    public String getNoticeWhen();

    /**
     * Setter for <code>win_task_define.notice_conf</code>.
     */
    public void setNoticeConf(String value);

    /**
     * Getter for <code>win_task_define.notice_conf</code>.
     */
    public String getNoticeConf();

    /**
     * Setter for <code>win_task_define.timing_zone</code>.
     */
    public void setTimingZone(String value);

    /**
     * Getter for <code>win_task_define.timing_zone</code>.
     */
    public String getTimingZone();

    /**
     * Setter for <code>win_task_define.timing_type</code>.
     */
    public void setTimingType(String value);

    /**
     * Getter for <code>win_task_define.timing_type</code>.
     */
    public String getTimingType();

    /**
     * Setter for <code>win_task_define.timing_cron</code>.
     */
    public void setTimingCron(String value);

    /**
     * Getter for <code>win_task_define.timing_cron</code>.
     */
    public String getTimingCron();

    /**
     * Setter for <code>win_task_define.timing_idle</code>.
     */
    public void setTimingIdle(Integer value);

    /**
     * Getter for <code>win_task_define.timing_idle</code>.
     */
    public Integer getTimingIdle();

    /**
     * Setter for <code>win_task_define.timing_rate</code>.
     */
    public void setTimingRate(Integer value);

    /**
     * Getter for <code>win_task_define.timing_rate</code>.
     */
    public Integer getTimingRate();

    /**
     * Setter for <code>win_task_define.timing_tune</code>.
     */
    public void setTimingTune(Integer value);

    /**
     * Getter for <code>win_task_define.timing_tune</code>.
     */
    public Integer getTimingTune();

    /**
     * Setter for <code>win_task_define.timing_miss</code>.
     */
    public void setTimingMiss(Long value);

    /**
     * Getter for <code>win_task_define.timing_miss</code>.
     */
    public Long getTimingMiss();

    /**
     * Setter for <code>win_task_define.timing_beat</code>.
     */
    public void setTimingBeat(Long value);

    /**
     * Getter for <code>win_task_define.timing_beat</code>.
     */
    public Long getTimingBeat();

    /**
     * Setter for <code>win_task_define.during_from</code>.
     */
    public void setDuringFrom(String value);

    /**
     * Getter for <code>win_task_define.during_from</code>.
     */
    public String getDuringFrom();

    /**
     * Setter for <code>win_task_define.during_stop</code>.
     */
    public void setDuringStop(String value);

    /**
     * Getter for <code>win_task_define.during_stop</code>.
     */
    public String getDuringStop();

    /**
     * Setter for <code>win_task_define.during_exec</code>.
     */
    public void setDuringExec(Integer value);

    /**
     * Getter for <code>win_task_define.during_exec</code>.
     */
    public Integer getDuringExec();

    /**
     * Setter for <code>win_task_define.during_fail</code>.
     */
    public void setDuringFail(Integer value);

    /**
     * Getter for <code>win_task_define.during_fail</code>.
     */
    public Integer getDuringFail();

    /**
     * Setter for <code>win_task_define.during_done</code>.
     */
    public void setDuringDone(Integer value);

    /**
     * Getter for <code>win_task_define.during_done</code>.
     */
    public Integer getDuringDone();

    /**
     * Setter for <code>win_task_define.during_boot</code>.
     */
    public void setDuringBoot(Integer value);

    /**
     * Getter for <code>win_task_define.during_boot</code>.
     */
    public Integer getDuringBoot();

    /**
     * Setter for <code>win_task_define.result_keep</code>.
     */
    public void setResultKeep(Integer value);

    /**
     * Getter for <code>win_task_define.result_keep</code>.
     */
    public Integer getResultKeep();

    /**
     * Setter for <code>win_task_define.last_exec</code>.
     */
    public void setLastExec(LocalDateTime value);

    /**
     * Getter for <code>win_task_define.last_exec</code>.
     */
    public LocalDateTime getLastExec();

    /**
     * Setter for <code>win_task_define.last_exit</code>.
     */
    public void setLastExit(LocalDateTime value);

    /**
     * Getter for <code>win_task_define.last_exit</code>.
     */
    public LocalDateTime getLastExit();

    /**
     * Setter for <code>win_task_define.last_fail</code>.
     */
    public void setLastFail(Boolean value);

    /**
     * Getter for <code>win_task_define.last_fail</code>.
     */
    public Boolean getLastFail();

    /**
     * Setter for <code>win_task_define.next_exec</code>.
     */
    public void setNextExec(LocalDateTime value);

    /**
     * Getter for <code>win_task_define.next_exec</code>.
     */
    public LocalDateTime getNextExec();

    /**
     * Setter for <code>win_task_define.next_lock</code>.
     */
    public void setNextLock(Integer value);

    /**
     * Getter for <code>win_task_define.next_lock</code>.
     */
    public Integer getNextLock();

    /**
     * Setter for <code>win_task_define.dur_fail</code>.
     */
    public void setDurFail(Integer value);

    /**
     * Getter for <code>win_task_define.dur_fail</code>.
     */
    public Integer getDurFail();

    /**
     * Setter for <code>win_task_define.sum_exec</code>.
     */
    public void setSumExec(Integer value);

    /**
     * Getter for <code>win_task_define.sum_exec</code>.
     */
    public Integer getSumExec();

    /**
     * Setter for <code>win_task_define.sum_fail</code>.
     */
    public void setSumFail(Integer value);

    /**
     * Getter for <code>win_task_define.sum_fail</code>.
     */
    public Integer getSumFail();

    /**
     * Setter for <code>win_task_define.sum_done</code>.
     */
    public void setSumDone(Integer value);

    /**
     * Getter for <code>win_task_define.sum_done</code>.
     */
    public Integer getSumDone();

    // -------------------------------------------------------------------------
    // FROM and INTO
    // -------------------------------------------------------------------------

    /**
     * Load data from another generated Record/POJO implementing the common
     * interface IWinTaskDefine
     */
    public void from(IWinTaskDefine from);

    /**
     * Copy data into another generated Record/POJO implementing the common
     * interface IWinTaskDefine
     */
    public <E extends IWinTaskDefine> E into(E into);
}
