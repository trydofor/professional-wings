/*
 * This file is generated by jOOQ.
 */
package pro.fessional.wings.warlock.database.autogen.tables;


import org.jetbrains.annotations.NotNull;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Function7;
import org.jooq.Records;
import org.jooq.Row7;
import org.jooq.Schema;
import org.jooq.SelectField;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.Internal;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;
import pro.fessional.wings.faceless.convention.EmptyValue;
import pro.fessional.wings.faceless.database.jooq.WingsJournalTable;
import pro.fessional.wings.faceless.service.journal.JournalService;
import pro.fessional.wings.faceless.service.lightid.LightIdAware;
import pro.fessional.wings.warlock.database.autogen.DefaultSchemaWarlock;
import pro.fessional.wings.warlock.database.autogen.tables.records.WinRoleEntryRecord;

import javax.annotation.processing.Generated;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;


/**
 * The table <code>wings.win_role_entry</code>.
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
public class WinRoleEntryTable extends TableImpl<WinRoleEntryRecord> implements WingsJournalTable<WinRoleEntryTable>, LightIdAware {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>win_role_entry</code>
     */
    public static final WinRoleEntryTable WinRoleEntry = new WinRoleEntryTable();
    public static final WinRoleEntryTable asC2 = WinRoleEntry.as(pro.fessional.wings.faceless.database.jooq.WingsJooqEnv.uniqueAlias());

    /**
     * The class holding records for this type
     */
    @Override
    public Class<WinRoleEntryRecord> getRecordType() {
        return WinRoleEntryRecord.class;
    }

    /**
     * The column <code>win_role_entry.id</code>.
     */
    public final TableField<WinRoleEntryRecord, Long> Id = createField(DSL.name("id"), SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>win_role_entry.create_dt</code>.
     */
    public final TableField<WinRoleEntryRecord, LocalDateTime> CreateDt = createField(DSL.name("create_dt"), SQLDataType.LOCALDATETIME(3).nullable(false).defaultValue(DSL.field(DSL.raw("CURRENT_TIMESTAMP(3)"), SQLDataType.LOCALDATETIME)), this, "");

    /**
     * The column <code>win_role_entry.modify_dt</code>.
     */
    public final TableField<WinRoleEntryRecord, LocalDateTime> ModifyDt = createField(DSL.name("modify_dt"), SQLDataType.LOCALDATETIME(3).nullable(false).defaultValue(DSL.inline("1000-01-01 00:00:00.000", SQLDataType.LOCALDATETIME)), this, "");

    /**
     * The column <code>win_role_entry.delete_dt</code>.
     */
    public final TableField<WinRoleEntryRecord, LocalDateTime> DeleteDt = createField(DSL.name("delete_dt"), SQLDataType.LOCALDATETIME(3).nullable(false).defaultValue(DSL.inline("1000-01-01 00:00:00.000", SQLDataType.LOCALDATETIME)), this, "");

    /**
     * The column <code>win_role_entry.commit_id</code>.
     */
    public final TableField<WinRoleEntryRecord, Long> CommitId = createField(DSL.name("commit_id"), SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>win_role_entry.name</code>.
     */
    public final TableField<WinRoleEntryRecord, String> Name = createField(DSL.name("name"), SQLDataType.VARCHAR(50).nullable(false), this, "");

    /**
     * The column <code>win_role_entry.remark</code>.
     */
    public final TableField<WinRoleEntryRecord, String> Remark = createField(DSL.name("remark"), SQLDataType.VARCHAR(500).nullable(false).defaultValue(DSL.inline("", SQLDataType.VARCHAR)), this, "");

    private WinRoleEntryTable(org.jooq.Name alias, Table<WinRoleEntryRecord> aliased) {
        this(alias, aliased, null);
    }

    private WinRoleEntryTable(org.jooq.Name alias, Table<WinRoleEntryRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>win_role_entry</code> table reference
     */
    public WinRoleEntryTable(String alias) {
        this(DSL.name(alias), WinRoleEntry);
    }

    /**
     * Create an aliased <code>win_role_entry</code> table reference
     */
    public WinRoleEntryTable(org.jooq.Name alias) {
        this(alias, WinRoleEntry);
    }

    /**
     * Create a <code>win_role_entry</code> table reference
     */
    public WinRoleEntryTable() {
        this(DSL.name("win_role_entry"), null);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : DefaultSchemaWarlock.DEFAULT_SCHEMA;
    }

    @Override
    public UniqueKey<WinRoleEntryRecord> getPrimaryKey() {
        return Internal.createUniqueKey(WinRoleEntryTable.WinRoleEntry, DSL.name("KEY_win_role_entry_PRIMARY"), new TableField[] { WinRoleEntryTable.WinRoleEntry.Id }, true);
    }

    @Override
    public WinRoleEntryTable as(String alias) {
        return new WinRoleEntryTable(DSL.name(alias), this);
    }

    @Override
    public WinRoleEntryTable as(org.jooq.Name alias) {
        return new WinRoleEntryTable(alias, this);
    }

    @Override
    public WinRoleEntryTable as(Table<?> alias) {
        return new WinRoleEntryTable(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public WinRoleEntryTable rename(String name) {
        return new WinRoleEntryTable(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public WinRoleEntryTable rename(org.jooq.Name name) {
        return new WinRoleEntryTable(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public WinRoleEntryTable rename(Table<?> name) {
        return new WinRoleEntryTable(name.getQualifiedName(), null);
    }

    // -------------------------------------------------------------------------
    // Row7 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row7<Long, LocalDateTime, LocalDateTime, LocalDateTime, Long, String, String> fieldsRow() {
        return (Row7) super.fieldsRow();
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Function)}.
     */
    public <U> SelectField<U> mapping(Function7<? super Long, ? super LocalDateTime, ? super LocalDateTime, ? super LocalDateTime, ? super Long, ? super String, ? super String, ? extends U> from) {
        return convertFrom(Records.mapping(from));
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Class,
     * Function)}.
     */
    public <U> SelectField<U> mapping(Class<U> toType, Function7<? super Long, ? super LocalDateTime, ? super LocalDateTime, ? super LocalDateTime, ? super Long, ? super String, ? super String, ? extends U> from) {
        return convertFrom(toType, Records.mapping(from));
    }

    /**
     * LightIdAware seqName
     */
    @Override
    @NotNull
    public String getSeqName() {
        return "win_role_entry";
    }

    /**
     * alias asC2
     */
    @Override
    @NotNull
    public WinRoleEntryTable getAliasTable() {
        return asC2;
    }

    /**
     * The colDel <code>delete_dt</code> condition
     */
    public final Condition DiedDataCondition = DeleteDt.gt(EmptyValue.DATE_TIME_AS_MAX);
    public final Condition LiveDataCondition = DeleteDt.lt(EmptyValue.DATE_TIME_AS_MAX);
    
    @Override
    @NotNull
    public Condition getOnlyDied() {
        return DiedDataCondition;
    }
    
    @Override
    @NotNull
    public Condition getOnlyLive() {
        return LiveDataCondition;
    }
    
    @Override
    @NotNull
    public Map<Field<?>, ?> markDelete(JournalService.Journal commit) {
        Map<org.jooq.Field<?>, Object> map = new HashMap<>();
        map.put(DeleteDt, commit.getCommitDt());
        map.put(CommitId, commit.getCommitId());
        return map;
    }
}
