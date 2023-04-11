package pro.fessional.wings.faceless.service;

import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author trydofor
 * @since 2023-03-09
 */
public interface TransactionalBusinessService {

    @Transactional
    void declarativeTx(AtomicLong id, boolean insErr, boolean updErr, boolean delError);

    void programmaticTx(AtomicLong id, boolean insErr, boolean updErr, boolean delError, boolean rollback);

    void withoutTx(AtomicLong id, boolean insErr, boolean updErr, boolean delError);

}
