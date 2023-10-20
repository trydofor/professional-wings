/*
 * This file is generated by jOOQ.
 */
package pro.fessional.wings.faceless.database.autogen.tables;


import org.jetbrains.annotations.NotNull;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Function8;
import org.jooq.Name;
import org.jooq.Records;
import org.jooq.Row8;
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
import pro.fessional.wings.faceless.database.autogen.DefaultSchema;
import pro.fessional.wings.faceless.database.autogen.tables.records.TstShardingRecord;
import pro.fessional.wings.faceless.database.jooq.WingsJournalTable;
import pro.fessional.wings.faceless.database.jooq.converter.JooqConsEnumConverter;
import pro.fessional.wings.faceless.enums.autogen.StandardLanguage;
import pro.fessional.wings.faceless.service.journal.JournalService;
import pro.fessional.wings.faceless.service.lightid.LightIdAware;

import javax.annotation.processing.Generated;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;


/**
 * The table <code>wings.tst_sharding</code>.
 */
@Generated(
    value = {
        "https://www.jooq.org",
        "jOOQ version:3.17.14",
        "schema version:2020102501"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class TstShardingTable extends TableImpl<TstShardingRecord> implements WingsJournalTable<TstShardingTable>, LightIdAware {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>tst_sharding</code>
     */
    public static final TstShardingTable TstSharding = new TstShardingTable();
    public static final TstShardingTable asP1 = TstSharding.as(pro.fessional.wings.faceless.database.jooq.WingsJooqEnv.uniqueAlias());

    /**
     * The class holding records for this type
     */
    @Override
    public Class<TstShardingRecord> getRecordType() {
        return TstShardingRecord.class;
    }

    /**
     * The column <code>tst_sharding.id</code>.
     */
    public final TableField<TstShardingRecord, Long> Id = createField(DSL.name("id"), SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>tst_sharding.create_dt</code>.
     */
    public final TableField<TstShardingRecord, LocalDateTime> CreateDt = createField(DSL.name("create_dt"), SQLDataType.LOCALDATETIME(3).nullable(false).defaultValue(DSL.field("CURRENT_TIMESTAMP(3)", SQLDataType.LOCALDATETIME)), this, "");

    /**
     * The column <code>tst_sharding.modify_dt</code>.
     */
    public final TableField<TstShardingRecord, LocalDateTime> ModifyDt = createField(DSL.name("modify_dt"), SQLDataType.LOCALDATETIME(3).nullable(false).defaultValue(DSL.inline("1000-01-01 00:00:00.000", SQLDataType.LOCALDATETIME)), this, "");

    /**
     * The column <code>tst_sharding.delete_dt</code>.
     */
    public final TableField<TstShardingRecord, LocalDateTime> DeleteDt = createField(DSL.name("delete_dt"), SQLDataType.LOCALDATETIME(3).nullable(false).defaultValue(DSL.inline("1000-01-01 00:00:00.000", SQLDataType.LOCALDATETIME)), this, "");

    /**
     * The column <code>tst_sharding.commit_id</code>.
     */
    public final TableField<TstShardingRecord, Long> CommitId = createField(DSL.name("commit_id"), SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>tst_sharding.login_info</code>.
     */
    public final TableField<TstShardingRecord, String> LoginInfo = createField(DSL.name("login_info"), SQLDataType.CLOB, this, "");

    /**
     * The column <code>tst_sharding.other_info</code>.
     */
    public final TableField<TstShardingRecord, String> OtherInfo = createField(DSL.name("other_info"), SQLDataType.CLOB, this, "");

    /**
     * The column <code>tst_sharding.language</code>.
     */
    public final TableField<TstShardingRecord, StandardLanguage> Language = createField(DSL.name("language"), SQLDataType.INTEGER.nullable(false).defaultValue(DSL.inline("1020111", SQLDataType.INTEGER)), this, "", new JooqConsEnumConverter(pro.fessional.wings.faceless.enums.autogen.StandardLanguage.class));

    private TstShardingTable(Name alias, Table<TstShardingRecord> aliased) {
        this(alias, aliased, null);
    }

    private TstShardingTable(Name alias, Table<TstShardingRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>tst_sharding</code> table reference
     */
    public TstShardingTable(String alias) {
        this(DSL.name(alias), TstSharding);
    }

    /**
     * Create an aliased <code>tst_sharding</code> table reference
     */
    public TstShardingTable(Name alias) {
        this(alias, TstSharding);
    }

    /**
     * Create a <code>tst_sharding</code> table reference
     */
    public TstShardingTable() {
        this(DSL.name("tst_sharding"), null);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : DefaultSchema.DEFAULT_SCHEMA;
    }

    @Override
    public UniqueKey<TstShardingRecord> getPrimaryKey() {
        return Internal.createUniqueKey(TstShardingTable.TstSharding, DSL.name("KEY_tst_sharding_PRIMARY"), new TableField[] { TstShardingTable.TstSharding.Id }, true);
    }

    @Override
    public TstShardingTable as(String alias) {
        return new TstShardingTable(DSL.name(alias), this);
    }

    @Override
    public TstShardingTable as(Name alias) {
        return new TstShardingTable(alias, this);
    }

    @Override
    public TstShardingTable as(Table<?> alias) {
        return new TstShardingTable(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public TstShardingTable rename(String name) {
        return new TstShardingTable(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public TstShardingTable rename(Name name) {
        return new TstShardingTable(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public TstShardingTable rename(Table<?> name) {
        return new TstShardingTable(name.getQualifiedName(), null);
    }

    // -------------------------------------------------------------------------
    // Row8 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row8<Long, LocalDateTime, LocalDateTime, LocalDateTime, Long, String, String, StandardLanguage> fieldsRow() {
        return (Row8) super.fieldsRow();
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Function)}.
     */
    public <U> SelectField<U> mapping(Function8<? super Long, ? super LocalDateTime, ? super LocalDateTime, ? super LocalDateTime, ? super Long, ? super String, ? super String, ? super StandardLanguage, ? extends U> from) {
        return convertFrom(Records.mapping(from));
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Class,
     * Function)}.
     */
    public <U> SelectField<U> mapping(Class<U> toType, Function8<? super Long, ? super LocalDateTime, ? super LocalDateTime, ? super LocalDateTime, ? super Long, ? super String, ? super String, ? super StandardLanguage, ? extends U> from) {
        return convertFrom(toType, Records.mapping(from));
    }

    /**
     * LightIdAware seqName
     */
    @Override
    @NotNull
    public String getSeqName() {
        return "tst_sharding";
    }
    

    /**
     * alias asP1
     */
    @Override
    @NotNull
    public TstShardingTable getAliasTable() {
        return asP1;
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
