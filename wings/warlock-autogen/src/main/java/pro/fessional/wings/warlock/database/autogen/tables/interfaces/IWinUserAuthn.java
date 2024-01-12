/*
 * This file is generated by jOOQ.
 */
package pro.fessional.wings.warlock.database.autogen.tables.interfaces;


import pro.fessional.wings.faceless.service.journal.JournalAware;

import javax.annotation.processing.Generated;
import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * The table <code>wings.win_user_authn</code>.
 */
@Generated(
    value = {
        "https://www.jooq.org",
        "jOOQ version:3.18.7",
        "schema version:2020102701"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public interface IWinUserAuthn extends JournalAware, Serializable {

    /**
     * Setter for <code>win_user_authn.id</code>.
     */
    public void setId(Long value);

    /**
     * Getter for <code>win_user_authn.id</code>.
     */
    public Long getId();

    /**
     * Setter for <code>win_user_authn.create_dt</code>.
     */
    public void setCreateDt(LocalDateTime value);

    /**
     * Getter for <code>win_user_authn.create_dt</code>.
     */
    public LocalDateTime getCreateDt();

    /**
     * Setter for <code>win_user_authn.modify_dt</code>.
     */
    public void setModifyDt(LocalDateTime value);

    /**
     * Getter for <code>win_user_authn.modify_dt</code>.
     */
    public LocalDateTime getModifyDt();

    /**
     * Setter for <code>win_user_authn.delete_dt</code>.
     */
    public void setDeleteDt(LocalDateTime value);

    /**
     * Getter for <code>win_user_authn.delete_dt</code>.
     */
    public LocalDateTime getDeleteDt();

    /**
     * Setter for <code>win_user_authn.commit_id</code>.
     */
    public void setCommitId(Long value);

    /**
     * Getter for <code>win_user_authn.commit_id</code>.
     */
    public Long getCommitId();

    /**
     * Setter for <code>win_user_authn.user_id</code>.
     */
    public void setUserId(Long value);

    /**
     * Getter for <code>win_user_authn.user_id</code>.
     */
    public Long getUserId();

    /**
     * Setter for <code>win_user_authn.auth_type</code>.
     */
    public void setAuthType(String value);

    /**
     * Getter for <code>win_user_authn.auth_type</code>.
     */
    public String getAuthType();

    /**
     * Setter for <code>win_user_authn.username</code>.
     */
    public void setUsername(String value);

    /**
     * Getter for <code>win_user_authn.username</code>.
     */
    public String getUsername();

    /**
     * Setter for <code>win_user_authn.password</code>.
     */
    public void setPassword(String value);

    /**
     * Getter for <code>win_user_authn.password</code>.
     */
    public String getPassword();

    /**
     * Setter for <code>win_user_authn.extra_para</code>.
     */
    public void setExtraPara(String value);

    /**
     * Getter for <code>win_user_authn.extra_para</code>.
     */
    public String getExtraPara();

    /**
     * Setter for <code>win_user_authn.extra_user</code>.
     */
    public void setExtraUser(String value);

    /**
     * Getter for <code>win_user_authn.extra_user</code>.
     */
    public String getExtraUser();

    /**
     * Setter for <code>win_user_authn.expired_dt</code>.
     */
    public void setExpiredDt(LocalDateTime value);

    /**
     * Getter for <code>win_user_authn.expired_dt</code>.
     */
    public LocalDateTime getExpiredDt();

    /**
     * Setter for <code>win_user_authn.failed_cnt</code>.
     */
    public void setFailedCnt(Integer value);

    /**
     * Getter for <code>win_user_authn.failed_cnt</code>.
     */
    public Integer getFailedCnt();

    /**
     * Setter for <code>win_user_authn.failed_max</code>.
     */
    public void setFailedMax(Integer value);

    /**
     * Getter for <code>win_user_authn.failed_max</code>.
     */
    public Integer getFailedMax();

    // -------------------------------------------------------------------------
    // FROM and INTO
    // -------------------------------------------------------------------------

    /**
     * Load data from another generated Record/POJO implementing the common
     * interface IWinUserAuthn
     */
    public void from(IWinUserAuthn from);

    /**
     * Copy data into another generated Record/POJO implementing the common
     * interface IWinUserAuthn
     */
    public <E extends IWinUserAuthn> E into(E into);
}
