package pro.fessional.wings.faceless.sample;

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


@Slf4j
@SpringBootTest
@Disabled("导出数据库表结构，备份数据时使用")
@SuppressWarnings("NewClassNamingConvention")
public class WingsSchemaDumper {

    @Setter(onMethod_ = {@Autowired})
    private DataSource dataSource;

    @Setter(onMethod_ = {@Autowired})
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
                "-- ==================== Floor-10(F11/90#):辅助 ======================="
        );
        Function1<List<String>, List<String>> rec = SchemaFulldumpManager.includeRegexp(
                "sys_light_.*",
                "sys_schema_.*",
                "sys_standard_.*",
                "sys_constant_.*");

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
