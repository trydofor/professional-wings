/*
 * This file is generated by jOOQ.
 */
package pro.fessional.wings.warlock.database.autogen.tables;


import jakarta.annotation.Generated;
import org.jetbrains.annotations.NotNull;
import org.jooq.Field;
import org.jooq.Name;
import org.jooq.Row6;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.Internal;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;
import pro.fessional.wings.faceless.database.jooq.WingsJournalTable;
import pro.fessional.wings.warlock.database.autogen.DefaultSchemaWarlock;
import pro.fessional.wings.warlock.database.autogen.tables.records.WinConfRuntimeRecord;

import java.util.Arrays;
import java.util.List;


/**
 * The table <code>wings.win_conf_runtime</code>.
 */
@Generated(
    value = {
        "https://www.jooq.org",
        "jOOQ version:3.14.16",
        "schema version:2020102701"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class WinConfRuntimeTable extends TableImpl<WinConfRuntimeRecord> implements WingsJournalTable<WinConfRuntimeTable> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>win_conf_runtime</code>
     */
    public static final WinConfRuntimeTable WinConfRuntime = new WinConfRuntimeTable();
    public static final WinConfRuntimeTable asS4 = WinConfRuntime.as(pro.fessional.wings.faceless.database.jooq.WingsJooqEnv.uniqueAlias());

    /**
     * The class holding records for this type
     */
    @Override
    public Class<WinConfRuntimeRecord> getRecordType() {
        return WinConfRuntimeRecord.class;
    }

    /**
     * The column <code>win_conf_runtime.key</code>.
     */
    public final TableField<WinConfRuntimeRecord, String> Key = createField(DSL.name("key"), SQLDataType.VARCHAR(200).nullable(false), this, "");

    /**
     * The column <code>win_conf_runtime.current</code>.
     */
    public final TableField<WinConfRuntimeRecord, String> Current = createField(DSL.name("current"), SQLDataType.VARCHAR(5000).nullable(false).defaultValue(DSL.inline("", SQLDataType.VARCHAR)), this, "");

    /**
     * The column <code>win_conf_runtime.previous</code>.
     */
    public final TableField<WinConfRuntimeRecord, String> Previous = createField(DSL.name("previous"), SQLDataType.VARCHAR(5000).nullable(false).defaultValue(DSL.inline("", SQLDataType.VARCHAR)), this, "");

    /**
     * The column <code>win_conf_runtime.initial</code>.
     */
    public final TableField<WinConfRuntimeRecord, String> Initial = createField(DSL.name("initial"), SQLDataType.VARCHAR(5000).nullable(false).defaultValue(DSL.inline("", SQLDataType.VARCHAR)), this, "");

    /**
     * The column <code>win_conf_runtime.comment</code>.
     */
    public final TableField<WinConfRuntimeRecord, String> Comment = createField(DSL.name("comment"), SQLDataType.VARCHAR(500).nullable(false).defaultValue(DSL.inline("", SQLDataType.VARCHAR)), this, "");

    /**
     * The column <code>win_conf_runtime.handler</code>.
     */
    public final TableField<WinConfRuntimeRecord, String> Handler = createField(DSL.name("handler"), SQLDataType.VARCHAR(200).nullable(false).defaultValue(DSL.inline("prop", SQLDataType.VARCHAR)), this, "");

    private WinConfRuntimeTable(Name alias, Table<WinConfRuntimeRecord> aliased) {
        this(alias, aliased, null);
    }

    private WinConfRuntimeTable(Name alias, Table<WinConfRuntimeRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>win_conf_runtime</code> table reference
     */
    public WinConfRuntimeTable(String alias) {
        this(DSL.name(alias), WinConfRuntime);
    }

    /**
     * Create an aliased <code>win_conf_runtime</code> table reference
     */
    public WinConfRuntimeTable(Name alias) {
        this(alias, WinConfRuntime);
    }

    /**
     * Create a <code>win_conf_runtime</code> table reference
     */
    public WinConfRuntimeTable() {
        this(DSL.name("win_conf_runtime"), null);
    }

    @Override
    public Schema getSchema() {
        return DefaultSchemaWarlock.DEFAULT_SCHEMA;
    }

    @Override
    public UniqueKey<WinConfRuntimeRecord> getPrimaryKey() {
        return Internal.createUniqueKey(WinConfRuntimeTable.WinConfRuntime, DSL.name("KEY_win_conf_runtime_PRIMARY"), new TableField[] { WinConfRuntimeTable.WinConfRuntime.Key }, true);
    }

    @Override
    public List<UniqueKey<WinConfRuntimeRecord>> getKeys() {
        return Arrays.<UniqueKey<WinConfRuntimeRecord>>asList(
              Internal.createUniqueKey(WinConfRuntimeTable.WinConfRuntime, DSL.name("KEY_win_conf_runtime_PRIMARY"), new TableField[] { WinConfRuntimeTable.WinConfRuntime.Key }, true)
        );
    }

    @Override
    public WinConfRuntimeTable as(String alias) {
        return new WinConfRuntimeTable(DSL.name(alias), this);
    }

    @Override
    public WinConfRuntimeTable as(Name alias) {
        return new WinConfRuntimeTable(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public WinConfRuntimeTable rename(String name) {
        return new WinConfRuntimeTable(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public WinConfRuntimeTable rename(Name name) {
        return new WinConfRuntimeTable(name, null);
    }

    // -------------------------------------------------------------------------
    // Row6 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row6<String, String, String, String, String, String> fieldsRow() {
        return (Row6) super.fieldsRow();
    }


    /**
     * alias asS4
     */
    @Override
    @NotNull
    public WinConfRuntimeTable getAliasTable() {
        return asS4;
    }
}
