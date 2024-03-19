package pro.fessional.wings.faceless.sample;

import io.qameta.allure.TmsLink;
import lombok.Setter;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pro.fessional.wings.faceless.flywave.SchemaRevisionManager;
import pro.fessional.wings.faceless.flywave.WingsRevision;
import pro.fessional.wings.testing.faceless.database.TestingDatabaseHelper;

import static pro.fessional.wings.faceless.flywave.WingsRevision.V90_22_0601_01_TestSchema;
import static pro.fessional.wings.faceless.util.FlywaveRevisionScanner.REVISION_PATH_MASTER;
import static pro.fessional.wings.faceless.util.FlywaveRevisionScanner.scan;

/**
 * @author trydofor
 * @since 2020-08-11
 */

@SpringBootTest
@Disabled("Sample: init database, have handled by devs")
public class TestWingsInitDatabaseSample {

    @Setter(onMethod_ = {@Autowired})
    private SchemaRevisionManager schemaRevisionManager;
    
    @Setter(onMethod_ = {@Autowired})
    protected TestingDatabaseHelper testingDatabaseHelper;

    @Test
    @TmsLink("C12024")
    public void init060101() {
        testingDatabaseHelper.cleanTable();
        // init
        var sqls = scan(REVISION_PATH_MASTER, WingsRevision.V01_19_0521_01_EnumI18n.classpath());
        schemaRevisionManager.publishRevision(WingsRevision.V00_19_0512_01_Schema.revision(), 0);
        schemaRevisionManager.checkAndInitSql(sqls, 0, false);

        // upgrade
        schemaRevisionManager.publishRevision(V90_22_0601_01_TestSchema.revision(), 0);
    }
}
