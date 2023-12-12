package pro.fessional.wings.faceless.sample;

import io.qameta.allure.TmsLink;
import lombok.Setter;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pro.fessional.wings.faceless.flywave.SchemaRevisionManager;
import pro.fessional.wings.faceless.flywave.WingsRevision;

import static pro.fessional.wings.faceless.helper.WingsTestHelper.REVISION_TEST_V1;
import static pro.fessional.wings.faceless.util.FlywaveRevisionScanner.REVISION_PATH_MASTER;
import static pro.fessional.wings.faceless.util.FlywaveRevisionScanner.scan;

/**
 * @author trydofor
 * @since 2020-08-11
 */

@SpringBootTest
@Disabled("Init database, have handled by devs")
public class TestWingsInitDatabaseSample {

    @Setter(onMethod_ = {@Autowired})
    private SchemaRevisionManager schemaRevisionManager;

    @Test
    @TmsLink("C12024")
    public void init0601() {
        // init
        var sqls = scan(REVISION_PATH_MASTER, WingsRevision.V01_19_0521_01_EnumI18n.classpath());
        schemaRevisionManager.publishRevision(WingsRevision.V00_19_0512_01_Schema.revision(), 0);
        schemaRevisionManager.checkAndInitSql(sqls, 0, false);

        // upgrade
        schemaRevisionManager.publishRevision(REVISION_TEST_V1, 0);
    }
}
