package pro.fessional.wings.faceless.service;

import org.jetbrains.annotations.Nullable;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author trydofor
 * @since 2023-03-09
 */
public interface TransactionalClauseService {

    @Transactional
    long createIntOneTx(AtomicLong oid, boolean error);

    @Transactional
    int increaseIntTx(long id, boolean error);

    @Transactional
    int deleteTx(long id, boolean error);

    long createIntOne(AtomicLong oid, boolean error);

    int increaseInt(long id, boolean error);

    int delete(long id, boolean error);

    @Nullable
    Integer selectInt(long id);

    long getNextSequence();
}
