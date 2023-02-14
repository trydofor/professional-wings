/*
 * This file is generated by jOOQ.
 */
package pro.fessional.wings.tiny.task.database.autogen.tables;


import org.jetbrains.annotations.NotNull;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Name;
import org.jooq.Schema;
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
import pro.fessional.wings.tiny.task.database.autogen.DefaultSchemaTinyTask;
import pro.fessional.wings.tiny.task.database.autogen.tables.records.WinTaskDefineRecord;

import javax.annotation.processing.Generated;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;


/**
 * The table <code>wings.win_task_define</code>.
 */
@Generated(
    value = {
        "https://www.jooq.org",
        "jOOQ version:3.17.7",
        "schema version:2020102701"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class WinTaskDefineTable extends TableImpl<WinTaskDefineRecord> implements WingsJournalTable<WinTaskDefineTable>, LightIdAware {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>win_task_define</code>
     */
    public static final WinTaskDefineTable WinTaskDefine = new WinTaskDefineTable();
    public static final WinTaskDefineTable asQ3 = WinTaskDefine.as(pro.fessional.wings.faceless.database.jooq.WingsJooqEnv.uniqueAlias());

    /**
     * The class holding records for this type
     */
    @Override
    public Class<WinTaskDefineRecord> getRecordType() {
        return WinTaskDefineRecord.class;
    }

    /**
     * The column <code>win_task_define.id</code>.
     */
    public final TableField<WinTaskDefineRecord, Long> Id = createField(DSL.name("id"), SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>win_task_define.create_dt</code>.
     */
    public final TableField<WinTaskDefineRecord, LocalDateTime> CreateDt = createField(DSL.name("create_dt"), SQLDataType.LOCALDATETIME(3).nullable(false).defaultValue(DSL.field("CURRENT_TIMESTAMP(3)", SQLDataType.LOCALDATETIME)), this, "");

    /**
     * The column <code>win_task_define.modify_dt</code>.
     */
    public final TableField<WinTaskDefineRecord, LocalDateTime> ModifyDt = createField(DSL.name("modify_dt"), SQLDataType.LOCALDATETIME(3).nullable(false).defaultValue(DSL.inline("1000-01-01 00:00:00.000", SQLDataType.LOCALDATETIME)), this, "");

    /**
     * The column <code>win_task_define.delete_dt</code>.
     */
    public final TableField<WinTaskDefineRecord, LocalDateTime> DeleteDt = createField(DSL.name("delete_dt"), SQLDataType.LOCALDATETIME(3).nullable(false).defaultValue(DSL.inline("1000-01-01 00:00:00.000", SQLDataType.LOCALDATETIME)), this, "");

    /**
     * The column <code>win_task_define.commit_id</code>.
     */
    public final TableField<WinTaskDefineRecord, Long> CommitId = createField(DSL.name("commit_id"), SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>win_task_define.propkey</code>.
     */
    public final TableField<WinTaskDefineRecord, String> Propkey = createField(DSL.name("propkey"), SQLDataType.VARCHAR(200).nullable(false).defaultValue(DSL.inline("", SQLDataType.VARCHAR)), this, "");

    /**
     * The column <code>win_task_define.enabled</code>.
     */
    public final TableField<WinTaskDefineRecord, Boolean> Enabled = createField(DSL.name("enabled"), SQLDataType.BOOLEAN.nullable(false).defaultValue(DSL.inline("1", SQLDataType.BOOLEAN)), this, "");

    /**
     * The column <code>win_task_define.autorun</code>.
     */
    public final TableField<WinTaskDefineRecord, Boolean> Autorun = createField(DSL.name("autorun"), SQLDataType.BOOLEAN.nullable(false).defaultValue(DSL.inline("1", SQLDataType.BOOLEAN)), this, "");

    /**
     * The column <code>win_task_define.version</code>.
     */
    public final TableField<WinTaskDefineRecord, Integer> Version = createField(DSL.name("version"), SQLDataType.INTEGER.nullable(false).defaultValue(DSL.inline("0", SQLDataType.INTEGER)), this, "");

    /**
     * The column <code>win_task_define.tasker_bean</code>.
     */
    public final TableField<WinTaskDefineRecord, String> TaskerBean = createField(DSL.name("tasker_bean"), SQLDataType.VARCHAR(300).nullable(false).defaultValue(DSL.inline("", SQLDataType.VARCHAR)), this, "");

    /**
     * The column <code>win_task_define.tasker_para</code>.
     */
    public final TableField<WinTaskDefineRecord, String> TaskerPara = createField(DSL.name("tasker_para"), SQLDataType.CLOB, this, "");

    /**
     * The column <code>win_task_define.tasker_name</code>.
     */
    public final TableField<WinTaskDefineRecord, String> TaskerName = createField(DSL.name("tasker_name"), SQLDataType.VARCHAR(200).nullable(false).defaultValue(DSL.inline("", SQLDataType.VARCHAR)), this, "");

    /**
     * The column <code>win_task_define.tasker_fast</code>.
     */
    public final TableField<WinTaskDefineRecord, Boolean> TaskerFast = createField(DSL.name("tasker_fast"), SQLDataType.BOOLEAN.nullable(false).defaultValue(DSL.inline("1", SQLDataType.BOOLEAN)), this, "");

    /**
     * The column <code>win_task_define.tasker_apps</code>.
     */
    public final TableField<WinTaskDefineRecord, String> TaskerApps = createField(DSL.name("tasker_apps"), SQLDataType.VARCHAR(500).nullable(false).defaultValue(DSL.inline("", SQLDataType.VARCHAR)), this, "");

    /**
     * The column <code>win_task_define.tasker_runs</code>.
     */
    public final TableField<WinTaskDefineRecord, String> TaskerRuns = createField(DSL.name("tasker_runs"), SQLDataType.VARCHAR(100).nullable(false).defaultValue(DSL.inline("", SQLDataType.VARCHAR)), this, "");

    /**
     * The column <code>win_task_define.notice_bean</code>.
     */
    public final TableField<WinTaskDefineRecord, String> NoticeBean = createField(DSL.name("notice_bean"), SQLDataType.VARCHAR(200).nullable(false).defaultValue(DSL.inline("", SQLDataType.VARCHAR)), this, "");

    /**
     * The column <code>win_task_define.notice_when</code>.
     */
    public final TableField<WinTaskDefineRecord, String> NoticeWhen = createField(DSL.name("notice_when"), SQLDataType.VARCHAR(100).nullable(false).defaultValue(DSL.inline("fail", SQLDataType.VARCHAR)), this, "");

    /**
     * The column <code>win_task_define.notice_conf</code>.
     */
    public final TableField<WinTaskDefineRecord, String> NoticeConf = createField(DSL.name("notice_conf"), SQLDataType.VARCHAR(200).nullable(false).defaultValue(DSL.inline("", SQLDataType.VARCHAR)), this, "");

    /**
     * The column <code>win_task_define.timing_zone</code>.
     */
    public final TableField<WinTaskDefineRecord, String> TimingZone = createField(DSL.name("timing_zone"), SQLDataType.VARCHAR(100).nullable(false).defaultValue(DSL.inline("", SQLDataType.VARCHAR)), this, "");

    /**
     * The column <code>win_task_define.timing_type</code>.
     */
    public final TableField<WinTaskDefineRecord, String> TimingType = createField(DSL.name("timing_type"), SQLDataType.VARCHAR(100).nullable(false).defaultValue(DSL.inline("cron", SQLDataType.VARCHAR)), this, "");

    /**
     * The column <code>win_task_define.timing_cron</code>.
     */
    public final TableField<WinTaskDefineRecord, String> TimingCron = createField(DSL.name("timing_cron"), SQLDataType.VARCHAR(100).nullable(false).defaultValue(DSL.inline("", SQLDataType.VARCHAR)), this, "");

    /**
     * The column <code>win_task_define.timing_idle</code>.
     */
    public final TableField<WinTaskDefineRecord, Integer> TimingIdle = createField(DSL.name("timing_idle"), SQLDataType.INTEGER.nullable(false).defaultValue(DSL.inline("0", SQLDataType.INTEGER)), this, "");

    /**
     * The column <code>win_task_define.timing_rate</code>.
     */
    public final TableField<WinTaskDefineRecord, Integer> TimingRate = createField(DSL.name("timing_rate"), SQLDataType.INTEGER.nullable(false).defaultValue(DSL.inline("0", SQLDataType.INTEGER)), this, "");

    /**
     * The column <code>win_task_define.timing_miss</code>.
     */
    public final TableField<WinTaskDefineRecord, Integer> TimingMiss = createField(DSL.name("timing_miss"), SQLDataType.INTEGER.nullable(false).defaultValue(DSL.inline("0", SQLDataType.INTEGER)), this, "");

    /**
     * The column <code>win_task_define.timing_beat</code>.
     */
    public final TableField<WinTaskDefineRecord, Integer> TimingBeat = createField(DSL.name("timing_beat"), SQLDataType.INTEGER.nullable(false).defaultValue(DSL.inline("0", SQLDataType.INTEGER)), this, "");

    /**
     * The column <code>win_task_define.during_from</code>.
     */
    public final TableField<WinTaskDefineRecord, String> DuringFrom = createField(DSL.name("during_from"), SQLDataType.VARCHAR(20).nullable(false).defaultValue(DSL.inline("", SQLDataType.VARCHAR)), this, "");

    /**
     * The column <code>win_task_define.during_stop</code>.
     */
    public final TableField<WinTaskDefineRecord, String> DuringStop = createField(DSL.name("during_stop"), SQLDataType.VARCHAR(20).nullable(false).defaultValue(DSL.inline("", SQLDataType.VARCHAR)), this, "");

    /**
     * The column <code>win_task_define.during_exec</code>.
     */
    public final TableField<WinTaskDefineRecord, Integer> DuringExec = createField(DSL.name("during_exec"), SQLDataType.INTEGER.nullable(false).defaultValue(DSL.inline("0", SQLDataType.INTEGER)), this, "");

    /**
     * The column <code>win_task_define.during_fail</code>.
     */
    public final TableField<WinTaskDefineRecord, Integer> DuringFail = createField(DSL.name("during_fail"), SQLDataType.INTEGER.nullable(false).defaultValue(DSL.inline("0", SQLDataType.INTEGER)), this, "");

    /**
     * The column <code>win_task_define.during_done</code>.
     */
    public final TableField<WinTaskDefineRecord, Integer> DuringDone = createField(DSL.name("during_done"), SQLDataType.INTEGER.nullable(false).defaultValue(DSL.inline("0", SQLDataType.INTEGER)), this, "");

    /**
     * The column <code>win_task_define.during_boot</code>.
     */
    public final TableField<WinTaskDefineRecord, Integer> DuringBoot = createField(DSL.name("during_boot"), SQLDataType.INTEGER.nullable(false).defaultValue(DSL.inline("0", SQLDataType.INTEGER)), this, "");

    /**
     * The column <code>win_task_define.result_keep</code>.
     */
    public final TableField<WinTaskDefineRecord, Integer> ResultKeep = createField(DSL.name("result_keep"), SQLDataType.INTEGER.nullable(false).defaultValue(DSL.inline("60", SQLDataType.INTEGER)), this, "");

    /**
     * The column <code>win_task_define.last_exec</code>.
     */
    public final TableField<WinTaskDefineRecord, LocalDateTime> LastExec = createField(DSL.name("last_exec"), SQLDataType.LOCALDATETIME(3).nullable(false).defaultValue(DSL.inline("1000-01-01 00:00:00.000", SQLDataType.LOCALDATETIME)), this, "");

    /**
     * The column <code>win_task_define.last_fail</code>.
     */
    public final TableField<WinTaskDefineRecord, LocalDateTime> LastFail = createField(DSL.name("last_fail"), SQLDataType.LOCALDATETIME(3).nullable(false).defaultValue(DSL.inline("1000-01-01 00:00:00.000", SQLDataType.LOCALDATETIME)), this, "");

    /**
     * The column <code>win_task_define.last_done</code>.
     */
    public final TableField<WinTaskDefineRecord, LocalDateTime> LastDone = createField(DSL.name("last_done"), SQLDataType.LOCALDATETIME(3).nullable(false).defaultValue(DSL.inline("1000-01-01 00:00:00.000", SQLDataType.LOCALDATETIME)), this, "");

    /**
     * The column <code>win_task_define.next_exec</code>.
     */
    public final TableField<WinTaskDefineRecord, LocalDateTime> NextExec = createField(DSL.name("next_exec"), SQLDataType.LOCALDATETIME(3).nullable(false).defaultValue(DSL.inline("1000-01-01 00:00:00.000", SQLDataType.LOCALDATETIME)), this, "");

    /**
     * The column <code>win_task_define.next_lock</code>.
     */
    public final TableField<WinTaskDefineRecord, Integer> NextLock = createField(DSL.name("next_lock"), SQLDataType.INTEGER.nullable(false).defaultValue(DSL.inline("0", SQLDataType.INTEGER)), this, "");

    /**
     * The column <code>win_task_define.dur_fail</code>.
     */
    public final TableField<WinTaskDefineRecord, Integer> DurFail = createField(DSL.name("dur_fail"), SQLDataType.INTEGER.nullable(false).defaultValue(DSL.inline("0", SQLDataType.INTEGER)), this, "");

    /**
     * The column <code>win_task_define.sum_exec</code>.
     */
    public final TableField<WinTaskDefineRecord, Integer> SumExec = createField(DSL.name("sum_exec"), SQLDataType.INTEGER.nullable(false).defaultValue(DSL.inline("0", SQLDataType.INTEGER)), this, "");

    /**
     * The column <code>win_task_define.sum_fail</code>.
     */
    public final TableField<WinTaskDefineRecord, Integer> SumFail = createField(DSL.name("sum_fail"), SQLDataType.INTEGER.nullable(false).defaultValue(DSL.inline("0", SQLDataType.INTEGER)), this, "");

    /**
     * The column <code>win_task_define.sum_done</code>.
     */
    public final TableField<WinTaskDefineRecord, Integer> SumDone = createField(DSL.name("sum_done"), SQLDataType.INTEGER.nullable(false).defaultValue(DSL.inline("0", SQLDataType.INTEGER)), this, "");

    private WinTaskDefineTable(Name alias, Table<WinTaskDefineRecord> aliased) {
        this(alias, aliased, null);
    }

    private WinTaskDefineTable(Name alias, Table<WinTaskDefineRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>win_task_define</code> table reference
     */
    public WinTaskDefineTable(String alias) {
        this(DSL.name(alias), WinTaskDefine);
    }

    /**
     * Create an aliased <code>win_task_define</code> table reference
     */
    public WinTaskDefineTable(Name alias) {
        this(alias, WinTaskDefine);
    }

    /**
     * Create a <code>win_task_define</code> table reference
     */
    public WinTaskDefineTable() {
        this(DSL.name("win_task_define"), null);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : DefaultSchemaTinyTask.DEFAULT_SCHEMA;
    }

    @Override
    public UniqueKey<WinTaskDefineRecord> getPrimaryKey() {
        return Internal.createUniqueKey(WinTaskDefineTable.WinTaskDefine, DSL.name("KEY_win_task_define_PRIMARY"), new TableField[] { WinTaskDefineTable.WinTaskDefine.Id }, true);
    }

    @Override
    public WinTaskDefineTable as(String alias) {
        return new WinTaskDefineTable(DSL.name(alias), this);
    }

    @Override
    public WinTaskDefineTable as(Name alias) {
        return new WinTaskDefineTable(alias, this);
    }

    @Override
    public WinTaskDefineTable as(Table<?> alias) {
        return new WinTaskDefineTable(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public WinTaskDefineTable rename(String name) {
        return new WinTaskDefineTable(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public WinTaskDefineTable rename(Name name) {
        return new WinTaskDefineTable(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public WinTaskDefineTable rename(Table<?> name) {
        return new WinTaskDefineTable(name.getQualifiedName(), null);
    }

    /**
     * LightIdAware seqName
     */
    @Override
    @NotNull
    public String getSeqName() {
        return "win_task_define";
    }


    /**
     * alias asQ3
     */
    @Override
    @NotNull
    public WinTaskDefineTable getAliasTable() {
        return asQ3;
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
