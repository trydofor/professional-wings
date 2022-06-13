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
@Disabled("手动执行，版本更新时处理")
@Slf4j
public class Devops7SchemaDumperTest {

    @Setter(onMethod_ = {@Autowired})
    private DataSource dataSource;

    @Setter(onMethod_ = {@Autowired})
    private SchemaFulldumpManager schemaFulldumpManager;

    @Test
    public void dumpSchema() {
        Function1<List<String>, List<String>> ddl = SchemaFulldumpManager.groupedTable(false,
                "-- ==================== Basement-4(B4/10#):基础 =======================",
                "sys_schema_version", // 101/表结构版本
                "sys_schema_journal", // 102/数据触发器
                "sys_light_sequence", // 103/序号生成器
                "sys_commit_journal", // 104/数据变更集
                "sys_constant_enum",  // 105/常量枚举:自动生成enum类
                "sys_standard_i18n",  // 106/标准多国语
                "-- ==================== Floor-1(F1/12#-13#):用户 =======================",
                "win_user_basis",  // 120/用户基本表
                "win_user_authn",  // 121/用户验证表
                "win_user_login",  // 122/用户登录表
                "win_perm_entry",  // 130/权限条目表
                "win_role_entry",  // 131/角色条目表
                "win_role_grant",  // 134/角色权限映射表
                "win_user_grant",  // 135/角色权限映射表
                "-- ==================== Floor-10(F11/90#):辅助 ======================="
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
                "win_user_basis",  // 120/用户基本表
                "win_user_authn",  // 121/用户验证表
                "win_user_login",  // 122/用户登录表
                "win_perm_entry",  // 130/权限条目表
                "win_role_entry",  // 131/角色条目表
                "win_role_grant",  // 134/角色权限映射表
                "win_user_grant"  // 135/角色权限映射表
                );

        String root = Devops0ProjectConstant.DUMP_PATH + Devops0ProjectConstant.DUMP_TYPE;
        log.info("===== dump rec to " + root);
        List<SchemaFulldumpManager.SqlString> recs = schemaFulldumpManager.dumpRec(dataSource, rec);
        schemaFulldumpManager.saveFile(root + "/record.sql", recs);
    }
}
