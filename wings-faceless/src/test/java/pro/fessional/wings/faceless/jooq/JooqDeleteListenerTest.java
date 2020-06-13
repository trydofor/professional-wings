package pro.fessional.wings.faceless.jooq;

import lombok.Setter;
import org.jooq.BatchBindStep;
import org.jooq.DSLContext;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import pro.fessional.wings.faceless.WingsTestHelper;
import pro.fessional.wings.faceless.database.autogen.tables.Tst中文也分表Table;
import pro.fessional.wings.faceless.database.autogen.tables.records.Tst中文也分表Record;
import pro.fessional.wings.faceless.database.helper.JournalHelp;
import pro.fessional.wings.faceless.flywave.SchemaJournalManager;
import pro.fessional.wings.faceless.flywave.SchemaRevisionManager;

import java.time.LocalDateTime;
import java.util.Arrays;

import static pro.fessional.wings.faceless.WingsTestHelper.REVISION_TEST_V2;
import static pro.fessional.wings.faceless.convention.EmptyValue.DATE_TIME;

/**
 * @author trydofor
 * @since 2019-09-27
 */
@RunWith(SpringRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@ActiveProfiles("init")
@SpringBootTest(properties = {"debug = true", "spring.wings.trigger.journal-delete.enabled=true"})

public class JooqDeleteListenerTest {

    @Setter(onMethod = @__({@Autowired}))
    private SchemaRevisionManager revisionManager;
    @Setter(onMethod = @__({@Autowired}))
    private SchemaJournalManager journalManager;
    @Setter(onMethod = @__({@Autowired}))
    private DSLContext dsl;
    @Setter(onMethod = @__({@Autowired}))
    private JdbcTemplate tmpl;

    // >>=>🦁🦁🦁
    @Setter(onMethod = @__({@Autowired}))
    private WingsTestHelper wingsTestHelper;
    @Test
    public void test0𓃬清表重置() {
        wingsTestHelper.cleanAndInit();
    }
    //  🦁🦁🦁<=<<

    @Test
    public void test1𓃬升级表和触发器() {
        revisionManager.publishRevision(REVISION_TEST_V2, 0);
        journalManager.publishUpdate("tst_中文也分表", true, 0);
        journalManager.publishDelete("tst_中文也分表", true, 0);
        wingsTestHelper.note("没有错误就是正确");
    }

    @Test
    public void test2𓃬Helper𓃬查日志() {
        JournalHelp.deleteByIds(dsl, Tst中文也分表Table.Tst中文也分表, 12L, 1L, 2L);
        JournalHelp.deleteByIds(tmpl, "`tst_中文也分表`", 34L, 3L, 4L);
        wingsTestHelper.note(
                "检查日志，在delete前update，如下",
                "UPDATE `tst_中文也分表` SET commit_id=34, delete_dt=NOW()  WHERE id IN (3,4)",
                "DELETE FROM `tst_中文也分表`  WHERE id IN (3,4)"
        );
    }

    @Test
    public void test3𓃬JooqDsl𓃬查日志() {
        // 有效
        dsl.execute("DELETE FROM `tst_中文也分表` WHERE ID =5 AND COMMIT_ID = 5");
        dsl.execute("DELETE FROM `tst_中文也分表` WHERE commit_id = 6 AND id = 6");
        dsl.execute("DELETE FROM `tst_中文也分表` WHERE commit_id = 7 AND id = ?", 7L);

        Tst中文也分表Table t = Tst中文也分表Table.Tst中文也分表;
        dsl.deleteFrom(t).where(t.Id.eq(8L).and(t.CommitId.eq(8L))).execute();
        wingsTestHelper.note(
                "检查日志，id 等于 (5,6,7,8)的sql，先delete，再update，如下",
                "DELETE FROM `tst_中文也分表` WHERE ID =5 AND COMMIT_ID = 5",
                "UPDATE `tst_中文也分表` SET COMMIT_ID = 5 ,delete_dt = NOW() WHERE ID =5"
        );

        // 无效
        LocalDateTime now = LocalDateTime.now();
        dsl.batchDelete(
                new Tst中文也分表Record(9L, now, DATE_TIME, DATE_TIME, 9L, "", "")
        ).execute();

        BatchBindStep batch = dsl.batch(
                dsl.deleteFrom(t).where(t.Id.eq((Long) null).and(t.CommitId.eq((Long) null)))
        );
        batch.bind(10L, 10L);
        batch.bind(11L, 11L);
        batch.bind(12L, 12L);
        batch.bind(13L, 13L);
        int[] rs = batch.execute();
        System.out.println(Arrays.toString(rs));
        wingsTestHelper.note(
                "检查日志，id >= 9的sql，只有delete，如下",
                "delete from `tst_中文也分表` where `id` = ?"
        );
    }
}
