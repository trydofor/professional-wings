package pro.fessional.wings.faceless.sample;

import io.qameta.allure.TmsLink;
import lombok.Setter;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pro.fessional.wings.faceless.flywave.SchemaRevisionManager;
import pro.fessional.wings.faceless.flywave.WingsRevision;
import pro.fessional.wings.faceless.util.FlywaveRevisionScanner;
import pro.fessional.wings.testing.faceless.database.TestingDatabaseHelper;

import static pro.fessional.wings.faceless.flywave.WingsRevision.V90_22_0601_02_TestRecord;

/**
 * Use flywave to manage database version
 *
 * @author trydofor
 * @since 2019-06-22
 */

@SpringBootTest(properties = {
//         "spring.shardingsphere.datasource.names=writer",
//         "spring.shardingsphere.datasource.writer.jdbc-url=jdbc:mysql://127.0.0.1:51487/wings?autoReconnect=true&useSSL=false",
//         "spring.shardingsphere.datasource.writer.username=trydofor",
//         "spring.shardingsphere.datasource.writer.password=moilioncircle",
})
@Disabled("Sample: init database, have handled by devs")
public class TestWingsSchemaGeneratorSample {

    @Setter(onMethod_ = {@Autowired})
    private SchemaRevisionManager schemaRevisionManager;

    @Setter(onMethod_ = {@Autowired})
    private TestingDatabaseHelper testingDatabaseHelper;

    @Test
    @TmsLink("C12026")
    public void init060102() {
        testingDatabaseHelper.cleanTable();
        var sqls = FlywaveRevisionScanner.scan(FlywaveRevisionScanner.REVISION_PATH_MASTER,
                WingsRevision.V01_19_0521_01_EnumI18n.classpath());
        schemaRevisionManager.checkAndInitSql(sqls, 0, true);
        schemaRevisionManager.publishRevision(V90_22_0601_02_TestRecord.revision(), 0);
    }
}
