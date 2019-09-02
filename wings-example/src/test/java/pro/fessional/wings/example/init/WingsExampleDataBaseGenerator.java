package pro.fessional.wings.example.init;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import pro.fessional.wings.faceless.flywave.SchemaRevisionManager;
import pro.fessional.wings.faceless.util.FlywaveRevisionSqlScanner;

import java.util.SortedMap;

/**
 * @author trydofor
 * @since 2019-06-22
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Ignore("手动执行一次，初始化步骤")
public class WingsExampleDataBaseGenerator {

    private SchemaRevisionManager schemaRevisionManager;

    @Autowired
    public SchemaRevisionManager getSchemaRevisionManager() {
        return schemaRevisionManager;
    }

    @Test
    public void initR520() {
        // 初始
        SortedMap<Long, SchemaRevisionManager.RevisionSql> sqls = FlywaveRevisionSqlScanner.scan(SchemaRevisionManager.REVISIONSQL_PATH);
        schemaRevisionManager.checkAndInitSql(sqls, 0);

        // 升级
        schemaRevisionManager.publishRevision(SchemaRevisionManager.INIT2ND_REVISION, 0);
    }
}