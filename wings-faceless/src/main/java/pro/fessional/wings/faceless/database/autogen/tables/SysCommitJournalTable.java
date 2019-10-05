/*
 * This file is generated by jOOQ.
 */
package pro.fessional.wings.faceless.database.autogen.tables;


import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Name;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.Internal;
import org.jooq.impl.TableImpl;

import pro.fessional.wings.faceless.database.autogen.DefaultSchema;
import pro.fessional.wings.faceless.database.autogen.tables.records.SysCommitJournalRecord;
import pro.fessional.wings.faceless.service.lightid.LightIdAware;


/**
 * The table <code>wings_0.sys_commit_journal</code>.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.11",
        "schema version:2019052001"
    },
    date = "2019-09-09T06:19:42.220Z",
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class SysCommitJournalTable extends TableImpl<SysCommitJournalRecord> implements LightIdAware {

    private static final long serialVersionUID = -1070863297;

    /**
     * The reference instance of <code>sys_commit_journal</code>
     */
    public static final SysCommitJournalTable SysCommitJournal = new SysCommitJournalTable();
    public static final SysCommitJournalTable asN1 = SysCommitJournal.as("n1");

    /**
     * The class holding records for this type
     */
    @Override
    public Class<SysCommitJournalRecord> getRecordType() {
        return SysCommitJournalRecord.class;
    }

    /**
     * The column <code>sys_commit_journal.id</code>.
     */
    public final TableField<SysCommitJournalRecord, Long> Id = createField("id", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "主键");

    /**
     * The column <code>sys_commit_journal.create_dt</code>.
     */
    public final TableField<SysCommitJournalRecord, LocalDateTime> CreateDt = createField("create_dt", org.jooq.impl.SQLDataType.LOCALDATETIME.nullable(false).defaultValue(org.jooq.impl.DSL.field("CURRENT_TIMESTAMP", org.jooq.impl.SQLDataType.LOCALDATETIME)), this, "创建日时");

    /**
     * The column <code>sys_commit_journal.event_name</code>.
     */
    public final TableField<SysCommitJournalRecord, String> EventName = createField("event_name", org.jooq.impl.SQLDataType.VARCHAR(200).nullable(false), this, "事件名称");

    /**
     * The column <code>sys_commit_journal.target_key</code>.
     */
    public final TableField<SysCommitJournalRecord, String> TargetKey = createField("target_key", org.jooq.impl.SQLDataType.VARCHAR(200).nullable(false).defaultValue(org.jooq.impl.DSL.inline("", org.jooq.impl.SQLDataType.VARCHAR)), this, "目标数据特征");

    /**
     * The column <code>sys_commit_journal.login_info</code>.
     */
    public final TableField<SysCommitJournalRecord, String> LoginInfo = createField("login_info", org.jooq.impl.SQLDataType.CLOB, this, "登陆信息，用户，终端等");

    /**
     * The column <code>sys_commit_journal.other_info</code>.
     */
    public final TableField<SysCommitJournalRecord, String> OtherInfo = createField("other_info", org.jooq.impl.SQLDataType.CLOB, this, "其他信息，业务侧自定义");

    /**
     * Create a <code>sys_commit_journal</code> table reference
     */
    public SysCommitJournalTable() {
        this(DSL.name("sys_commit_journal"), null);
    }

    /**
     * Create an aliased <code>sys_commit_journal</code> table reference
     */
    public SysCommitJournalTable(String alias) {
        this(DSL.name(alias), SysCommitJournal);
    }

    /**
     * Create an aliased <code>sys_commit_journal</code> table reference
     */
    public SysCommitJournalTable(Name alias) {
        this(alias, SysCommitJournal);
    }

    private SysCommitJournalTable(Name alias, Table<SysCommitJournalRecord> aliased) {
        this(alias, aliased, null);
    }

    private SysCommitJournalTable(Name alias, Table<SysCommitJournalRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("变更日志"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Schema getSchema() {
        return DefaultSchema.DEFAULT_SCHEMA;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<SysCommitJournalRecord> getPrimaryKey() {
        return Internal.createUniqueKey(pro.fessional.wings.faceless.database.autogen.tables.SysCommitJournalTable.SysCommitJournal, "KEY_sys_commit_journal_PRIMARY", pro.fessional.wings.faceless.database.autogen.tables.SysCommitJournalTable.SysCommitJournal.Id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<SysCommitJournalRecord>> getKeys() {
        return Arrays.<UniqueKey<SysCommitJournalRecord>>asList(
              Internal.createUniqueKey(pro.fessional.wings.faceless.database.autogen.tables.SysCommitJournalTable.SysCommitJournal, "KEY_sys_commit_journal_PRIMARY", pro.fessional.wings.faceless.database.autogen.tables.SysCommitJournalTable.SysCommitJournal.Id)
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SysCommitJournalTable as(String alias) {
        return new SysCommitJournalTable(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SysCommitJournalTable as(Name alias) {
        return new SysCommitJournalTable(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public SysCommitJournalTable rename(String name) {
        return new SysCommitJournalTable(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public SysCommitJournalTable rename(Name name) {
        return new SysCommitJournalTable(name, null);
    }
}