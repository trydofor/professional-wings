package pro.fessional.wings.faceless.jooq;

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
import pro.fessional.wings.faceless.convention.EmptyValue;
import pro.fessional.wings.faceless.database.autogen.tables.Tst中文也分表Table;
import pro.fessional.wings.faceless.database.autogen.tables.records.Tst中文也分表Record;
import pro.fessional.wings.faceless.database.helper.JournalHelp;
import pro.fessional.wings.faceless.flywave.SchemaJournalManager;
import pro.fessional.wings.faceless.flywave.SchemaRevisionManager;

import java.time.LocalDateTime;
import java.util.Arrays;

import static pro.fessional.wings.faceless.convention.EmptyValue.DATE_TIME;

/**
 * @author trydofor
 * @since 2019-09-27
 */
@RunWith(SpringRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@SpringBootTest
@ActiveProfiles("init")
public class TestJooqDeleteListener {

    private SchemaRevisionManager revi;
    private SchemaJournalManager jour;
    private DSLContext dsl;
    private JdbcTemplate tmpl;

    @Autowired
    public void setDsl(DSLContext dsl) {
        this.dsl = dsl;
    }

    @Autowired
    public void setTmpl(JdbcTemplate tmpl) {
        this.tmpl = tmpl;
    }

    @Autowired
    public void setRevi(SchemaRevisionManager revi) {
        this.revi = revi;
    }

    @Autowired
    public void setJour(SchemaJournalManager jour) {
        this.jour = jour;
    }

    @Test
    public void test1Init() {
        revi.publishRevision(20190521_02L, 0);
        jour.publishUpdate("tst_中文也分表", true, 0);
        jour.publishDelete("tst_中文也分表", true, 0);
    }


    @Test
    public void test2Help() {
        JournalHelp.deleteByIds(dsl, Tst中文也分表Table.Tst中文也分表, 12L, 1L, 2L);
        JournalHelp.deleteByIds(tmpl, "`tst_中文也分表`", 34L, 3L, 4L);
    }

    @Test
    public void test3Auto() {
        // 有效
        dsl.execute("DELETE FROM `tst_中文也分表` WHERE ID =5 AND COMMIT_ID = 5");
        dsl.execute("delete from `tst_中文也分表` where commit_id = 6 and id = 6");
        dsl.execute("delete from `tst_中文也分表` where commit_id = 7 and id = ?", 7L);

        Tst中文也分表Table t = Tst中文也分表Table.Tst中文也分表;
        dsl.deleteFrom(t).where(t.Id.eq(8L).and(t.CommitId.eq(8L))).execute();

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
    }
}
