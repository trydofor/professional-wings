package pro.fessional.wings.faceless.jooq;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pro.fessional.mirana.id.LightIdBufferedProvider;
import pro.fessional.wings.faceless.flywave.SchemaRevisionManager;
import pro.fessional.wings.faceless.service.TransactionalBusinessService;
import pro.fessional.wings.faceless.service.TransactionalClauseService;
import pro.fessional.wings.faceless.util.FlywaveRevisionScanner;

import java.util.concurrent.atomic.AtomicLong;

import static pro.fessional.wings.faceless.WingsTestHelper.REVISION_TEST_V1;

/**
 * @author trydofor
 * @since 2023-03-09
 */
@SpringBootTest
@TestMethodOrder(MethodOrderer.MethodName.class)
@Slf4j
public class TransactionalServiceTest {

    @Setter(onMethod_ = {@Autowired})
    protected SchemaRevisionManager schemaRevisionManager;

    @Setter(onMethod_ = {@Autowired})
    protected TransactionalClauseService transactionalClauseService;

    @Setter(onMethod_ = {@Autowired})
    protected TransactionalBusinessService transactionalBusinessService;

    @Setter(onMethod_ = {@Autowired})
    protected LightIdBufferedProvider lightIdBufferedProvider;

    @Test
    public void test0Init() {
        val sqls = FlywaveRevisionScanner.scanMaster();
        schemaRevisionManager.checkAndInitSql(sqls, 0, false);
        schemaRevisionManager.publishRevision(REVISION_TEST_V1, 0);
    }

    @Test
    public void testDeclarativeTx() {
        final AtomicLong id = new AtomicLong(-1);
        lightIdBufferedProvider.setFixCount(1);
        // insert
        try {
            transactionalBusinessService.declarativeTx(id, true, false, false);
            Assertions.fail("should exception in create");
        }
        catch (Exception e) {
            log.info("insert failure", e);
        }
        final long ild = id.get();
        final long nxt = transactionalClauseService.getNextSequence();
        Assertions.assertEquals(nxt, ild + 1);

        final Integer ic = transactionalClauseService.selectInt(id.get());
        Assertions.assertNull(ic);

        // update
        try {
            transactionalBusinessService.declarativeTx(id, false, true, false);
            Assertions.fail("should exception in update");
        }
        catch (Exception e) {
            log.info("update failure", e);
        }

        // rollback
        final Integer uc = transactionalClauseService.selectInt(id.get());
        Assertions.assertNull(uc);

        // delete
        try {
            transactionalBusinessService.declarativeTx(id, false, false, true);
            Assertions.fail("should exception in delete");
        }
        catch (Exception e) {
            log.info("delete failure", e);
        }

        // rollback
        final Integer dc = transactionalClauseService.selectInt(id.get());
        Assertions.assertNull(dc);

        // normal
        try {
            transactionalBusinessService.declarativeTx(id, false, false, false);
        }
        catch (Exception e) {
            Assertions.fail("should no exception");
        }

        final Integer ac = transactionalClauseService.selectInt(id.get());
        Assertions.assertNull(ac);
        lightIdBufferedProvider.setFixCount(0);
    }

    @Test
    public void testWithoutTx() {
        lightIdBufferedProvider.setFixCount(1);
        final AtomicLong id = new AtomicLong(-1);
        // insert
        try {
            transactionalBusinessService.withoutTx(id, true, false, false);
            Assertions.fail("should exception in create");
        }
        catch (Exception e) {
            log.info("insert failure", e);
        }
        final long ild = id.get();
        final long nxt = transactionalClauseService.getNextSequence();
        Assertions.assertEquals(nxt, ild + 1);

        final Integer ic = transactionalClauseService.selectInt(id.get());
        Assertions.assertEquals(1, ic);

        // update
        try {
            transactionalBusinessService.withoutTx(id, false, true, false);
            Assertions.fail("should exception in update");
        }
        catch (Exception e) {
            log.info("update failure", e);
        }

        final Integer uc = transactionalClauseService.selectInt(id.get());
        Assertions.assertEquals(2, uc);

        // delete
        try {
            transactionalBusinessService.withoutTx(id, false, false, true);
            Assertions.fail("should exception in delete");
        }
        catch (Exception e) {
            log.info("delete failure", e);
        }

        final Integer dc = transactionalClauseService.selectInt(id.get());
        Assertions.assertNull(dc);

        // normal
        try {
            transactionalBusinessService.withoutTx(id, false, false, false);
        }
        catch (Exception e) {
            Assertions.fail("should no exception");
        }

        final Integer ac = transactionalClauseService.selectInt(id.get());
        Assertions.assertNull(ac);
        lightIdBufferedProvider.setFixCount(0);
    }

    @Test
    public void testProgrammaticTx() {
        lightIdBufferedProvider.setFixCount(1);
        testProgrammaticTx(false);
        testProgrammaticTx(true);
        lightIdBufferedProvider.setFixCount(0);
    }

    public void testProgrammaticTx(boolean rollback) {
        final AtomicLong id = new AtomicLong(-1);
        // insert
        try {
            transactionalBusinessService.programmaticTx(id, true, false, false, rollback);
            Assertions.fail("should exception in create");
        }
        catch (Exception e) {
            log.info("insert failure", e);
        }
        final long ild = id.get();
        final long nxt = transactionalClauseService.getNextSequence();
        Assertions.assertEquals(nxt, ild + 1);

        // rollback
        final Integer ic = transactionalClauseService.selectInt(id.get());
        Assertions.assertNull(ic);

        // update
        try {
            transactionalBusinessService.programmaticTx(id, false, true, false, rollback);
            Assertions.fail("should exception in update");
        }
        catch (Exception e) {
            log.info("update failure", e);
        }
        // rollback
        final Integer uc = transactionalClauseService.selectInt(id.get());
        Assertions.assertNull(uc);

        // delete
        try {
            transactionalBusinessService.programmaticTx(id, false, false, true, rollback);
            Assertions.fail("should exception in delete");
        }
        catch (Exception e) {
            log.info("delete failure", e);
        }
        // rollback
        final Integer dc = transactionalClauseService.selectInt(id.get());
        Assertions.assertNull(dc);

        // nomal
        try {
            transactionalBusinessService.programmaticTx(id, false, false, false, rollback);
        }
        catch (Exception e) {
            Assertions.fail("should no exception");
        }

        final Integer ac = transactionalClauseService.selectInt(id.get());
        Assertions.assertNull(ac);
    }
}
