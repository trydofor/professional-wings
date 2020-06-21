package pro.fessional.wings.example.init;

import lombok.Setter;
import lombok.val;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import pro.fessional.wings.example.WingsExampleApplication;
import pro.fessional.wings.faceless.flywave.SchemaRevisionManager;
import pro.fessional.wings.faceless.util.FlywaveRevisionScanner;

import java.util.SortedMap;

import static pro.fessional.wings.faceless.util.FlywaveRevisionScanner.REVISION_3RD_ENU18N;
import static pro.fessional.wings.faceless.util.FlywaveRevisionScanner.REVISION_PATH_BRANCH_3RD_ENU18N;
import static pro.fessional.wings.faceless.util.FlywaveRevisionScanner.REVISION_PATH_MASTER;

/**
 * ① 使用wings的flywave管理数据库版本
 *
 * @author trydofor
 * @since 2019-06-22
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = WingsExampleApplication.class, properties =
        {"debug = true",
         "spring.wings.flywave.enabled=true",
//         "spring.wings.enumi18n.enabled=true",
//         "spring.shardingsphere.datasource.names=master",
//         "spring.shardingsphere.datasource.master.jdbc-url=jdbc:mysql://127.0.0.1:3306/wings?autoReconnect=true&useSSL=false",
//         "spring.shardingsphere.datasource.master.username=trydofor",
//         "spring.shardingsphere.datasource.master.password=moilioncircle",
        })
@Ignore("手动执行一次，初始化步骤，危险操作")
public class Wings1SchemaGen {

    @Setter(onMethod = @__({@Autowired}))
    private SchemaRevisionManager schemaRevisionManager;

    @Test
    public void gen() {
        long revision = 2019_0704_03L;
        long commitId = 2020_0606_1235L;
//        String path = "classpath*:/wings-flywave/revision/**/*.sql"; // 全部类路径
//        String path = "classpath:/wings-flywave/revision/**/*.sql";  // 当前类路径
//        String path = "file:src/main/resources/wings-flywave/revision/**/*.sql"; // 具体文件
//        String path = "file:src/main/resources/wings-flywave/dev-revi/**/*.sql"; // DEV文件
        val sqls = FlywaveRevisionScanner.scan(REVISION_PATH_MASTER, REVISION_PATH_BRANCH_3RD_ENU18N);

        // 是否更新前，更新掉数据库中的脚本，以免字段修改无法降级
        mergeThenDown3rdPub(sqls, revision, commitId);
    }

    //先降级，否则无法更新已更新的sql
    private void down3rdThenMergePub(SortedMap<Long, SchemaRevisionManager.RevisionSql> sqls, long revision, long commitId) {
        schemaRevisionManager.publishRevision(REVISION_3RD_ENU18N, commitId);
        schemaRevisionManager.checkAndInitSql(sqls, commitId, true);
        schemaRevisionManager.publishRevision(revision, commitId);
    }
    //先降级，否则无法更新已更新的sql
    private void mergeThenDown3rdPub(SortedMap<Long, SchemaRevisionManager.RevisionSql> sqls, long revision, long commitId) {
        schemaRevisionManager.checkAndInitSql(sqls, commitId, true);
        schemaRevisionManager.publishRevision(REVISION_3RD_ENU18N, commitId);
        schemaRevisionManager.publishRevision(revision, commitId);
    }

    // 直接升级
    private void mergeThenPub(SortedMap<Long, SchemaRevisionManager.RevisionSql> sqls, long revision, long commitId) {
        schemaRevisionManager.checkAndInitSql(sqls, commitId, true);
        schemaRevisionManager.publishRevision(revision, commitId);
    }

    // 重复升级
    private void forceDownThenMergePub(SortedMap<Long, SchemaRevisionManager.RevisionSql> sqls, long revision, long commitId) {
        schemaRevisionManager.forceApplyBreak(revision, commitId, false, null);
        schemaRevisionManager.checkAndInitSql(sqls, commitId, true);
        schemaRevisionManager.publishRevision(revision, commitId);
    }

    // 强制升级
    private void insertThenForce(SortedMap<Long, SchemaRevisionManager.RevisionSql> sqls, long revision, long commitId) {
        schemaRevisionManager.checkAndInitSql(sqls, commitId, false);
        schemaRevisionManager.forceApplyBreak(revision, commitId, true, null);
    }
}