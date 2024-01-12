package com.moilioncircle.wings.devops.project;

import kotlin.jvm.functions.Function1;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pro.fessional.wings.faceless.flywave.SchemaFulldumpManager;

import javax.sql.DataSource;
import java.util.List;

/**
 * @author trydofor
 * @since 2019-12-26
 */


@SpringBootTest
@Disabled("Project: Dump database")
@Slf4j
public class Devops7SchemaDumperTest {

    @Setter(onMethod_ = {@Autowired})
    private DataSource dataSource;

    @Setter(onMethod_ = {@Autowired})
    private SchemaFulldumpManager schemaFulldumpManager;

    @Test
    public void dumpSchema() {
        Function1<List<String>, List<String>> ddl = SchemaFulldumpManager.groupedTable(false,
                "-- ==================== Basement-4(B4/10#): Base =======================",
                "sys_schema_version", // 101/schema version
                "sys_schema_journal", // 102/schema journal
                "sys_light_sequence", // 103/sequence
                "sys_commit_journal", // 104/data changeset
                "sys_constant_enum",  // 105/enum const: auto gen enum
                "sys_standard_i18n",  // 106/i18n message
                "-- ==================== Floor-1(F1/12#-13#): User =======================",
                "win_user_basis",  // 120/user basis
                "win_user_authn",  // 121/user authn
                "win_user_login",  // 122/user login
                "win_perm_entry",  // 130/perm entry
                "win_role_entry",  // 131/role entry
                "win_role_grant",  // 134/grant to role
                "win_user_grant",  // 135/grant to user
                "-- ==================== Floor-10(F11/90#): Help ======================="
        );
        String root = Devops0ProjectConstant.DUMP_PATH + Devops0ProjectConstant.DUMP_TYPE;
        log.info("===== dump ddl to " + root);
        List<SchemaFulldumpManager.SqlString> ddls = schemaFulldumpManager.dumpDdl(dataSource, ddl);
        schemaFulldumpManager.saveFile(root + "/schema.sql", ddls);
    }

    @Test
    public void dumpRecord() {
        Function1<List<String>, List<String>> rec = SchemaFulldumpManager.includeRegexp(
                "sys_schema_.*",
                "sys_light_.*",
                "sys_constant_.*",
                "sys_standard_.*",
                "win_user_basis",
                "win_user_authn",
                "win_user_login",
                "win_perm_entry",
                "win_role_entry",
                "win_role_grant",
                "win_user_grant"
        );

        String root = Devops0ProjectConstant.DUMP_PATH + Devops0ProjectConstant.DUMP_TYPE;
        log.info("===== dump rec to " + root);
        List<SchemaFulldumpManager.SqlString> recs = schemaFulldumpManager.dumpRec(dataSource, rec);
        schemaFulldumpManager.saveFile(root + "/record.sql", recs);
    }
}
