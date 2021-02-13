package pro.fessional.wings.example.init;

import kotlin.jvm.functions.Function1;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pro.fessional.wings.example.WingsExampleApplication;
import pro.fessional.wings.faceless.flywave.SchemaFulldumpManager;

import javax.sql.DataSource;
import java.util.List;

/**
 * ④根据配置，把schema导出，方便保存和对比
 * 更牛的数据库操作，参考 godbart项目
 *
 * @author trydofor
 * @since 2019-12-26
 */


@SpringBootTest(classes = WingsExampleApplication.class, properties =
        {"debug = true",
         "spring.wings.faceless.flywave.enabled.module=true",
//         "spring.wings.faceless.enabled.enumi18n=true",
//         "spring.shardingsphere.datasource.names=writer",
//         "spring.shardingsphere.datasource.writer.jdbc-url=jdbc:mysql://127.0.0.1:3306/wings?autoReconnect=true&useSSL=false",
//         "spring.shardingsphere.datasource.writer.username=trydofor",
//         "spring.shardingsphere.datasource.writer.password=moilioncircle",
        })
@Disabled("手动执行，版本更新时处理")
@Slf4j
public class Wings4SchemaDumper {

    @Setter(onMethod = @__({@Autowired}))
    private DataSource dataSource;

    @Setter(onMethod = @__({@Autowired}))
    private SchemaFulldumpManager schemaFulldumpManager;

    @Test
    public void dump() {
        Function1<List<String>, List<String>> ddl = SchemaFulldumpManager.groupedTable(false,
                "-- ==================== Basement-4(B4/10#):基础 =======================",
                "sys_schema_version", // 101/表结构版本
                "sys_schema_journal", // 102/数据触发器
                "sys_light_sequence", // 103/序号生成器
                "sys_commit_journal", // 104/数据变更集
                "-- ==================== Basement-3(B3/15#):多语言，多时区，多货币 =======================",
                "sys_constant_enum", // 105/常量枚举:自动生成enum类
                "sys_standard_i18n", // 106/标准多国语
                "-- ==================== Floor-1(F4/40#):用户权限 =======================",
                "win_auth_role", // 402/权限组(角色)
                "win_user", // 411/用户
                "win_user_login", // 412/用户登录
                "-- ==================== Floor-2(F4/45#):商品信息======================="
        );
        Function1<List<String>, List<String>> rec = SchemaFulldumpManager.includeRegexp(
                "sys_light_.*",
                "sys_constant_enum",
                "sys_standard_i18n",
                "win_auth_role",
                "win_user",
                "win_user_login"
        );

        String type = "local";
        String root = "./src/test/resources/wings-flywave/fulldump/" + type;
        log.info("===== dump ddl to " + root);
        List<SchemaFulldumpManager.SqlString> ddls = schemaFulldumpManager.dumpDdl(dataSource, ddl);
        schemaFulldumpManager.saveFile(root + "/schema.sql", ddls);
        log.info("===== dump rec to " + root);
        List<SchemaFulldumpManager.SqlString> recs = schemaFulldumpManager.dumpRec(dataSource, rec);
        schemaFulldumpManager.saveFile(root + "/record.sql", recs);
    }
}
