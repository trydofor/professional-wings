package pro.fessional.wings.faceless.app.service.impl;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import pro.fessional.wings.faceless.app.service.TestTransactionalClauseService;
import pro.fessional.wings.faceless.app.service.TestTransactionalManageService;
import pro.fessional.wings.faceless.database.helper.DaoAssert;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author trydofor
 * @since 2023-03-10
 */
@Service
@Slf4j
public class TestTestTransactionalManageServiceImpl implements TestTransactionalManageService {

    @Setter(onMethod_ = {@Autowired})
    protected TestTransactionalClauseService testTransactionalClauseService;

    @Setter(onMethod_ = {@Autowired})
    protected PlatformTransactionManager transactionManager;

    @Override
    public void declarativeTx(AtomicLong oid, boolean insErr, boolean updErr, boolean delError) {
        final long id = testTransactionalClauseService.createIntOneTx(oid, insErr);
        final int uc = testTransactionalClauseService.increaseIntTx(id, updErr);
        DaoAssert.assertGe1(uc, "must update one");
        final int dc = testTransactionalClauseService.deleteTx(id, delError);
        DaoAssert.assertEq1(dc, "must delete one");
    }

    @Override
    public void programmaticTx(AtomicLong oid, boolean insErr, boolean updErr, boolean delError, boolean rollback) {
        final DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
        definition.setIsolationLevel(TransactionDefinition.ISOLATION_REPEATABLE_READ);
        definition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        final TransactionStatus programmaticTx = transactionManager.getTransaction(definition);

        // Perform transactional operations using the programmatic transaction
        try {
            final long id = testTransactionalClauseService.createIntOneTx(oid, insErr);
            final int uc = testTransactionalClauseService.increaseIntTx(id, updErr);
            DaoAssert.assertGe1(uc, "must update one");
            final int dc = testTransactionalClauseService.deleteTx(id, delError);
            DaoAssert.assertEq1(dc, "must delete one");

            if (rollback) {
                log.info("rollback programmatic manual");
                transactionManager.rollback(programmaticTx);
            }
            else {
                log.info("commit programmatic manual");
                transactionManager.commit(programmaticTx);
            }
        }
        catch (Exception ex) {
//            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.info("rollback programmatic exception");
            transactionManager.rollback(programmaticTx);
            throw ex;
        }
    }

    @Override
    public void withoutTx(AtomicLong oid, boolean insErr, boolean updErr, boolean delError) {
        final long id = testTransactionalClauseService.createIntOne(oid, insErr);
        final int uc = testTransactionalClauseService.increaseInt(id, updErr);
        DaoAssert.assertGe1(uc, "must update one");
        final int dc = testTransactionalClauseService.delete(id, delError);
        DaoAssert.assertEq1(dc, "must delete one");
    }


}
