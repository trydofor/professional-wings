package pro.fessional.wings.faceless.service.journal.impl;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pro.fessional.wings.faceless.database.manual.single.modify.commitjournal.CommitJournalModify;
import pro.fessional.wings.faceless.service.journal.JournalService;
import pro.fessional.wings.faceless.service.lightid.BlockIdProvider;
import pro.fessional.wings.faceless.service.lightid.LightIdService;

import java.time.LocalDateTime;

/**
 * @author trydofor
 * @since 2019-09-11
 */
@RequiredArgsConstructor
public class DefaultJournalService implements JournalService {

    public static final String SEQ_JOURNAL = "sys_commit_journal";
    private final LightIdService lightIdService;
    private final BlockIdProvider blockIdProvider;
    private final CommitJournalModify journalModify;

    @NotNull
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Journal commit(@NotNull String eventName, @Nullable String loginInfo, @Nullable String targetKey, @Nullable String otherInfo) {

        Journal journal = new Journal();
        journal.setCommitDt(LocalDateTime.now());
        journal.setEventName(eventName);
        journal.setLoginInfo(loginInfo);
        journal.setTargetKey(targetKey);
        journal.setOtherInfo(otherInfo);

        long id = lightIdService.getId(SEQ_JOURNAL, blockIdProvider.getBlockId());
        journal.setCommitId(id);

        int rc = journalModify.insert(journal);
        if (rc != 1) {
            throw new IllegalStateException("failed to insert Journal=" + journal);
        }
        return journal;
    }
}
