/*
 * This file is generated by jOOQ.
 */
package pro.fessional.wings.faceless.app.database.autogen.tables.interfaces;


import pro.fessional.wings.faceless.service.journal.JournalAware;

import javax.annotation.processing.Generated;
import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * The table <code>wings.tst_sharding</code>.
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
public interface ITstSharding extends JournalAware, Serializable {

    /**
     * Setter for <code>tst_sharding.id</code>.
     */
    public void setId(Long value);

    /**
     * Getter for <code>tst_sharding.id</code>.
     */
    public Long getId();

    /**
     * Setter for <code>tst_sharding.create_dt</code>.
     */
    public void setCreateDt(LocalDateTime value);

    /**
     * Getter for <code>tst_sharding.create_dt</code>.
     */
    public LocalDateTime getCreateDt();

    /**
     * Setter for <code>tst_sharding.modify_dt</code>.
     */
    public void setModifyDt(LocalDateTime value);

    /**
     * Getter for <code>tst_sharding.modify_dt</code>.
     */
    public LocalDateTime getModifyDt();

    /**
     * Setter for <code>tst_sharding.delete_dt</code>.
     */
    public void setDeleteDt(LocalDateTime value);

    /**
     * Getter for <code>tst_sharding.delete_dt</code>.
     */
    public LocalDateTime getDeleteDt();

    /**
     * Setter for <code>tst_sharding.commit_id</code>.
     */
    public void setCommitId(Long value);

    /**
     * Getter for <code>tst_sharding.commit_id</code>.
     */
    public Long getCommitId();

    /**
     * Setter for <code>tst_sharding.login_info</code>.
     */
    public void setLoginInfo(String value);

    /**
     * Getter for <code>tst_sharding.login_info</code>.
     */
    public String getLoginInfo();

    /**
     * Setter for <code>tst_sharding.other_info</code>.
     */
    public void setOtherInfo(String value);

    /**
     * Getter for <code>tst_sharding.other_info</code>.
     */
    public String getOtherInfo();

    /**
     * Setter for <code>tst_sharding.language</code>.
     */
    public void setLanguage(Integer value);

    /**
     * Getter for <code>tst_sharding.language</code>.
     */
    public Integer getLanguage();

    // -------------------------------------------------------------------------
    // FROM and INTO
    // -------------------------------------------------------------------------

    /**
     * Load data from another generated Record/POJO implementing the common
     * interface ITstSharding
     */
    public void from(ITstSharding from);

    /**
     * Copy data into another generated Record/POJO implementing the common
     * interface ITstSharding
     */
    public <E extends ITstSharding> E into(E into);
}
