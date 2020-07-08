package pro.fessional.wings.example.init;

import lombok.val;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import pro.fessional.wings.example.WingsExampleApplication;
import pro.fessional.wings.faceless.flywave.SchemaRevisionManager;
import pro.fessional.wings.faceless.util.FlywaveRevisionGui;
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
//         "spring.shardingsphere.datasource.names=writer",
//         "spring.shardingsphere.datasource.writer.jdbc-url=jdbc:mysql://127.0.0.1:3306/wings?autoReconnect=true&useSSL=false",
//         "spring.shardingsphere.datasource.writer.username=trydofor",
//         "spring.shardingsphere.datasource.writer.password=moilioncircle",
        })
@Ignore("手动执行一次，初始化步骤，危险操作")
public class Wings1SchemaGen {

    private SchemaRevisionManager schemaRevisionManager;

    @Autowired
    public void setSchemaRevisionManager(SchemaRevisionManager schemaRevisionManager) {
        this.schemaRevisionManager = schemaRevisionManager;
        schemaRevisionManager.confirmWay(FlywaveRevisionGui.confirmDialog());
        schemaRevisionManager.messageWay(FlywaveRevisionGui.messageDialog());
    }

    @Test
    public void gen() {
        long revision = 2019_0704_03L;
        long commitId = 2020_0606_1235L;
//        String path = "classpath*:/wings-flywave/revision/**/*.sql"; // 全部类路径
//        String path = "classpath:/wings-flywave/revision/**/*.sql";  // 当前类路径
//        String path = "file:src/main/resources/wings-flywave/revision/**/*.sql"; // 具体文件
//        String path = "file:src/main/resources/wings-flywave/dev-revi/**/*.sql"; // DEV文件
        val sqls = FlywaveRevisionScanner.scan(REVISION_PATH_MASTER, REVISION_PATH_BRANCH_3RD_ENU18N);

        // 合并，升级
//        mergeThenPub(sqls, commitId, revision);
        // 是否更新前，更新掉数据库中的脚本，以免字段修改无法降级
         down3rdThenMergePub(sqls, commitId, revision);
        // 重复升级最新版，用来检查脚本正确性
        // forceDownThenMergePub(sqls, commitId, revision);
        // 连续降级，合并，再升级
//        downMergeThenPub(sqls, commitId, 2020_0702_01L, 2020_0703_01L);
    }

    //先降级，否则无法更新已更新的sql
    private void down3rdThenMergePub(SortedMap<Long, SchemaRevisionManager.RevisionSql> sqls, long commitId, long revision) {
        schemaRevisionManager.publishRevision(REVISION_3RD_ENU18N, commitId);
        schemaRevisionManager.checkAndInitSql(sqls, commitId, true);
        schemaRevisionManager.publishRevision(revision, commitId);
    }

    //先降级，否则无法更新已更新的sql
    private void mergeThenDown3rdPub(SortedMap<Long, SchemaRevisionManager.RevisionSql> sqls, long commitId, long revision) {
        schemaRevisionManager.checkAndInitSql(sqls, commitId, true);
        schemaRevisionManager.publishRevision(REVISION_3RD_ENU18N, commitId);
        schemaRevisionManager.publishRevision(revision, commitId);
    }

    // 直接升级
    private void mergeThenPub(SortedMap<Long, SchemaRevisionManager.RevisionSql> sqls, long commitId, long revision) {
        schemaRevisionManager.checkAndInitSql(sqls, commitId, true);
        schemaRevisionManager.publishRevision(revision, commitId);
    }

    // 重复升级
    private void forceDownThenMergePub(SortedMap<Long, SchemaRevisionManager.RevisionSql> sqls, long commitId, long revision) {
        schemaRevisionManager.forceApplyBreak(revision, commitId, false, null);
        schemaRevisionManager.checkAndInitSql(sqls, commitId, true);
        schemaRevisionManager.publishRevision(revision, commitId);
    }

    // 强制升级
    private void insertThenForce(SortedMap<Long, SchemaRevisionManager.RevisionSql> sqls, long commitId, long revision) {
        schemaRevisionManager.checkAndInitSql(sqls, commitId, false);
        schemaRevisionManager.forceApplyBreak(revision, commitId, true, null);
    }

    // 连续降级再升级
    private void downMergeThenPub(SortedMap<Long, SchemaRevisionManager.RevisionSql> sqls, long commitId, long... revision) {
        for (int i = revision.length - 1; i >= 0; i--) {
            schemaRevisionManager.publishRevision(revision[i], commitId);
        }
        schemaRevisionManager.checkAndInitSql(sqls, commitId, true);
        for (long l : revision) {
            schemaRevisionManager.publishRevision(l, commitId);
        }
    }
}