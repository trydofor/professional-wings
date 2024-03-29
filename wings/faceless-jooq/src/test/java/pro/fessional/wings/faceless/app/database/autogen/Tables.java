/*
 * This file is generated by jOOQ.
 */
package pro.fessional.wings.faceless.app.database.autogen;


import pro.fessional.wings.faceless.app.database.autogen.tables.SysConstantEnumTable;
import pro.fessional.wings.faceless.app.database.autogen.tables.SysStandardI18nTable;
import pro.fessional.wings.faceless.app.database.autogen.tables.TstNormalTableTable;
import pro.fessional.wings.faceless.app.database.autogen.tables.TstShardingTable;

import javax.annotation.processing.Generated;


/**
 * Convenience access to all tables in the default schema.
 */
@Generated(
    value = {
        "https://www.jooq.org",
        "jOOQ version:3.18.7",
        "schema version:2020102701"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class Tables {

    /**
     * The table <code>sys_constant_enum</code>.
     */
    public static final SysConstantEnumTable SysConstantEnum = SysConstantEnumTable.SysConstantEnum;

    /**
     * The table <code>sys_standard_i18n</code>.
     */
    public static final SysStandardI18nTable SysStandardI18n = SysStandardI18nTable.SysStandardI18n;

    /**
     * The table <code>tst_normal_table</code>.
     */
    public static final TstNormalTableTable TstNormalTable = TstNormalTableTable.TstNormalTable;

    /**
     * The table <code>tst_sharding</code>.
     */
    public static final TstShardingTable TstSharding = TstShardingTable.TstSharding;
}
