/*
 * This file is generated by jOOQ.
 */
package pro.fessional.wings.faceless.database.autogen.tables;


import org.jetbrains.annotations.NotNull;
import org.jooq.Field;
import org.jooq.Name;
import org.jooq.Row5;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.Internal;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;
import pro.fessional.wings.faceless.database.autogen.DefaultSchema;
import pro.fessional.wings.faceless.database.autogen.tables.records.SysStandardI18nRecord;
import pro.fessional.wings.faceless.database.jooq.WingsJournalTable;

import javax.annotation.Generated;
import java.util.Arrays;
import java.util.List;


/**
 * The table <code>wings.sys_standard_i18n</code>.
 */
@Generated(
    value = {
        "https://www.jooq.org",
        "jOOQ version:3.14.4",
        "schema version:2019060101"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class SysStandardI18nTable extends TableImpl<SysStandardI18nRecord> implements WingsJournalTable<SysStandardI18nTable> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>sys_standard_i18n</code>
     */
    public static final SysStandardI18nTable SysStandardI18n = new SysStandardI18nTable();
    public static final SysStandardI18nTable asM5 = SysStandardI18n.as(pro.fessional.wings.faceless.database.jooq.WingsJooqEnv.uniqueAlias());

    /**
     * The class holding records for this type
     */
    @Override
    public Class<SysStandardI18nRecord> getRecordType() {
        return SysStandardI18nRecord.class;
    }

    /**
     * The column <code>sys_standard_i18n.base</code>.
     */
    public final TableField<SysStandardI18nRecord, String> Base = createField(DSL.name("base"), SQLDataType.VARCHAR(100).nullable(false), this, "");

    /**
     * The column <code>sys_standard_i18n.kind</code>.
     */
    public final TableField<SysStandardI18nRecord, String> Kind = createField(DSL.name("kind"), SQLDataType.VARCHAR(100).nullable(false), this, "");

    /**
     * The column <code>sys_standard_i18n.ukey</code>.
     */
    public final TableField<SysStandardI18nRecord, String> Ukey = createField(DSL.name("ukey"), SQLDataType.VARCHAR(200).nullable(false), this, "");

    /**
     * The column <code>sys_standard_i18n.lang</code>.
     */
    public final TableField<SysStandardI18nRecord, String> Lang = createField(DSL.name("lang"), SQLDataType.CHAR(5).nullable(false), this, "");

    /**
     * The column <code>sys_standard_i18n.hint</code>.
     */
    public final TableField<SysStandardI18nRecord, String> Hint = createField(DSL.name("hint"), SQLDataType.VARCHAR(3000).nullable(false), this, "");

    private SysStandardI18nTable(Name alias, Table<SysStandardI18nRecord> aliased) {
        this(alias, aliased, null);
    }

    private SysStandardI18nTable(Name alias, Table<SysStandardI18nRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>sys_standard_i18n</code> table reference
     */
    public SysStandardI18nTable(String alias) {
        this(DSL.name(alias), SysStandardI18n);
    }

    /**
     * Create an aliased <code>sys_standard_i18n</code> table reference
     */
    public SysStandardI18nTable(Name alias) {
        this(alias, SysStandardI18n);
    }

    /**
     * Create a <code>sys_standard_i18n</code> table reference
     */
    public SysStandardI18nTable() {
        this(DSL.name("sys_standard_i18n"), null);
    }

    @Override
    public Schema getSchema() {
        return DefaultSchema.DEFAULT_SCHEMA;
    }

    @Override
    public UniqueKey<SysStandardI18nRecord> getPrimaryKey() {
        return Internal.createUniqueKey(SysStandardI18nTable.SysStandardI18n, DSL.name("KEY_sys_standard_i18n_PRIMARY"), new TableField[] { SysStandardI18nTable.SysStandardI18n.Base, SysStandardI18nTable.SysStandardI18n.Kind, SysStandardI18nTable.SysStandardI18n.Ukey, SysStandardI18nTable.SysStandardI18n.Lang }, true);
    }

    @Override
    public List<UniqueKey<SysStandardI18nRecord>> getKeys() {
        return Arrays.<UniqueKey<SysStandardI18nRecord>>asList(
              Internal.createUniqueKey(SysStandardI18nTable.SysStandardI18n, DSL.name("KEY_sys_standard_i18n_PRIMARY"), new TableField[] { SysStandardI18nTable.SysStandardI18n.Base, SysStandardI18nTable.SysStandardI18n.Kind, SysStandardI18nTable.SysStandardI18n.Ukey, SysStandardI18nTable.SysStandardI18n.Lang }, true)
        );
    }

    @Override
    public SysStandardI18nTable as(String alias) {
        return new SysStandardI18nTable(DSL.name(alias), this);
    }

    @Override
    public SysStandardI18nTable as(Name alias) {
        return new SysStandardI18nTable(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public SysStandardI18nTable rename(String name) {
        return new SysStandardI18nTable(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public SysStandardI18nTable rename(Name name) {
        return new SysStandardI18nTable(name, null);
    }

    // -------------------------------------------------------------------------
    // Row5 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row5<String, String, String, String, String> fieldsRow() {
        return (Row5) super.fieldsRow();
    }


    /**
     * alias asM5
     */
    @Override
    @NotNull
    public SysStandardI18nTable getAliasTable() {
        return asM5;
    }
}
