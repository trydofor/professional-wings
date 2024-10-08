/*
 * This file is generated by jOOQ.
 */
package pro.fessional.wings.faceless.app.database.autogen.tables;


import org.jetbrains.annotations.NotNull;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Function13;
import org.jooq.Name;
import org.jooq.Records;
import org.jooq.Row13;
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
import pro.fessional.wings.faceless.app.database.autogen.DefaultSchema;
import pro.fessional.wings.faceless.app.database.autogen.tables.records.TstNormalTableRecord;
import pro.fessional.wings.faceless.convention.EmptyValue;
import pro.fessional.wings.faceless.database.jooq.WingsJournalTable;
import pro.fessional.wings.faceless.service.journal.JournalService;
import pro.fessional.wings.faceless.service.lightid.LightIdAware;

import javax.annotation.processing.Generated;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;


/**
 * The table <code>wings_faceless.tst_normal_table</code>.
 */
@Generated(
    value = {
        "https://www.jooq.org",
        "jOOQ version:3.18.9",
        "schema version:2022060102"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class TstNormalTableTable extends TableImpl<TstNormalTableRecord> implements WingsJournalTable<TstNormalTableTable>, LightIdAware {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>tst_normal_table</code>
     */
    public static final TstNormalTableTable TstNormalTable = new TstNormalTableTable();
    public static final TstNormalTableTable asI4 = TstNormalTable.as(pro.fessional.wings.faceless.database.jooq.WingsJooqEnv.uniqueAlias());

    /**
     * The class holding records for this type
     */
    @Override
    public Class<TstNormalTableRecord> getRecordType() {
        return TstNormalTableRecord.class;
    }

    /**
     * The column <code>tst_normal_table.id</code>.
     */
    public final TableField<TstNormalTableRecord, Long> Id = createField(DSL.name("id"), SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>tst_normal_table.create_dt</code>.
     */
    public final TableField<TstNormalTableRecord, LocalDateTime> CreateDt = createField(DSL.name("create_dt"), SQLDataType.LOCALDATETIME(3).nullable(false).defaultValue(DSL.field(DSL.raw("CURRENT_TIMESTAMP(3)"), SQLDataType.LOCALDATETIME)), this, "");

    /**
     * The column <code>tst_normal_table.modify_dt</code>.
     */
    public final TableField<TstNormalTableRecord, LocalDateTime> ModifyDt = createField(DSL.name("modify_dt"), SQLDataType.LOCALDATETIME(3).nullable(false).defaultValue(DSL.inline("1000-01-01 00:00:00.000", SQLDataType.LOCALDATETIME)), this, "");

    /**
     * The column <code>tst_normal_table.delete_dt</code>.
     */
    public final TableField<TstNormalTableRecord, LocalDateTime> DeleteDt = createField(DSL.name("delete_dt"), SQLDataType.LOCALDATETIME(3).nullable(false).defaultValue(DSL.inline("1000-01-01 00:00:00.000", SQLDataType.LOCALDATETIME)), this, "");

    /**
     * The column <code>tst_normal_table.commit_id</code>.
     */
    public final TableField<TstNormalTableRecord, Long> CommitId = createField(DSL.name("commit_id"), SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>tst_normal_table.value_varchar</code>.
     */
    public final TableField<TstNormalTableRecord, String> ValueVarchar = createField(DSL.name("value_varchar"), SQLDataType.VARCHAR(256).nullable(false).defaultValue(DSL.inline("0", SQLDataType.VARCHAR)), this, "");

    /**
     * The column <code>tst_normal_table.value_decimal</code>.
     */
    public final TableField<TstNormalTableRecord, BigDecimal> ValueDecimal = createField(DSL.name("value_decimal"), SQLDataType.DECIMAL(10, 2).nullable(false).defaultValue(DSL.inline("0.00", SQLDataType.DECIMAL)), this, "");

    /**
     * The column <code>tst_normal_table.value_boolean</code>.
     */
    public final TableField<TstNormalTableRecord, Boolean> ValueBoolean = createField(DSL.name("value_boolean"), SQLDataType.BOOLEAN.nullable(false).defaultValue(DSL.inline("0", SQLDataType.BOOLEAN)), this, "");

    /**
     * The column <code>tst_normal_table.value_int</code>.
     */
    public final TableField<TstNormalTableRecord, Integer> ValueInt = createField(DSL.name("value_int"), SQLDataType.INTEGER.nullable(false).defaultValue(DSL.inline("0", SQLDataType.INTEGER)), this, "");

    /**
     * The column <code>tst_normal_table.value_long</code>.
     */
    public final TableField<TstNormalTableRecord, Long> ValueLong = createField(DSL.name("value_long"), SQLDataType.BIGINT.nullable(false).defaultValue(DSL.inline("0", SQLDataType.BIGINT)), this, "");

    /**
     * The column <code>tst_normal_table.value_date</code>.
     */
    public final TableField<TstNormalTableRecord, LocalDate> ValueDate = createField(DSL.name("value_date"), SQLDataType.LOCALDATE.nullable(false).defaultValue(DSL.inline("1000-01-01", SQLDataType.LOCALDATE)), this, "");

    /**
     * The column <code>tst_normal_table.value_time</code>.
     */
    public final TableField<TstNormalTableRecord, LocalTime> ValueTime = createField(DSL.name("value_time"), SQLDataType.LOCALTIME.nullable(false).defaultValue(DSL.inline("00:00:00", SQLDataType.LOCALTIME)), this, "");

    /**
     * The column <code>tst_normal_table.value_lang</code>.
     */
    public final TableField<TstNormalTableRecord, Integer> ValueLang = createField(DSL.name("value_lang"), SQLDataType.INTEGER.nullable(false).defaultValue(DSL.inline("1020111", SQLDataType.INTEGER)), this, "");

    private TstNormalTableTable(Name alias, Table<TstNormalTableRecord> aliased) {
        this(alias, aliased, null);
    }

    private TstNormalTableTable(Name alias, Table<TstNormalTableRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>tst_normal_table</code> table reference
     */
    public TstNormalTableTable(String alias) {
        this(DSL.name(alias), TstNormalTable);
    }

    /**
     * Create an aliased <code>tst_normal_table</code> table reference
     */
    public TstNormalTableTable(Name alias) {
        this(alias, TstNormalTable);
    }

    /**
     * Create a <code>tst_normal_table</code> table reference
     */
    public TstNormalTableTable() {
        this(DSL.name("tst_normal_table"), null);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : DefaultSchema.DEFAULT_SCHEMA;
    }

    @Override
    public UniqueKey<TstNormalTableRecord> getPrimaryKey() {
        return Internal.createUniqueKey(TstNormalTableTable.TstNormalTable, DSL.name("KEY_tst_normal_table_PRIMARY"), new TableField[] { TstNormalTableTable.TstNormalTable.Id }, true);
    }

    @Override
    public TstNormalTableTable as(String alias) {
        return new TstNormalTableTable(DSL.name(alias), this);
    }

    @Override
    public TstNormalTableTable as(Name alias) {
        return new TstNormalTableTable(alias, this);
    }

    @Override
    public TstNormalTableTable as(Table<?> alias) {
        return new TstNormalTableTable(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public TstNormalTableTable rename(String name) {
        return new TstNormalTableTable(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public TstNormalTableTable rename(Name name) {
        return new TstNormalTableTable(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public TstNormalTableTable rename(Table<?> name) {
        return new TstNormalTableTable(name.getQualifiedName(), null);
    }

    // -------------------------------------------------------------------------
    // Row13 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row13<Long, LocalDateTime, LocalDateTime, LocalDateTime, Long, String, BigDecimal, Boolean, Integer, Long, LocalDate, LocalTime, Integer> fieldsRow() {
        return (Row13) super.fieldsRow();
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Function)}.
     */
    public <U> SelectField<U> mapping(Function13<? super Long, ? super LocalDateTime, ? super LocalDateTime, ? super LocalDateTime, ? super Long, ? super String, ? super BigDecimal, ? super Boolean, ? super Integer, ? super Long, ? super LocalDate, ? super LocalTime, ? super Integer, ? extends U> from) {
        return convertFrom(Records.mapping(from));
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Class,
     * Function)}.
     */
    public <U> SelectField<U> mapping(Class<U> toType, Function13<? super Long, ? super LocalDateTime, ? super LocalDateTime, ? super LocalDateTime, ? super Long, ? super String, ? super BigDecimal, ? super Boolean, ? super Integer, ? super Long, ? super LocalDate, ? super LocalTime, ? super Integer, ? extends U> from) {
        return convertFrom(toType, Records.mapping(from));
    }

    /**
     * LightIdAware seqName
     */
    @Override
    @NotNull
    public String getSeqName() {
        return "tst_normal_table";
    }

    /**
     * alias asI4
     */
    @Override
    @NotNull
    public TstNormalTableTable getAliasTable() {
        return asI4;
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
