package pro.fessional.wings.faceless.sample;

import lombok.Setter;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pro.fessional.wings.faceless.WingsTestHelper;
import pro.fessional.wings.faceless.flywave.SchemaRevisionManager;
import pro.fessional.wings.faceless.util.FlywaveRevisionScanner;

import java.util.SortedMap;

/**
 * ① 使用wings的flywave管理数据库版本
 *
 * @author trydofor
 * @since 2019-06-22
 */

@SpringBootTest(properties =
        {"debug = true",
         "spring.wings.faceless.flywave.enabled=true",
//         "spring.wings.faceless.enumi18n.enabled=true",
//         "spring.shardingsphere.datasource.names=writer",
//         "spring.shardingsphere.datasource.writer.jdbc-url=jdbc:mysql://127.0.0.1:3306/wings?autoReconnect=true&useSSL=false",
//         "spring.shardingsphere.datasource.writer.username=trydofor",
//         "spring.shardingsphere.datasource.writer.password=moilioncircle",
        })
@Disabled("手动执行一次，初始化步骤，危险操作")
public class WingsSchemaGenerator {

    @Setter(onMethod = @__({@Autowired}))
    private WingsTestHelper wingsTestHelper;

    @Setter(onMethod = @__({@Autowired}))
    private SchemaRevisionManager schemaRevisionManager;

    @Test
    public void init() {
        wingsTestHelper.cleanTable();
        final SortedMap<Long, SchemaRevisionManager.RevisionSql> sqls = FlywaveRevisionScanner.scan(FlywaveRevisionScanner.REVISION_PATH_MASTER, FlywaveRevisionScanner.REVISION_PATH_BRANCH_3RD_ENU18N);
        schemaRevisionManager.checkAndInitSql(sqls, 0, true);
        schemaRevisionManager.publishRevision(WingsTestHelper.REVISION_TEST_V2, 0);
    }
}
