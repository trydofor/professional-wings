package pro.fessional.wings.faceless.sample;

import lombok.Setter;
import lombok.val;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import pro.fessional.wings.faceless.flywave.SchemaRevisionManager;

import static pro.fessional.wings.faceless.WingsTestHelper.REVISION_TEST_V1;
import static pro.fessional.wings.faceless.util.FlywaveRevisionScanner.REVISION_1ST_SCHEMA;
import static pro.fessional.wings.faceless.util.FlywaveRevisionScanner.REVISION_PATH_BRANCH_3RD_ENU18N;
import static pro.fessional.wings.faceless.util.FlywaveRevisionScanner.REVISION_PATH_MASTER;
import static pro.fessional.wings.faceless.util.FlywaveRevisionScanner.scan;

/**
 * @author trydofor
 * @since 2020-08-11
 */

@SpringBootTest
@ActiveProfiles("init")
@Disabled("手动执行，JavaMainJooqGenSample 需要 ")
public class WingsInitDatabaseSample {

    @Setter(onMethod = @__({@Autowired}))
    private SchemaRevisionManager schemaRevisionManager;

    @Test
    public void init0601() {
        // 初始
        val sqls = scan(REVISION_PATH_MASTER, REVISION_PATH_BRANCH_3RD_ENU18N);
        schemaRevisionManager.publishRevision(REVISION_1ST_SCHEMA, 0);
        schemaRevisionManager.checkAndInitSql(sqls, 0, false);

        // 升级
        schemaRevisionManager.publishRevision(REVISION_TEST_V1, 0);
    }
}
