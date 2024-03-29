/*
 * This file is generated by jOOQ.
 */
package pro.fessional.wings.faceless.app.database.autogen.tables.records;


import org.jooq.Field;
import org.jooq.Record4;
import org.jooq.Record5;
import org.jooq.Row5;
import org.jooq.impl.UpdatableRecordImpl;
import pro.fessional.wings.faceless.app.database.autogen.tables.SysStandardI18nTable;
import pro.fessional.wings.faceless.app.database.autogen.tables.interfaces.ISysStandardI18n;
import pro.fessional.wings.faceless.app.database.autogen.tables.pojos.SysStandardI18n;

import javax.annotation.processing.Generated;


/**
 * The table <code>wings.sys_standard_i18n</code>.
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
public class SysStandardI18nRecord extends UpdatableRecordImpl<SysStandardI18nRecord> implements Record5<String, String, String, String, String>, ISysStandardI18n {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>sys_standard_i18n.base</code>.
     */
    @Override
    public void setBase(String value) {
        set(0, value);
    }

    /**
     * Getter for <code>sys_standard_i18n.base</code>.
     */
    @Override
    public String getBase() {
        return (String) get(0);
    }

    /**
     * Setter for <code>sys_standard_i18n.kind</code>.
     */
    @Override
    public void setKind(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>sys_standard_i18n.kind</code>.
     */
    @Override
    public String getKind() {
        return (String) get(1);
    }

    /**
     * Setter for <code>sys_standard_i18n.ukey</code>.
     */
    @Override
    public void setUkey(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>sys_standard_i18n.ukey</code>.
     */
    @Override
    public String getUkey() {
        return (String) get(2);
    }

    /**
     * Setter for <code>sys_standard_i18n.lang</code>.
     */
    @Override
    public void setLang(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>sys_standard_i18n.lang</code>.
     */
    @Override
    public String getLang() {
        return (String) get(3);
    }

    /**
     * Setter for <code>sys_standard_i18n.hint</code>.
     */
    @Override
    public void setHint(String value) {
        set(4, value);
    }

    /**
     * Getter for <code>sys_standard_i18n.hint</code>.
     */
    @Override
    public String getHint() {
        return (String) get(4);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record4<String, String, String, String> key() {
        return (Record4) super.key();
    }

    // -------------------------------------------------------------------------
    // Record5 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row5<String, String, String, String, String> fieldsRow() {
        return (Row5) super.fieldsRow();
    }

    @Override
    public Row5<String, String, String, String, String> valuesRow() {
        return (Row5) super.valuesRow();
    }

    @Override
    public Field<String> field1() {
        return SysStandardI18nTable.SysStandardI18n.Base;
    }

    @Override
    public Field<String> field2() {
        return SysStandardI18nTable.SysStandardI18n.Kind;
    }

    @Override
    public Field<String> field3() {
        return SysStandardI18nTable.SysStandardI18n.Ukey;
    }

    @Override
    public Field<String> field4() {
        return SysStandardI18nTable.SysStandardI18n.Lang;
    }

    @Override
    public Field<String> field5() {
        return SysStandardI18nTable.SysStandardI18n.Hint;
    }

    @Override
    public String component1() {
        return getBase();
    }

    @Override
    public String component2() {
        return getKind();
    }

    @Override
    public String component3() {
        return getUkey();
    }

    @Override
    public String component4() {
        return getLang();
    }

    @Override
    public String component5() {
        return getHint();
    }

    @Override
    public String value1() {
        return getBase();
    }

    @Override
    public String value2() {
        return getKind();
    }

    @Override
    public String value3() {
        return getUkey();
    }

    @Override
    public String value4() {
        return getLang();
    }

    @Override
    public String value5() {
        return getHint();
    }

    @Override
    public SysStandardI18nRecord value1(String value) {
        setBase(value);
        return this;
    }

    @Override
    public SysStandardI18nRecord value2(String value) {
        setKind(value);
        return this;
    }

    @Override
    public SysStandardI18nRecord value3(String value) {
        setUkey(value);
        return this;
    }

    @Override
    public SysStandardI18nRecord value4(String value) {
        setLang(value);
        return this;
    }

    @Override
    public SysStandardI18nRecord value5(String value) {
        setHint(value);
        return this;
    }

    @Override
    public SysStandardI18nRecord values(String value1, String value2, String value3, String value4, String value5) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        return this;
    }

    // -------------------------------------------------------------------------
    // FROM and INTO
    // -------------------------------------------------------------------------

    @Override
    public void from(ISysStandardI18n from) {
        setBase(from.getBase());
        setKind(from.getKind());
        setUkey(from.getUkey());
        setLang(from.getLang());
        setHint(from.getHint());
        resetChangedOnNotNull();
    }

    @Override
    public <E extends ISysStandardI18n> E into(E into) {
        into.from(this);
        return into;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached SysStandardI18nRecord
     */
    public SysStandardI18nRecord() {
        super(SysStandardI18nTable.SysStandardI18n);
    }

    /**
     * Create a detached, initialised SysStandardI18nRecord
     */
    public SysStandardI18nRecord(String base, String kind, String ukey, String lang, String hint) {
        super(SysStandardI18nTable.SysStandardI18n);

        setBase(base);
        setKind(kind);
        setUkey(ukey);
        setLang(lang);
        setHint(hint);
        resetChangedOnNotNull();
    }

    /**
     * Create a detached, initialised SysStandardI18nRecord
     */
    public SysStandardI18nRecord(SysStandardI18n value) {
        super(SysStandardI18nTable.SysStandardI18n);

        if (value != null) {
            setBase(value.getBase());
            setKind(value.getKind());
            setUkey(value.getUkey());
            setLang(value.getLang());
            setHint(value.getHint());
            resetChangedOnNotNull();
        }
    }
}
