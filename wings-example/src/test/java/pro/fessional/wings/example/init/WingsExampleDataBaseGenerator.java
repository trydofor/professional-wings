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
public class WingsExampleDataBaseGenerator {

    private SchemaRevisionManager schemaRevisionManager;

    @Autowired
    public void setSchemaRevisionManager(SchemaRevisionManager schemaRevisionManager) {
        this.schemaRevisionManager = schemaRevisionManager;
    }

    @Test
    @Ignore("手动执行一次，初始化步骤")
    public void initR520() {
        // 初始
        SortedMap<Long, SchemaRevisionManager.RevisionSql> sqls = FlywaveRevisionSqlScanner.scan(SchemaRevisionManager.REVISIONSQL_PATH);
        schemaRevisionManager.checkAndInitSql(sqls, 0, false);

        // 初始为可用状态
        schemaRevisionManager.publishRevision(SchemaRevisionManager.INIT2ND_REVISION, 0);
    }

    @Test
    @Ignore("手动执行，版本更新时处理")
    public void initOther() {
//        String path = "classpath*:/wings-flywave/revision/**/*.sql"; // 全部类路径
//        String path = "classpath:/wings-flywave/revision/**/*.sql";  // 当前类路径
        String path = "file:src/main/resources/wings-flywave/revision/**/*.sql"; // 具体文件
        SortedMap<Long, SchemaRevisionManager.RevisionSql> sqls = FlywaveRevisionSqlScanner.scan(path);
        schemaRevisionManager.checkAndInitSql(sqls, 0, false);
        schemaRevisionManager.publishRevision(3L, 0);
    }
}