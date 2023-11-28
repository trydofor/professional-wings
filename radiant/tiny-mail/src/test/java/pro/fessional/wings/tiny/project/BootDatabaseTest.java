package pro.fessional.wings.tiny.project;

import io.qameta.allure.TmsLink;
import lombok.Setter;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import pro.fessional.wings.faceless.flywave.SchemaRevisionManager;
import pro.fessional.wings.faceless.project.ProjectSchemaManager;

import javax.sql.DataSource;

import static pro.fessional.wings.faceless.flywave.WingsRevision.V00_19_0512_01_Schema;
import static pro.fessional.wings.faceless.flywave.WingsRevision.V01_19_0520_01_IdLog;
import static pro.fessional.wings.faceless.flywave.WingsRevision.V01_19_0521_01_EnumI18n;
import static pro.fessional.wings.faceless.flywave.WingsRevision.V03_20_1023_01_AuthEnum;
import static pro.fessional.wings.faceless.flywave.WingsRevision.V04_20_1024_01_UserLogin;
import static pro.fessional.wings.faceless.flywave.WingsRevision.V04_20_1024_02_RolePermit;
import static pro.fessional.wings.faceless.flywave.WingsRevision.V05_20_1025_01_ConfRuntime;
import static pro.fessional.wings.faceless.flywave.WingsRevision.V06_20_1026_01_TinyTask;
import static pro.fessional.wings.faceless.flywave.WingsRevision.V07_20_1027_01_TinyMail;


/**
 * @author trydofor
 * @since 2021-02-22
 */
@SpringBootTest(properties = {
        "wings.enabled.faceless.flywave=true",
        "wings.faceless.flywave.checker=false",
})
@Disabled("manual initialization")
public class BootDatabaseTest {
    @Setter(onMethod_ = {@Autowired})
    private DataSource dataSource;
    @Setter(onMethod_ = {@Autowired})
    private SchemaRevisionManager schemaRevisionManager;

    @Test
    @TmsLink("C15010")
    void testDropAndInit() {
        JdbcTemplate tmpl = new JdbcTemplate(dataSource);
        tmpl.query("SHOW TABLES", rs -> {
            String tbl = rs.getString(1);
            tmpl.execute("DROP TABLE `" + tbl + "`");
        });

        final ProjectSchemaManager manager = new ProjectSchemaManager(schemaRevisionManager);
        manager.mergePublish(V07_20_1027_01_TinyMail.revision(), helper ->
                helper.master().path(
                        V00_19_0512_01_Schema,
                        V01_19_0520_01_IdLog,
                        V01_19_0521_01_EnumI18n,
                        V03_20_1023_01_AuthEnum,
                        V04_20_1024_01_UserLogin,
                        V04_20_1024_02_RolePermit,
                        V05_20_1025_01_ConfRuntime,
                        V06_20_1026_01_TinyTask,
                        V07_20_1027_01_TinyMail)
        );
    }
}
