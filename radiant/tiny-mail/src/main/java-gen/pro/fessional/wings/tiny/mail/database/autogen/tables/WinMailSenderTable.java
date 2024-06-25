/*
 * This file is generated by jOOQ.
 */
package pro.fessional.wings.tiny.mail.database.autogen.tables;


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
import pro.fessional.wings.tiny.mail.database.autogen.DefaultSchemaTinyMail;
import pro.fessional.wings.tiny.mail.database.autogen.tables.records.WinMailSenderRecord;

import javax.annotation.processing.Generated;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;


/**
 * The table <code>wings.win_mail_sender</code>.
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
public class WinMailSenderTable extends TableImpl<WinMailSenderRecord> implements WingsJournalTable<WinMailSenderTable>, LightIdAware {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>win_mail_sender</code>
     */
    public static final WinMailSenderTable WinMailSender = new WinMailSenderTable();
    public static final WinMailSenderTable asW3 = WinMailSender.as(pro.fessional.wings.faceless.database.jooq.WingsJooqEnv.uniqueAlias());

    /**
     * The class holding records for this type
     */
    @Override
    public Class<WinMailSenderRecord> getRecordType() {
        return WinMailSenderRecord.class;
    }

    /**
     * The column <code>win_mail_sender.id</code>.
     */
    public final TableField<WinMailSenderRecord, Long> Id = createField(DSL.name("id"), SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>win_mail_sender.create_dt</code>.
     */
    public final TableField<WinMailSenderRecord, LocalDateTime> CreateDt = createField(DSL.name("create_dt"), SQLDataType.LOCALDATETIME(3).nullable(false).defaultValue(DSL.field(DSL.raw("CURRENT_TIMESTAMP(3)"), SQLDataType.LOCALDATETIME)), this, "");

    /**
     * The column <code>win_mail_sender.modify_dt</code>.
     */
    public final TableField<WinMailSenderRecord, LocalDateTime> ModifyDt = createField(DSL.name("modify_dt"), SQLDataType.LOCALDATETIME(3).nullable(false).defaultValue(DSL.inline("1000-01-01 00:00:00.000", SQLDataType.LOCALDATETIME)), this, "");

    /**
     * The column <code>win_mail_sender.delete_dt</code>.
     */
    public final TableField<WinMailSenderRecord, LocalDateTime> DeleteDt = createField(DSL.name("delete_dt"), SQLDataType.LOCALDATETIME(3).nullable(false).defaultValue(DSL.inline("1000-01-01 00:00:00.000", SQLDataType.LOCALDATETIME)), this, "");

    /**
     * The column <code>win_mail_sender.commit_id</code>.
     */
    public final TableField<WinMailSenderRecord, Long> CommitId = createField(DSL.name("commit_id"), SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>win_mail_sender.mail_apps</code>.
     */
    public final TableField<WinMailSenderRecord, String> MailApps = createField(DSL.name("mail_apps"), SQLDataType.VARCHAR(500).nullable(false).defaultValue(DSL.inline("", SQLDataType.VARCHAR)), this, "");

    /**
     * The column <code>win_mail_sender.mail_runs</code>.
     */
    public final TableField<WinMailSenderRecord, String> MailRuns = createField(DSL.name("mail_runs"), SQLDataType.VARCHAR(100).nullable(false).defaultValue(DSL.inline("", SQLDataType.VARCHAR)), this, "");

    /**
     * The column <code>win_mail_sender.mail_conf</code>.
     */
    public final TableField<WinMailSenderRecord, String> MailConf = createField(DSL.name("mail_conf"), SQLDataType.VARCHAR(100).nullable(false).defaultValue(DSL.inline("", SQLDataType.VARCHAR)), this, "");

    /**
     * The column <code>win_mail_sender.mail_from</code>.
     */
    public final TableField<WinMailSenderRecord, String> MailFrom = createField(DSL.name("mail_from"), SQLDataType.VARCHAR(200).nullable(false).defaultValue(DSL.inline("", SQLDataType.VARCHAR)), this, "");

    /**
     * The column <code>win_mail_sender.mail_to</code>.
     */
    public final TableField<WinMailSenderRecord, String> MailTo = createField(DSL.name("mail_to"), SQLDataType.VARCHAR(500).nullable(false).defaultValue(DSL.inline("", SQLDataType.VARCHAR)), this, "");

    /**
     * The column <code>win_mail_sender.mail_cc</code>.
     */
    public final TableField<WinMailSenderRecord, String> MailCc = createField(DSL.name("mail_cc"), SQLDataType.VARCHAR(500).nullable(false).defaultValue(DSL.inline("", SQLDataType.VARCHAR)), this, "");

    /**
     * The column <code>win_mail_sender.mail_bcc</code>.
     */
    public final TableField<WinMailSenderRecord, String> MailBcc = createField(DSL.name("mail_bcc"), SQLDataType.VARCHAR(500).nullable(false).defaultValue(DSL.inline("", SQLDataType.VARCHAR)), this, "");

    /**
     * The column <code>win_mail_sender.mail_reply</code>.
     */
    public final TableField<WinMailSenderRecord, String> MailReply = createField(DSL.name("mail_reply"), SQLDataType.VARCHAR(200).nullable(false).defaultValue(DSL.inline("", SQLDataType.VARCHAR)), this, "");

    /**
     * The column <code>win_mail_sender.mail_subj</code>.
     */
    public final TableField<WinMailSenderRecord, String> MailSubj = createField(DSL.name("mail_subj"), SQLDataType.VARCHAR(400).nullable(false).defaultValue(DSL.inline("", SQLDataType.VARCHAR)), this, "");

    /**
     * The column <code>win_mail_sender.mail_text</code>.
     */
    public final TableField<WinMailSenderRecord, String> MailText = createField(DSL.name("mail_text"), SQLDataType.CLOB, this, "");

    /**
     * The column <code>win_mail_sender.mail_html</code>.
     */
    public final TableField<WinMailSenderRecord, Boolean> MailHtml = createField(DSL.name("mail_html"), SQLDataType.BOOLEAN.nullable(false).defaultValue(DSL.inline("1", SQLDataType.BOOLEAN)), this, "");

    /**
     * The column <code>win_mail_sender.mail_file</code>.
     */
    public final TableField<WinMailSenderRecord, String> MailFile = createField(DSL.name("mail_file"), SQLDataType.VARCHAR(9000).nullable(false).defaultValue(DSL.inline("", SQLDataType.VARCHAR)), this, "");

    /**
     * The column <code>win_mail_sender.mail_mark</code>.
     */
    public final TableField<WinMailSenderRecord, String> MailMark = createField(DSL.name("mail_mark"), SQLDataType.VARCHAR(200).nullable(false).defaultValue(DSL.inline("", SQLDataType.VARCHAR)), this, "");

    /**
     * The column <code>win_mail_sender.mail_date</code>.
     */
    public final TableField<WinMailSenderRecord, LocalDateTime> MailDate = createField(DSL.name("mail_date"), SQLDataType.LOCALDATETIME(3).nullable(false).defaultValue(DSL.inline("1000-01-01 00:00:00.000", SQLDataType.LOCALDATETIME)), this, "");

    /**
     * The column <code>win_mail_sender.last_send</code>.
     */
    public final TableField<WinMailSenderRecord, LocalDateTime> LastSend = createField(DSL.name("last_send"), SQLDataType.LOCALDATETIME(3).nullable(false).defaultValue(DSL.inline("1000-01-01 00:00:00.000", SQLDataType.LOCALDATETIME)), this, "");

    /**
     * The column <code>win_mail_sender.last_fail</code>.
     */
    public final TableField<WinMailSenderRecord, String> LastFail = createField(DSL.name("last_fail"), SQLDataType.CLOB, this, "");

    /**
     * The column <code>win_mail_sender.last_done</code>.
     */
    public final TableField<WinMailSenderRecord, LocalDateTime> LastDone = createField(DSL.name("last_done"), SQLDataType.LOCALDATETIME(3).nullable(false).defaultValue(DSL.inline("1000-01-01 00:00:00.000", SQLDataType.LOCALDATETIME)), this, "");

    /**
     * The column <code>win_mail_sender.last_cost</code>.
     */
    public final TableField<WinMailSenderRecord, Integer> LastCost = createField(DSL.name("last_cost"), SQLDataType.INTEGER.nullable(false).defaultValue(DSL.inline("0", SQLDataType.INTEGER)), this, "");

    /**
     * The column <code>win_mail_sender.next_send</code>.
     */
    public final TableField<WinMailSenderRecord, LocalDateTime> NextSend = createField(DSL.name("next_send"), SQLDataType.LOCALDATETIME(3).nullable(false).defaultValue(DSL.inline("1000-01-01 00:00:00.000", SQLDataType.LOCALDATETIME)), this, "");

    /**
     * The column <code>win_mail_sender.next_lock</code>.
     */
    public final TableField<WinMailSenderRecord, Integer> NextLock = createField(DSL.name("next_lock"), SQLDataType.INTEGER.nullable(false).defaultValue(DSL.inline("0", SQLDataType.INTEGER)), this, "");

    /**
     * The column <code>win_mail_sender.sum_send</code>.
     */
    public final TableField<WinMailSenderRecord, Integer> SumSend = createField(DSL.name("sum_send"), SQLDataType.INTEGER.nullable(false).defaultValue(DSL.inline("0", SQLDataType.INTEGER)), this, "");

    /**
     * The column <code>win_mail_sender.sum_fail</code>.
     */
    public final TableField<WinMailSenderRecord, Integer> SumFail = createField(DSL.name("sum_fail"), SQLDataType.INTEGER.nullable(false).defaultValue(DSL.inline("0", SQLDataType.INTEGER)), this, "");

    /**
     * The column <code>win_mail_sender.sum_done</code>.
     */
    public final TableField<WinMailSenderRecord, Integer> SumDone = createField(DSL.name("sum_done"), SQLDataType.INTEGER.nullable(false).defaultValue(DSL.inline("0", SQLDataType.INTEGER)), this, "");

    /**
     * The column <code>win_mail_sender.max_fail</code>.
     */
    public final TableField<WinMailSenderRecord, Integer> MaxFail = createField(DSL.name("max_fail"), SQLDataType.INTEGER.nullable(false).defaultValue(DSL.inline("0", SQLDataType.INTEGER)), this, "");

    /**
     * The column <code>win_mail_sender.max_done</code>.
     */
    public final TableField<WinMailSenderRecord, Integer> MaxDone = createField(DSL.name("max_done"), SQLDataType.INTEGER.nullable(false).defaultValue(DSL.inline("0", SQLDataType.INTEGER)), this, "");

    /**
     * The column <code>win_mail_sender.ref_type</code>.
     */
    public final TableField<WinMailSenderRecord, Integer> RefType = createField(DSL.name("ref_type"), SQLDataType.INTEGER.nullable(false).defaultValue(DSL.inline("0", SQLDataType.INTEGER)), this, "");

    /**
     * The column <code>win_mail_sender.ref_key1</code>.
     */
    public final TableField<WinMailSenderRecord, Long> RefKey1 = createField(DSL.name("ref_key1"), SQLDataType.BIGINT.nullable(false).defaultValue(DSL.inline("0", SQLDataType.BIGINT)), this, "");

    /**
     * The column <code>win_mail_sender.ref_key2</code>.
     */
    public final TableField<WinMailSenderRecord, String> RefKey2 = createField(DSL.name("ref_key2"), SQLDataType.VARCHAR(500).nullable(false).defaultValue(DSL.inline("", SQLDataType.VARCHAR)), this, "");

    private WinMailSenderTable(Name alias, Table<WinMailSenderRecord> aliased) {
        this(alias, aliased, null);
    }

    private WinMailSenderTable(Name alias, Table<WinMailSenderRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>win_mail_sender</code> table reference
     */
    public WinMailSenderTable(String alias) {
        this(DSL.name(alias), WinMailSender);
    }

    /**
     * Create an aliased <code>win_mail_sender</code> table reference
     */
    public WinMailSenderTable(Name alias) {
        this(alias, WinMailSender);
    }

    /**
     * Create a <code>win_mail_sender</code> table reference
     */
    public WinMailSenderTable() {
        this(DSL.name("win_mail_sender"), null);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : DefaultSchemaTinyMail.DEFAULT_SCHEMA;
    }

    @Override
    public UniqueKey<WinMailSenderRecord> getPrimaryKey() {
        return Internal.createUniqueKey(WinMailSenderTable.WinMailSender, DSL.name("KEY_win_mail_sender_PRIMARY"), new TableField[] { WinMailSenderTable.WinMailSender.Id }, true);
    }

    @Override
    public WinMailSenderTable as(String alias) {
        return new WinMailSenderTable(DSL.name(alias), this);
    }

    @Override
    public WinMailSenderTable as(Name alias) {
        return new WinMailSenderTable(alias, this);
    }

    @Override
    public WinMailSenderTable as(Table<?> alias) {
        return new WinMailSenderTable(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public WinMailSenderTable rename(String name) {
        return new WinMailSenderTable(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public WinMailSenderTable rename(Name name) {
        return new WinMailSenderTable(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public WinMailSenderTable rename(Table<?> name) {
        return new WinMailSenderTable(name.getQualifiedName(), null);
    }

    /**
     * LightIdAware seqName
     */
    @Override
    @NotNull
    public String getSeqName() {
        return "win_mail_sender";
    }

    /**
     * alias asW3
     */
    @Override
    @NotNull
    public WinMailSenderTable getAliasTable() {
        return asW3;
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
