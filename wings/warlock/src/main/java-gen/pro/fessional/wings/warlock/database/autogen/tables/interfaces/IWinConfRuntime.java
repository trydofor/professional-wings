/*
 * This file is generated by jOOQ.
 */
package pro.fessional.wings.warlock.database.autogen.tables.interfaces;


import javax.annotation.processing.Generated;
import java.io.Serializable;


/**
 * The table <code>wings.win_conf_runtime</code>.
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
public interface IWinConfRuntime extends Serializable {

    /**
     * Setter for <code>win_conf_runtime.key</code>.
     */
    public void setKey(String value);

    /**
     * Getter for <code>win_conf_runtime.key</code>.
     */
    public String getKey();

    /**
     * Setter for <code>win_conf_runtime.enabled</code>.
     */
    public void setEnabled(Boolean value);

    /**
     * Getter for <code>win_conf_runtime.enabled</code>.
     */
    public Boolean getEnabled();

    /**
     * Setter for <code>win_conf_runtime.current</code>.
     */
    public void setCurrent(String value);

    /**
     * Getter for <code>win_conf_runtime.current</code>.
     */
    public String getCurrent();

    /**
     * Setter for <code>win_conf_runtime.previous</code>.
     */
    public void setPrevious(String value);

    /**
     * Getter for <code>win_conf_runtime.previous</code>.
     */
    public String getPrevious();

    /**
     * Setter for <code>win_conf_runtime.initial</code>.
     */
    public void setInitial(String value);

    /**
     * Getter for <code>win_conf_runtime.initial</code>.
     */
    public String getInitial();

    /**
     * Setter for <code>win_conf_runtime.outline</code>.
     */
    public void setOutline(String value);

    /**
     * Getter for <code>win_conf_runtime.outline</code>.
     */
    public String getOutline();

    /**
     * Setter for <code>win_conf_runtime.comment</code>.
     */
    public void setComment(String value);

    /**
     * Getter for <code>win_conf_runtime.comment</code>.
     */
    public String getComment();

    /**
     * Setter for <code>win_conf_runtime.handler</code>.
     */
    public void setHandler(String value);

    /**
     * Getter for <code>win_conf_runtime.handler</code>.
     */
    public String getHandler();

    // -------------------------------------------------------------------------
    // FROM and INTO
    // -------------------------------------------------------------------------

    /**
     * Load data from another generated Record/POJO implementing the common
     * interface IWinConfRuntime
     */
    public void from(IWinConfRuntime from);

    /**
     * Copy data into another generated Record/POJO implementing the common
     * interface IWinConfRuntime
     */
    public <E extends IWinConfRuntime> E into(E into);
}
