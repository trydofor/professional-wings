package pro.fessional.wings.faceless.sample;

import io.qameta.allure.TmsLink;
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
@Disabled("Sample: dump schema structure, used to backup and restore")
public class TestWingsSchemaDumperSample {

    @Setter(onMethod_ = {@Autowired})
    private DataSource dataSource;

    @Setter(onMethod_ = {@Autowired})
    private SchemaFulldumpManager schemaFulldumpManager;

    @Test
    @TmsLink("C12025")
    public void dump() {
        Function1<List<String>, List<String>> ddl = SchemaFulldumpManager.groupedTable(false,
                "-- ==================== Basement-4(B4/10#):basic =======================",
                "sys_schema_version", // 101/table structure
                "sys_schema_journal", // 102/data trigger
                "sys_light_sequence", // 103/id and sequence
                "sys_commit_journal", // 104/commit log
                "-- ==================== Basement-3(B3/15#):multiple lang/time/money =======================",
                "sys_constant_enum", // 105/enum const: auto code gen
                "sys_standard_i18n", // 106/i18n message
                "-- ==================== Floor-10(F11/90#):helper ======================="
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
