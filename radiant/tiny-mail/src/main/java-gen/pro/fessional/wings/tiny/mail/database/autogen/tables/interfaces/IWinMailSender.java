/*
 * This file is generated by jOOQ.
 */
package pro.fessional.wings.tiny.mail.database.autogen.tables.interfaces;


import pro.fessional.wings.faceless.service.journal.JournalAware;

import javax.annotation.processing.Generated;
import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * The table <code>wings.win_mail_sender</code>.
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
public interface IWinMailSender extends JournalAware, Serializable {

    /**
     * Setter for <code>win_mail_sender.id</code>.
     */
    public void setId(Long value);

    /**
     * Getter for <code>win_mail_sender.id</code>.
     */
    public Long getId();

    /**
     * Setter for <code>win_mail_sender.create_dt</code>.
     */
    public void setCreateDt(LocalDateTime value);

    /**
     * Getter for <code>win_mail_sender.create_dt</code>.
     */
    public LocalDateTime getCreateDt();

    /**
     * Setter for <code>win_mail_sender.modify_dt</code>.
     */
    public void setModifyDt(LocalDateTime value);

    /**
     * Getter for <code>win_mail_sender.modify_dt</code>.
     */
    public LocalDateTime getModifyDt();

    /**
     * Setter for <code>win_mail_sender.delete_dt</code>.
     */
    public void setDeleteDt(LocalDateTime value);

    /**
     * Getter for <code>win_mail_sender.delete_dt</code>.
     */
    public LocalDateTime getDeleteDt();

    /**
     * Setter for <code>win_mail_sender.commit_id</code>.
     */
    public void setCommitId(Long value);

    /**
     * Getter for <code>win_mail_sender.commit_id</code>.
     */
    public Long getCommitId();

    /**
     * Setter for <code>win_mail_sender.mail_apps</code>.
     */
    public void setMailApps(String value);

    /**
     * Getter for <code>win_mail_sender.mail_apps</code>.
     */
    public String getMailApps();

    /**
     * Setter for <code>win_mail_sender.mail_runs</code>.
     */
    public void setMailRuns(String value);

    /**
     * Getter for <code>win_mail_sender.mail_runs</code>.
     */
    public String getMailRuns();

    /**
     * Setter for <code>win_mail_sender.mail_conf</code>.
     */
    public void setMailConf(String value);

    /**
     * Getter for <code>win_mail_sender.mail_conf</code>.
     */
    public String getMailConf();

    /**
     * Setter for <code>win_mail_sender.mail_from</code>.
     */
    public void setMailFrom(String value);

    /**
     * Getter for <code>win_mail_sender.mail_from</code>.
     */
    public String getMailFrom();

    /**
     * Setter for <code>win_mail_sender.mail_to</code>.
     */
    public void setMailTo(String value);

    /**
     * Getter for <code>win_mail_sender.mail_to</code>.
     */
    public String getMailTo();

    /**
     * Setter for <code>win_mail_sender.mail_cc</code>.
     */
    public void setMailCc(String value);

    /**
     * Getter for <code>win_mail_sender.mail_cc</code>.
     */
    public String getMailCc();

    /**
     * Setter for <code>win_mail_sender.mail_bcc</code>.
     */
    public void setMailBcc(String value);

    /**
     * Getter for <code>win_mail_sender.mail_bcc</code>.
     */
    public String getMailBcc();

    /**
     * Setter for <code>win_mail_sender.mail_reply</code>.
     */
    public void setMailReply(String value);

    /**
     * Getter for <code>win_mail_sender.mail_reply</code>.
     */
    public String getMailReply();

    /**
     * Setter for <code>win_mail_sender.mail_subj</code>.
     */
    public void setMailSubj(String value);

    /**
     * Getter for <code>win_mail_sender.mail_subj</code>.
     */
    public String getMailSubj();

    /**
     * Setter for <code>win_mail_sender.mail_text</code>.
     */
    public void setMailText(String value);

    /**
     * Getter for <code>win_mail_sender.mail_text</code>.
     */
    public String getMailText();

    /**
     * Setter for <code>win_mail_sender.mail_html</code>.
     */
    public void setMailHtml(Boolean value);

    /**
     * Getter for <code>win_mail_sender.mail_html</code>.
     */
    public Boolean getMailHtml();

    /**
     * Setter for <code>win_mail_sender.mail_file</code>.
     */
    public void setMailFile(String value);

    /**
     * Getter for <code>win_mail_sender.mail_file</code>.
     */
    public String getMailFile();

    /**
     * Setter for <code>win_mail_sender.mail_mark</code>.
     */
    public void setMailMark(String value);

    /**
     * Getter for <code>win_mail_sender.mail_mark</code>.
     */
    public String getMailMark();

    /**
     * Setter for <code>win_mail_sender.mail_date</code>.
     */
    public void setMailDate(LocalDateTime value);

    /**
     * Getter for <code>win_mail_sender.mail_date</code>.
     */
    public LocalDateTime getMailDate();

    /**
     * Setter for <code>win_mail_sender.lazy_bean</code>.
     */
    public void setLazyBean(String value);

    /**
     * Getter for <code>win_mail_sender.lazy_bean</code>.
     */
    public String getLazyBean();

    /**
     * Setter for <code>win_mail_sender.lazy_para</code>.
     */
    public void setLazyPara(String value);

    /**
     * Getter for <code>win_mail_sender.lazy_para</code>.
     */
    public String getLazyPara();

    /**
     * Setter for <code>win_mail_sender.last_send</code>.
     */
    public void setLastSend(LocalDateTime value);

    /**
     * Getter for <code>win_mail_sender.last_send</code>.
     */
    public LocalDateTime getLastSend();

    /**
     * Setter for <code>win_mail_sender.last_fail</code>.
     */
    public void setLastFail(String value);

    /**
     * Getter for <code>win_mail_sender.last_fail</code>.
     */
    public String getLastFail();

    /**
     * Setter for <code>win_mail_sender.last_done</code>.
     */
    public void setLastDone(LocalDateTime value);

    /**
     * Getter for <code>win_mail_sender.last_done</code>.
     */
    public LocalDateTime getLastDone();

    /**
     * Setter for <code>win_mail_sender.last_cost</code>.
     */
    public void setLastCost(Integer value);

    /**
     * Getter for <code>win_mail_sender.last_cost</code>.
     */
    public Integer getLastCost();

    /**
     * Setter for <code>win_mail_sender.next_send</code>.
     */
    public void setNextSend(LocalDateTime value);

    /**
     * Getter for <code>win_mail_sender.next_send</code>.
     */
    public LocalDateTime getNextSend();

    /**
     * Setter for <code>win_mail_sender.next_lock</code>.
     */
    public void setNextLock(Integer value);

    /**
     * Getter for <code>win_mail_sender.next_lock</code>.
     */
    public Integer getNextLock();

    /**
     * Setter for <code>win_mail_sender.sum_send</code>.
     */
    public void setSumSend(Integer value);

    /**
     * Getter for <code>win_mail_sender.sum_send</code>.
     */
    public Integer getSumSend();

    /**
     * Setter for <code>win_mail_sender.sum_fail</code>.
     */
    public void setSumFail(Integer value);

    /**
     * Getter for <code>win_mail_sender.sum_fail</code>.
     */
    public Integer getSumFail();

    /**
     * Setter for <code>win_mail_sender.sum_done</code>.
     */
    public void setSumDone(Integer value);

    /**
     * Getter for <code>win_mail_sender.sum_done</code>.
     */
    public Integer getSumDone();

    /**
     * Setter for <code>win_mail_sender.max_fail</code>.
     */
    public void setMaxFail(Integer value);

    /**
     * Getter for <code>win_mail_sender.max_fail</code>.
     */
    public Integer getMaxFail();

    /**
     * Setter for <code>win_mail_sender.max_done</code>.
     */
    public void setMaxDone(Integer value);

    /**
     * Getter for <code>win_mail_sender.max_done</code>.
     */
    public Integer getMaxDone();

    /**
     * Setter for <code>win_mail_sender.ref_type</code>.
     */
    public void setRefType(Integer value);

    /**
     * Getter for <code>win_mail_sender.ref_type</code>.
     */
    public Integer getRefType();

    /**
     * Setter for <code>win_mail_sender.ref_key1</code>.
     */
    public void setRefKey1(Long value);

    /**
     * Getter for <code>win_mail_sender.ref_key1</code>.
     */
    public Long getRefKey1();

    /**
     * Setter for <code>win_mail_sender.ref_key2</code>.
     */
    public void setRefKey2(String value);

    /**
     * Getter for <code>win_mail_sender.ref_key2</code>.
     */
    public String getRefKey2();

    // -------------------------------------------------------------------------
    // FROM and INTO
    // -------------------------------------------------------------------------

    /**
     * Load data from another generated Record/POJO implementing the common
     * interface IWinMailSender
     */
    public void from(IWinMailSender from);

    /**
     * Copy data into another generated Record/POJO implementing the common
     * interface IWinMailSender
     */
    public <E extends IWinMailSender> E into(E into);
}
