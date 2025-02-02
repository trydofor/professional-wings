package pro.fessional.wings.faceless.service.journal.impl;

import com.alibaba.ttl.TransmittableThreadLocal;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.transaction.annotation.Propagation;
import pro.fessional.mirana.best.AssertCrud;
import pro.fessional.mirana.time.DateLocaling;
import pro.fessional.mirana.time.ThreadNow;
import pro.fessional.wings.faceless.convention.EmptyValue;
import pro.fessional.wings.faceless.database.helper.TransactionHelper;
import pro.fessional.wings.faceless.database.manual.single.modify.commitjournal.CommitJournalModify;
import pro.fessional.wings.faceless.service.journal.JournalService;
import pro.fessional.wings.faceless.service.lightid.BlockIdProvider;
import pro.fessional.wings.faceless.service.lightid.LightIdService;

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

/**
 * @author trydofor
 * @since 2019-09-11
 */
@RequiredArgsConstructor
@Slf4j
public class DefaultJournalService implements JournalService {

    public static final String SEQ_JOURNAL = "sys_commit_journal";

    /** no leak, for try-finally */
    private final TransmittableThreadLocal<Journal> context = new TransmittableThreadLocal<>();

    private final LightIdService lightIdService;
    private final BlockIdProvider blockIdProvider;
    private final CommitJournalModify journalModify;

    @Setter
    private Propagation propagation = Propagation.REQUIRES_NEW;

    /**
     * create journal by dummyLightId getAndIncrement
     */
    @Setter
    private AtomicLong dummyLightId = null;

    /**
     * <pre>
     * create new journal if the existing to alive,
     * * negative - use the old
     * * zero - new one every time
     * * positive - new one if older
     * </pre>
     */
    @Setter
    private int aliveSecond = 300;

    /**
     * For internal debugging purposes, insert records with a new transaction (REQUIRES_NEW) if not using a dummy connection.
     */
    @NotNull
    @ApiStatus.Internal
    public Journal create(long parentId, AtomicLong dummyId, long nowUtc, @NotNull String eventName, @Nullable String loginInfo, @Nullable String targetKey, @Nullable String otherInfo) {
        if (nowUtc <= 0) nowUtc = ThreadNow.millis();

        final long id = dummyId == null
            ? lightIdService.getId(SEQ_JOURNAL, blockIdProvider.getBlockId())
            : dummyId.getAndIncrement();

        final Journal journal = new Journal(id, DateLocaling.sysLdt(nowUtc), parentId, nowUtc, eventName,
            targetKey == null ? EmptyValue.VARCHAR : targetKey,
            loginInfo == null ? EmptyValue.VARCHAR : loginInfo,
            otherInfo == null ? EmptyValue.VARCHAR : otherInfo
        );

        if (dummyId == null) {
            TransactionHelper.template(propagation).executeWithoutResult(ignore -> {
                int rc = journalModify.insert(journal);
                AssertCrud.affectEq(rc, 1, "failed to insert Journal={}", journal);
            });
        }
        else {
            log.warn("dummyLightId id={}", id);
        }

        return journal;
    }

    /**
     * For internal debugging purposes, insert records with a new transaction (REQUIRES_NEW) if not using a dummy connection.
     * However, update the elapsed time using the default connection without an explicit transaction.
     */
    @NotNull
    @ApiStatus.Internal
    public <R> R submit(int aliveSd, AtomicLong dummyId, @NotNull String eventName, @Nullable String loginInfo, @Nullable String targetKey, @Nullable String otherInfo, @NotNull Function<Journal, R> commitSet) {
        long now = 0;
        long pid = 0;
        final Journal oldOne = context.get();
        if (oldOne != null) {
            if (aliveSd < 0) {
                return commitSet.apply(oldOne);
            }
            else if (aliveSd > 0) {
                now = ThreadNow.millis();
                long live = (now - oldOne.getCommitMs()) / 1000;
                if (live <= aliveSd) {
                    return commitSet.apply(oldOne);
                }
                else {
                    pid = oldOne.getId();
                    log.warn("renew timeout journal id={}, for alive={}, but live={}", pid, aliveSd, live);
                }
            }
        }

        final Journal newOne = create(pid, dummyId, now, eventName, loginInfo, targetKey, otherInfo);
        // Who created, who destroy
        context.set(newOne);
        try {
            return commitSet.apply(newOne);
        }
        finally {
            context.remove();
            elapse(newOne);
        }
    }

    /**
     * For internal debugging purposes, using the default connection without an explicit transaction, can ignore error.
     */
    @ApiStatus.Internal
    public long elapse(AtomicLong dummyId, @NotNull Journal journal) {
        final long cost = ThreadNow.millis() - journal.getCommitMs();

        if (dummyId == null) {
            try {
                // using the default connection without an explicit transaction.
                journalModify.elapse(cost, journal.getId());
            }
            catch (Exception e) {
                // noinspection StringConcatenationArgumentToLogCall
                log.warn("fail to update elapse=" + cost + ", id=" + journal.getId(), e);
            }
        }
        return cost;
    }

    @Override
    public long elapse(@NotNull Journal journal) {
        return elapse(dummyLightId, journal);
    }

    @Override
    @NotNull
    public Journal create(long parentId, @NotNull String eventName, @Nullable String loginInfo, @Nullable String targetKey, @Nullable String otherInfo) {
        return create(parentId, dummyLightId, 0, eventName, loginInfo, targetKey, otherInfo);
    }

    @NotNull
    @Override
    public <R> R submit(@NotNull String eventName, @Nullable String loginInfo, @Nullable String targetKey, @Nullable String otherInfo, @NotNull Function<Journal, R> commitSet) {
        return submit(aliveSecond, dummyLightId, eventName, loginInfo, targetKey, otherInfo, commitSet);
    }
}
