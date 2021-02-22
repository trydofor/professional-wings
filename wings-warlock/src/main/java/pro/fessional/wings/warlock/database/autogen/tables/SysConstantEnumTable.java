/*
 * This file is generated by jOOQ.
 */
package pro.fessional.wings.warlock.database.autogen.tables;


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
import pro.fessional.wings.faceless.database.jooq.WingsAliasTable;
import pro.fessional.wings.warlock.database.autogen.DefaultSchema;
import pro.fessional.wings.warlock.database.autogen.tables.records.SysConstantEnumRecord;

import javax.annotation.Generated;
import java.util.Arrays;
import java.util.List;


/**
 * The table <code>wings_warlock.sys_constant_enum</code>.
 */
@Generated(
    value = {
        "https://www.jooq.org",
        "jOOQ version:3.14.4",
        "schema version:2020102401"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class SysConstantEnumTable extends TableImpl<SysConstantEnumRecord> implements WingsAliasTable<SysConstantEnumTable> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>sys_constant_enum</code>
     */
    public static final SysConstantEnumTable SysConstantEnum = new SysConstantEnumTable();
        public static final SysConstantEnumTable asK5 = SysConstantEnum.as("k5");

    /**
     * The class holding records for this type
     */
    @Override
    public Class<SysConstantEnumRecord> getRecordType() {
        return SysConstantEnumRecord.class;
    }

    /**
     * The column <code>sys_constant_enum.id</code>.
     */
    public final TableField<SysConstantEnumRecord, Integer> Id = createField(DSL.name("id"), SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>sys_constant_enum.type</code>.
     */
    public final TableField<SysConstantEnumRecord, String> Type = createField(DSL.name("type"), SQLDataType.VARCHAR(100).nullable(false), this, "");

    /**
     * The column <code>sys_constant_enum.code</code>.
     */
    public final TableField<SysConstantEnumRecord, String> Code = createField(DSL.name("code"), SQLDataType.VARCHAR(100).nullable(false), this, "");

    /**
     * The column <code>sys_constant_enum.hint</code>.
     */
    public final TableField<SysConstantEnumRecord, String> Hint = createField(DSL.name("hint"), SQLDataType.VARCHAR(100).nullable(false), this, "");

    /**
     * The column <code>sys_constant_enum.info</code>.
     */
    public final TableField<SysConstantEnumRecord, String> Info = createField(DSL.name("info"), SQLDataType.VARCHAR(500).nullable(false), this, "");

    private SysConstantEnumTable(Name alias, Table<SysConstantEnumRecord> aliased) {
        this(alias, aliased, null);
    }

    private SysConstantEnumTable(Name alias, Table<SysConstantEnumRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>sys_constant_enum</code> table reference
     */
    public SysConstantEnumTable(String alias) {
        this(DSL.name(alias), SysConstantEnum);
    }

    /**
     * Create an aliased <code>sys_constant_enum</code> table reference
     */
    public SysConstantEnumTable(Name alias) {
        this(alias, SysConstantEnum);
    }

    /**
     * Create a <code>sys_constant_enum</code> table reference
     */
    public SysConstantEnumTable() {
        this(DSL.name("sys_constant_enum"), null);
    }

    @Override
    public Schema getSchema() {
        return DefaultSchema.DEFAULT_SCHEMA;
    }

    @Override
    public UniqueKey<SysConstantEnumRecord> getPrimaryKey() {
        return Internal.createUniqueKey(SysConstantEnumTable.SysConstantEnum, DSL.name("KEY_sys_constant_enum_PRIMARY"), new TableField[] { SysConstantEnumTable.SysConstantEnum.Id }, true);
    }

    @Override
    public List<UniqueKey<SysConstantEnumRecord>> getKeys() {
        return Arrays.<UniqueKey<SysConstantEnumRecord>>asList(
              Internal.createUniqueKey(SysConstantEnumTable.SysConstantEnum, DSL.name("KEY_sys_constant_enum_PRIMARY"), new TableField[] { SysConstantEnumTable.SysConstantEnum.Id }, true)
        );
    }

    @Override
    public SysConstantEnumTable as(String alias) {
        return new SysConstantEnumTable(DSL.name(alias), this);
    }

    @Override
    public SysConstantEnumTable as(Name alias) {
        return new SysConstantEnumTable(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public SysConstantEnumTable rename(String name) {
        return new SysConstantEnumTable(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public SysConstantEnumTable rename(Name name) {
        return new SysConstantEnumTable(name, null);
    }

    // -------------------------------------------------------------------------
    // Row5 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row5<Integer, String, String, String, String> fieldsRow() {
        return (Row5) super.fieldsRow();
    }

    /**
     * alias K5
     */
    @Override
    public SysConstantEnumTable getAliasTable() {
            return asK5;
    }
}
