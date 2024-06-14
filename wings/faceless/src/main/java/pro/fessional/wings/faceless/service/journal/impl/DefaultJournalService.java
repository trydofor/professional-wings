package pro.fessional.wings.faceless.service.journal.impl;

import com.alibaba.ttl.TransmittableThreadLocal;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.transaction.annotation.Propagation;
import pro.fessional.mirana.time.ThreadNow;
import pro.fessional.wings.faceless.convention.EmptyValue;
import pro.fessional.wings.faceless.database.helper.DaoAssert;
import pro.fessional.wings.faceless.database.helper.TransactionHelper;
import pro.fessional.wings.faceless.database.manual.single.modify.commitjournal.CommitJournalModify;
import pro.fessional.wings.faceless.service.journal.JournalService;
import pro.fessional.wings.faceless.service.lightid.BlockIdProvider;
import pro.fessional.wings.faceless.service.lightid.LightIdService;

import java.util.Objects;
import java.util.function.Function;

/**
 * @author trydofor
 * @since 2019-09-11
 */
@RequiredArgsConstructor
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
     * programmatic Propagation.REQUIRES_NEW
     */
    @NotNull
    @Override
    public <R> R submit(@NotNull String eventName, @Nullable String loginInfo, @Nullable String targetKey, @Nullable String otherInfo, @NotNull Function<Journal, R> commitSet) {
        // not null
        R result = TransactionHelper.template(propagation).execute(ignore ->
            execute(eventName, loginInfo, targetKey, otherInfo, commitSet)
        );
        return Objects.requireNonNull(result);
    }

    @NotNull
    private <R> R execute(@NotNull String eventName, @Nullable String loginInfo, @Nullable String targetKey, @Nullable String otherInfo, @NotNull Function<Journal, R> commitSet) {
        final Journal commit = context.get();
        if (commit == null) {
            long id = lightIdService.getId(SEQ_JOURNAL, blockIdProvider.getBlockId());

            Journal journal = new Journal(id, ThreadNow.localDateTime(), eventName,
                targetKey == null ? EmptyValue.VARCHAR : targetKey,
                loginInfo == null ? EmptyValue.VARCHAR : loginInfo,
                otherInfo == null ? EmptyValue.VARCHAR : otherInfo
            );

            int rc = journalModify.insert(journal);
            DaoAssert.assertEq1(rc, "failed to insert Journal={}", journal);

            // Who created, who destroy
            context.set(journal);
            try {
                return commitSet.apply(journal);
            }
            finally {
                context.remove();
            }
        }
        else {
            return commitSet.apply(commit);
        }
    }
}
