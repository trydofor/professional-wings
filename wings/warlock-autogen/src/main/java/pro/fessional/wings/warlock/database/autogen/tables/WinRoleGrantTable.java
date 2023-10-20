/*
 * This file is generated by jOOQ.
 */
package pro.fessional.wings.warlock.database.autogen.tables;


import org.jetbrains.annotations.NotNull;
import org.jooq.Field;
import org.jooq.Function5;
import org.jooq.Name;
import org.jooq.Records;
import org.jooq.Row5;
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
import pro.fessional.wings.faceless.database.jooq.WingsJournalTable;
import pro.fessional.wings.faceless.database.jooq.converter.JooqConsEnumConverter;
import pro.fessional.wings.warlock.database.autogen.DefaultSchemaWarlock;
import pro.fessional.wings.warlock.database.autogen.tables.records.WinRoleGrantRecord;

import javax.annotation.processing.Generated;
import java.time.LocalDateTime;
import java.util.function.Function;


/**
 * The table <code>wings.win_role_grant</code>.
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
public class WinRoleGrantTable extends TableImpl<WinRoleGrantRecord> implements WingsJournalTable<WinRoleGrantTable> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>win_role_grant</code>
     */
    public static final WinRoleGrantTable WinRoleGrant = new WinRoleGrantTable();
    public static final WinRoleGrantTable asE2 = WinRoleGrant.as(pro.fessional.wings.faceless.database.jooq.WingsJooqEnv.uniqueAlias());

    /**
     * The class holding records for this type
     */
    @Override
    public Class<WinRoleGrantRecord> getRecordType() {
        return WinRoleGrantRecord.class;
    }

    /**
     * The column <code>win_role_grant.refer_role</code>.
     */
    public final TableField<WinRoleGrantRecord, Long> ReferRole = createField(DSL.name("refer_role"), SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>win_role_grant.grant_type</code>.
     */
    public final TableField<WinRoleGrantRecord, pro.fessional.wings.warlock.enums.autogen.GrantType> GrantType = createField(DSL.name("grant_type"), SQLDataType.INTEGER.nullable(false), this, "", new JooqConsEnumConverter(pro.fessional.wings.warlock.enums.autogen.GrantType.class));

    /**
     * The column <code>win_role_grant.grant_entry</code>.
     */
    public final TableField<WinRoleGrantRecord, Long> GrantEntry = createField(DSL.name("grant_entry"), SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>win_role_grant.create_dt</code>.
     */
    public final TableField<WinRoleGrantRecord, LocalDateTime> CreateDt = createField(DSL.name("create_dt"), SQLDataType.LOCALDATETIME(3).nullable(false).defaultValue(DSL.field("CURRENT_TIMESTAMP(3)", SQLDataType.LOCALDATETIME)), this, "");

    /**
     * The column <code>win_role_grant.commit_id</code>.
     */
    public final TableField<WinRoleGrantRecord, Long> CommitId = createField(DSL.name("commit_id"), SQLDataType.BIGINT.nullable(false), this, "");

    private WinRoleGrantTable(Name alias, Table<WinRoleGrantRecord> aliased) {
        this(alias, aliased, null);
    }

    private WinRoleGrantTable(Name alias, Table<WinRoleGrantRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>win_role_grant</code> table reference
     */
    public WinRoleGrantTable(String alias) {
        this(DSL.name(alias), WinRoleGrant);
    }

    /**
     * Create an aliased <code>win_role_grant</code> table reference
     */
    public WinRoleGrantTable(Name alias) {
        this(alias, WinRoleGrant);
    }

    /**
     * Create a <code>win_role_grant</code> table reference
     */
    public WinRoleGrantTable() {
        this(DSL.name("win_role_grant"), null);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : DefaultSchemaWarlock.DEFAULT_SCHEMA;
    }

    @Override
    public UniqueKey<WinRoleGrantRecord> getPrimaryKey() {
        return Internal.createUniqueKey(WinRoleGrantTable.WinRoleGrant, DSL.name("KEY_win_role_grant_PRIMARY"), new TableField[] { WinRoleGrantTable.WinRoleGrant.ReferRole, WinRoleGrantTable.WinRoleGrant.GrantType, WinRoleGrantTable.WinRoleGrant.GrantEntry }, true);
    }

    @Override
    public WinRoleGrantTable as(String alias) {
        return new WinRoleGrantTable(DSL.name(alias), this);
    }

    @Override
    public WinRoleGrantTable as(Name alias) {
        return new WinRoleGrantTable(alias, this);
    }

    @Override
    public WinRoleGrantTable as(Table<?> alias) {
        return new WinRoleGrantTable(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public WinRoleGrantTable rename(String name) {
        return new WinRoleGrantTable(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public WinRoleGrantTable rename(Name name) {
        return new WinRoleGrantTable(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public WinRoleGrantTable rename(Table<?> name) {
        return new WinRoleGrantTable(name.getQualifiedName(), null);
    }

    // -------------------------------------------------------------------------
    // Row5 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row5<Long, pro.fessional.wings.warlock.enums.autogen.GrantType, Long, LocalDateTime, Long> fieldsRow() {
        return (Row5) super.fieldsRow();
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Function)}.
     */
    public <U> SelectField<U> mapping(Function5<? super Long, ? super pro.fessional.wings.warlock.enums.autogen.GrantType, ? super Long, ? super LocalDateTime, ? super Long, ? extends U> from) {
        return convertFrom(Records.mapping(from));
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Class,
     * Function)}.
     */
    public <U> SelectField<U> mapping(Class<U> toType, Function5<? super Long, ? super pro.fessional.wings.warlock.enums.autogen.GrantType, ? super Long, ? super LocalDateTime, ? super Long, ? extends U> from) {
        return convertFrom(toType, Records.mapping(from));
    }
    

    /**
     * alias asE2
     */
    @Override
    @NotNull
    public WinRoleGrantTable getAliasTable() {
        return asE2;
    }
}
