/*
 * This file is generated by jOOQ.
 */
package pro.fessional.wings.warlock.database.autogen;


import pro.fessional.wings.warlock.database.autogen.tables.SysConstantEnumTable;
import pro.fessional.wings.warlock.database.autogen.tables.SysStandardI18nTable;
import pro.fessional.wings.warlock.database.autogen.tables.WinPermEntryTable;
import pro.fessional.wings.warlock.database.autogen.tables.WinRoleEntryTable;
import pro.fessional.wings.warlock.database.autogen.tables.WinRoleGrantTable;
import pro.fessional.wings.warlock.database.autogen.tables.WinUserAnthnTable;
import pro.fessional.wings.warlock.database.autogen.tables.WinUserBasisTable;
import pro.fessional.wings.warlock.database.autogen.tables.WinUserGrantTable;
import pro.fessional.wings.warlock.database.autogen.tables.WinUserLoginTable;

import javax.annotation.Generated;


/**
 * Convenience access to all tables in the default schema.
 */
@Generated(
    value = {
        "https://www.jooq.org",
        "jOOQ version:3.14.4",
        "schema version:2020102402"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
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
     * The table <code>win_perm_entry</code>.
     */
    public static final WinPermEntryTable WinPermEntry = WinPermEntryTable.WinPermEntry;

    /**
     * The table <code>win_role_entry</code>.
     */
    public static final WinRoleEntryTable WinRoleEntry = WinRoleEntryTable.WinRoleEntry;

    /**
     * The table <code>win_role_grant</code>.
     */
    public static final WinRoleGrantTable WinRoleGrant = WinRoleGrantTable.WinRoleGrant;

    /**
     * The table <code>win_user_anthn</code>.
     */
    public static final WinUserAnthnTable WinUserAnthn = WinUserAnthnTable.WinUserAnthn;

    /**
     * The table <code>win_user_basis</code>.
     */
    public static final WinUserBasisTable WinUserBasis = WinUserBasisTable.WinUserBasis;

    /**
     * The table <code>win_user_grant</code>.
     */
    public static final WinUserGrantTable WinUserGrant = WinUserGrantTable.WinUserGrant;

    /**
     * The table <code>win_user_login</code>.
     */
    public static final WinUserLoginTable WinUserLogin = WinUserLoginTable.WinUserLogin;
}
