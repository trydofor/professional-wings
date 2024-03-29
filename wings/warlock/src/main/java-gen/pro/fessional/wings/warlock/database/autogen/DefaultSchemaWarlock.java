/*
 * This file is generated by jOOQ.
 */
package pro.fessional.wings.warlock.database.autogen;


import org.jooq.Catalog;
import org.jooq.Table;
import org.jooq.impl.SchemaImpl;
import pro.fessional.wings.warlock.database.autogen.tables.SysConstantEnumTable;
import pro.fessional.wings.warlock.database.autogen.tables.SysStandardI18nTable;
import pro.fessional.wings.warlock.database.autogen.tables.WinConfRuntimeTable;
import pro.fessional.wings.warlock.database.autogen.tables.WinPermEntryTable;
import pro.fessional.wings.warlock.database.autogen.tables.WinRoleEntryTable;
import pro.fessional.wings.warlock.database.autogen.tables.WinRoleGrantTable;
import pro.fessional.wings.warlock.database.autogen.tables.WinUserAuthnTable;
import pro.fessional.wings.warlock.database.autogen.tables.WinUserBasisTable;
import pro.fessional.wings.warlock.database.autogen.tables.WinUserGrantTable;
import pro.fessional.wings.warlock.database.autogen.tables.WinUserLoginTable;

import javax.annotation.processing.Generated;
import java.util.Arrays;
import java.util.List;


/**
 * The schema <code>wings</code>.
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
public class DefaultSchemaWarlock extends SchemaImpl {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>DEFAULT_SCHEMA</code>
     */
    public static final DefaultSchemaWarlock DEFAULT_SCHEMA = new DefaultSchemaWarlock();

    /**
     * The table <code>sys_constant_enum</code>.
     */
    public final SysConstantEnumTable SysConstantEnum = SysConstantEnumTable.SysConstantEnum;

    /**
     * The table <code>sys_standard_i18n</code>.
     */
    public final SysStandardI18nTable SysStandardI18n = SysStandardI18nTable.SysStandardI18n;

    /**
     * The table <code>win_conf_runtime</code>.
     */
    public final WinConfRuntimeTable WinConfRuntime = WinConfRuntimeTable.WinConfRuntime;

    /**
     * The table <code>win_perm_entry</code>.
     */
    public final WinPermEntryTable WinPermEntry = WinPermEntryTable.WinPermEntry;

    /**
     * The table <code>win_role_entry</code>.
     */
    public final WinRoleEntryTable WinRoleEntry = WinRoleEntryTable.WinRoleEntry;

    /**
     * The table <code>win_role_grant</code>.
     */
    public final WinRoleGrantTable WinRoleGrant = WinRoleGrantTable.WinRoleGrant;

    /**
     * The table <code>win_user_authn</code>.
     */
    public final WinUserAuthnTable WinUserAuthn = WinUserAuthnTable.WinUserAuthn;

    /**
     * The table <code>win_user_basis</code>.
     */
    public final WinUserBasisTable WinUserBasis = WinUserBasisTable.WinUserBasis;

    /**
     * The table <code>win_user_grant</code>.
     */
    public final WinUserGrantTable WinUserGrant = WinUserGrantTable.WinUserGrant;

    /**
     * The table <code>win_user_login</code>.
     */
    public final WinUserLoginTable WinUserLogin = WinUserLoginTable.WinUserLogin;

    /**
     * No further instances allowed
     */
    private DefaultSchemaWarlock() {
        super("", null);
    }


    @Override
    public Catalog getCatalog() {
        return DefaultCatalogWarlock.DEFAULT_CATALOG;
    }

    @Override
    public final List<Table<?>> getTables() {
        return Arrays.asList(
            SysConstantEnumTable.SysConstantEnum,
            SysStandardI18nTable.SysStandardI18n,
            WinConfRuntimeTable.WinConfRuntime,
            WinPermEntryTable.WinPermEntry,
            WinRoleEntryTable.WinRoleEntry,
            WinRoleGrantTable.WinRoleGrant,
            WinUserAuthnTable.WinUserAuthn,
            WinUserBasisTable.WinUserBasis,
            WinUserGrantTable.WinUserGrant,
            WinUserLoginTable.WinUserLogin
        );
    }
}
