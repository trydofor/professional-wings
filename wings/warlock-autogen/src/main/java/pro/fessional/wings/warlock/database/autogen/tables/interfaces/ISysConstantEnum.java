/*
 * This file is generated by jOOQ.
 */
package pro.fessional.wings.warlock.database.autogen.tables.interfaces;


import jakarta.annotation.Generated;

import java.io.Serializable;


/**
 * The table <code>wings_warlock.sys_constant_enum</code>.
 */
@Generated(
    value = {
        "https://www.jooq.org",
        "jOOQ version:3.14.16",
        "schema version:2020102501"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public interface ISysConstantEnum extends Serializable {

    /**
     * Setter for <code>sys_constant_enum.id</code>.
     */
    public void setId(Integer value);

    /**
     * Getter for <code>sys_constant_enum.id</code>.
     */
    public Integer getId();

    /**
     * Setter for <code>sys_constant_enum.type</code>.
     */
    public void setType(String value);

    /**
     * Getter for <code>sys_constant_enum.type</code>.
     */
    public String getType();

    /**
     * Setter for <code>sys_constant_enum.code</code>.
     */
    public void setCode(String value);

    /**
     * Getter for <code>sys_constant_enum.code</code>.
     */
    public String getCode();

    /**
     * Setter for <code>sys_constant_enum.hint</code>.
     */
    public void setHint(String value);

    /**
     * Getter for <code>sys_constant_enum.hint</code>.
     */
    public String getHint();

    /**
     * Setter for <code>sys_constant_enum.info</code>.
     */
    public void setInfo(String value);

    /**
     * Getter for <code>sys_constant_enum.info</code>.
     */
    public String getInfo();

    // -------------------------------------------------------------------------
    // FROM and INTO
    // -------------------------------------------------------------------------

    /**
     * Load data from another generated Record/POJO implementing the common interface ISysConstantEnum
     */
    public void from(ISysConstantEnum from);

    /**
     * Copy data into another generated Record/POJO implementing the common interface ISysConstantEnum
     */
    public <E extends ISysConstantEnum> E into(E into);
}
