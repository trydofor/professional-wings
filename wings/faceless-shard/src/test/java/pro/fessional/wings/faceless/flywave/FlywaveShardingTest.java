package pro.fessional.wings.faceless.flywave;

import lombok.Setter;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import pro.fessional.wings.faceless.WingsTestHelper;
import pro.fessional.wings.faceless.database.DataSourceContext;
import pro.fessional.wings.faceless.util.FlywaveRevisionScanner;

import java.util.SortedMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static pro.fessional.wings.faceless.WingsTestHelper.REVISION_TEST_V1;
import static pro.fessional.wings.faceless.WingsTestHelper.REVISION_TEST_V2;
import static pro.fessional.wings.faceless.WingsTestHelper.testcaseNotice;

/**
 * @author trydofor
 * @since 2021-01-18
 */
@SpringBootTest
@TestMethodOrder(MethodOrderer.MethodName.class)
public class FlywaveShardingTest {

    @Setter(onMethod_ = {@Autowired})
    private SchemaShardingManager schemaShardingManager;
    @Setter(onMethod_ = {@Autowired})
    private SchemaRevisionManager schemaRevisionManager;
    @Setter(onMethod_ = {@Autowired})
    private JdbcTemplate shardingJdbcTemplate;
    @Setter(onMethod_ = {@Autowired})
    private DataSourceContext dataSourceContext;
    @Setter(onMethod_ = {@Autowired})
    private WingsTestHelper wingsTestHelper;

    @Test
    public void test0CleanTables() {
        wingsTestHelper.cleanTable();
        final SortedMap<Long, SchemaRevisionManager.RevisionSql> sqls = FlywaveRevisionScanner.scanMaster();
        schemaRevisionManager.checkAndInitSql(sqls, 0, true);
    }

    @Test
    public void test1Single() {
        schemaRevisionManager.publishRevision(REVISION_TEST_V1, 0);
        wingsTestHelper.assertHas(WingsTestHelper.Type.Table, "tst_sharding");

        schemaRevisionManager.forceApplyBreak(REVISION_TEST_V2, 2, true, "writer");
        assertEquals(20, countRecords("writer", "tst_sharding"));
        assertEquals(0, countRecords("reader", "tst_sharding"));

        testcaseNotice("Force to insert data in the writer. Select by SQL, only the writer has the data, not the reader.");
    }

    @Test
    public void test2Sharding() {
        schemaShardingManager.publishShard("sys_schema_journal", 2);
        wingsTestHelper.assertHas(WingsTestHelper.Type.Table, "sys_schema_journal_0", "sys_schema_journal_1");
        schemaShardingManager.publishShard("sys_schema_journal", 0);
        wingsTestHelper.assertNot(WingsTestHelper.Type.Table, "sys_schema_journal_0", "sys_schema_journal_1");
        testcaseNotice("2 sys_schema_journal_[0-1] tables on writer and reader at the same time");
    }

    @Test
    public void test3ShardMove() {
        schemaShardingManager.publishShard("tst_sharding", 5);
        wingsTestHelper.assertHas(WingsTestHelper.Type.Table, "tst_sharding",
                "tst_sharding_0",
                "tst_sharding_1",
                "tst_sharding_2",
                "tst_sharding_3",
                "tst_sharding_4");
        assertEquals(20, countRecords("writer", "tst_sharding"));
        schemaShardingManager.shardingData("tst_sharding", true);
        // delete from main table
        assertEquals(0, countRecords("writer", "tst_sharding"), "If it fails, run the entire class individually to avoid interference.");
        // insert into the sharding by hash
        assertEquals(4, countRecords("writer", "tst_sharding_0"));
        assertEquals(4, countRecords("writer", "tst_sharding_1"));
        assertEquals(4, countRecords("writer", "tst_sharding_2"));
        assertEquals(4, countRecords("writer", "tst_sharding_3"));
        assertEquals(4, countRecords("writer", "tst_sharding_4"));

        // form 5.x, shardingsphere will SELECT count(*) FROM tst_sharding_0 UNION ALL SELECT count(*) FROM tst_sharding_#
        Integer cnt = shardingJdbcTemplate.queryForObject("SELECT count(*) FROM tst_sharding", Integer.class);
//        testcaseNotice("The writer and reader are not actually configured to synchronize, so reading from db is 0");
        assertEquals(20, cnt);
    }

    public int countRecords(String db, String tbl) {
        final Integer cnt = new JdbcTemplate(dataSourceContext.getBackends().get(db))
                .queryForObject("SELECT count(*) FROM "+tbl, Integer.class);
        return cnt == null ? 0 : cnt;
    }
}
