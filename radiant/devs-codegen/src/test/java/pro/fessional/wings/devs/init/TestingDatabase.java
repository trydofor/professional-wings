package pro.fessional.wings.devs.init;

import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import pro.fessional.mirana.best.AssertState;
import pro.fessional.wings.faceless.flywave.SchemaRevisionManager;
import pro.fessional.wings.faceless.flywave.SchemaShardingManager;
import pro.fessional.wings.faceless.flywave.WingsRevision;
import pro.fessional.wings.faceless.util.FlywaveRevisionScanner;
import pro.fessional.wings.testing.faceless.database.TestingDatabaseHelper;
import pro.fessional.wings.testing.silencer.TestingPropertyHelper;

import java.nio.file.Path;

import static pro.fessional.wings.faceless.util.FlywaveRevisionScanner.REVISION_PATH_REVIFILE_TAIL;


/**
 * @author trydofor
 * @since 2024-01-23
 */
@Slf4j
public class TestingDatabase {

    @Setter(onMethod_ = {@Autowired})
    protected TestingDatabaseHelper testingDatabaseHelper;
    @Setter(onMethod_ = {@Autowired})
    protected TestingPropertyHelper testingPropertyHelper;
    @Setter(onMethod_ = {@Autowired})
    protected SchemaRevisionManager schemaRevisionManager;
    @Setter(onMethod_ = {@Autowired})
    protected SchemaShardingManager schemaShardingManager;

    /**
     * drop all tables in current database
     */
    public void cleanTable() {
        testingDatabaseHelper.cleanTable();
    }

    /**
     * publish to the max revision
     */
    @SneakyThrows
    public void publishMax(WingsRevision... include) {
        if (include == null || include.length == 0) return;

        var helper = FlywaveRevisionScanner.helper();
        long revi = 0;
        for (WingsRevision rv : include) {
            Path path = testingPropertyHelper.modulePath(rv.getRoot(), rv.getPath());
            AssertState.notNull(path, "path={} not found", path);
            String abp = path.toFile().getCanonicalPath();
            helper.path("file://" + abp + REVISION_PATH_REVIFILE_TAIL);
            log.info("revi-path={}", abp);
            if (rv.revision() > revi) {
                revi = rv.revision();
            }
        }

        var sqls = helper.scan();

        testingDatabaseHelper.cleanTable();
        schemaRevisionManager.checkAndInitSql(sqls, -9, true);
        schemaRevisionManager.publishRevision(revi, -9);
    }

    public void reset(WingsRevision... include) {
        cleanTable();
        publishMax(include);
    }

    public void shard(String table, int number) {
        schemaShardingManager.publishShard(table, number);
    }
}
