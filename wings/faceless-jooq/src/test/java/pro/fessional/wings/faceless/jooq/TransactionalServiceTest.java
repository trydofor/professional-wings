package pro.fessional.wings.faceless.jooq;

import io.qameta.allure.TmsLink;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.sql.init.dependency.DependsOnDatabaseInitialization;
import org.springframework.boot.test.context.SpringBootTest;
import pro.fessional.mirana.id.LightIdBufferedProvider;
import pro.fessional.wings.faceless.app.service.TestTransactionalClauseService;
import pro.fessional.wings.faceless.app.service.TestTransactionalManageService;
import pro.fessional.wings.faceless.flywave.SchemaRevisionManager;
import pro.fessional.wings.faceless.util.FlywaveRevisionScanner;
import pro.fessional.wings.testing.faceless.database.TestingDatabaseHelper;

import java.util.concurrent.atomic.AtomicLong;

import static pro.fessional.wings.faceless.flywave.WingsRevision.V90_22_0601_01_TestSchema;

/**
 * @author trydofor
 * @since 2023-03-09
 */
@SpringBootTest
@DependsOnDatabaseInitialization
@TestMethodOrder(MethodOrderer.MethodName.class)
@Slf4j
public class TransactionalServiceTest {

    @Setter(onMethod_ = {@Autowired})
    protected SchemaRevisionManager schemaRevisionManager;

    @Setter(onMethod_ = {@Autowired})
    protected TestTransactionalClauseService testTransactionalClauseService;

    @Setter(onMethod_ = {@Autowired})
    protected TestTransactionalManageService testTransactionalManageService;

    @Setter(onMethod_ = {@Autowired})
    protected LightIdBufferedProvider lightIdBufferedProvider;

    @Setter(onMethod_ = {@Autowired})
    protected TestingDatabaseHelper testingDatabaseHelper;

    @Test
    @TmsLink("C12108")
    public void test0Init() {
        testingDatabaseHelper.cleanTable();
        var sqls = FlywaveRevisionScanner.scanMaster();
        schemaRevisionManager.checkAndInitSql(sqls, 0, false);
        schemaRevisionManager.publishRevision(V90_22_0601_01_TestSchema.revision(), 0);
    }

    @Test
    @TmsLink("C12109")
    public void testDeclarativeTx() {
        final AtomicLong id = new AtomicLong(-1);
        lightIdBufferedProvider.setFixCount(1);
        // insert
        try {
            testTransactionalManageService.declarativeTx(id, true, false, false);
            Assertions.fail("should exception in create");
        }
        catch (Exception e) {
            log.info("should insert failure", e);
        }
        final long ild = id.get();
        final long nxt = testTransactionalClauseService.getNextSequence();
        Assertions.assertEquals(nxt, ild + 1);

        final Integer ic = testTransactionalClauseService.selectInt(id.get());
        Assertions.assertNull(ic);

        // update
        try {
            testTransactionalManageService.declarativeTx(id, false, true, false);
            Assertions.fail("should exception in update");
        }
        catch (Exception e) {
            log.info("should update failure", e);
        }

        // rollback
        final Integer uc = testTransactionalClauseService.selectInt(id.get());
        Assertions.assertNull(uc);

        // delete
        try {
            testTransactionalManageService.declarativeTx(id, false, false, true);
            Assertions.fail("should exception in delete");
        }
        catch (Exception e) {
            log.info("should delete failure", e);
        }

        // rollback
        final Integer dc = testTransactionalClauseService.selectInt(id.get());
        Assertions.assertNull(dc);

        // normal
        try {
            testTransactionalManageService.declarativeTx(id, false, false, false);
        }
        catch (Exception e) {
            Assertions.fail("should no exception");
        }

        final Integer ac = testTransactionalClauseService.selectInt(id.get());
        Assertions.assertNull(ac);
        lightIdBufferedProvider.setFixCount(0);
    }

    @Test
    @TmsLink("C12110")
    public void testWithoutTx() {
        lightIdBufferedProvider.setFixCount(1);
        final AtomicLong id = new AtomicLong(-1);
        // insert
        try {
            testTransactionalManageService.withoutTx(id, true, false, false);
            Assertions.fail("should exception in create");
        }
        catch (Exception e) {
            log.info("should insert failure", e);
        }
        final long ild = id.get();
        final long nxt = testTransactionalClauseService.getNextSequence();
        Assertions.assertEquals(nxt, ild + 1);

        final Integer ic = testTransactionalClauseService.selectInt(id.get());
        Assertions.assertEquals(1, ic);

        // update
        try {
            testTransactionalManageService.withoutTx(id, false, true, false);
            Assertions.fail("should exception in update");
        }
        catch (Exception e) {
            log.info("should update failure", e);
        }

        final Integer uc = testTransactionalClauseService.selectInt(id.get());
        Assertions.assertEquals(2, uc);

        // delete
        try {
            testTransactionalManageService.withoutTx(id, false, false, true);
            Assertions.fail("should exception in delete");
        }
        catch (Exception e) {
            log.info("should delete failure", e);
        }

        final Integer dc = testTransactionalClauseService.selectInt(id.get());
        Assertions.assertNull(dc);

        // normal
        try {
            testTransactionalManageService.withoutTx(id, false, false, false);
        }
        catch (Exception e) {
            Assertions.fail("should no exception");
        }

        final Integer ac = testTransactionalClauseService.selectInt(id.get());
        Assertions.assertNull(ac);
        lightIdBufferedProvider.setFixCount(0);
    }

    @Test
    @TmsLink("C12111")
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
            testTransactionalManageService.programmaticTx(id, true, false, false, rollback);
            Assertions.fail("should exception in create");
        }
        catch (Exception e) {
            log.info("should insert failure", e);
        }
        final long ild = id.get();
        final long nxt = testTransactionalClauseService.getNextSequence();
        Assertions.assertEquals(nxt, ild + 1);

        // rollback
        final Integer ic = testTransactionalClauseService.selectInt(id.get());
        Assertions.assertNull(ic);

        // update
        try {
            testTransactionalManageService.programmaticTx(id, false, true, false, rollback);
            Assertions.fail("should exception in update");
        }
        catch (Exception e) {
            log.info("should update failure", e);
        }
        // rollback
        final Integer uc = testTransactionalClauseService.selectInt(id.get());
        Assertions.assertNull(uc);

        // delete
        try {
            testTransactionalManageService.programmaticTx(id, false, false, true, rollback);
            Assertions.fail("should exception in delete");
        }
        catch (Exception e) {
            log.info("should delete failure", e);
        }
        // rollback
        final Integer dc = testTransactionalClauseService.selectInt(id.get());
        Assertions.assertNull(dc);

        // nomal
        try {
            testTransactionalManageService.programmaticTx(id, false, false, false, rollback);
        }
        catch (Exception e) {
            Assertions.fail("should no exception");
        }

        final Integer ac = testTransactionalClauseService.selectInt(id.get());
        Assertions.assertNull(ac);
    }
}
