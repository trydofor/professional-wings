package pro.fessional.wings.example.service.security;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pro.fessional.wings.faceless.database.manual.single.modify.commitjournal.CommitJournalModify;
import pro.fessional.wings.faceless.service.journal.impl.DefaultJournalService;
import pro.fessional.wings.faceless.service.lightid.BlockIdProvider;
import pro.fessional.wings.faceless.service.lightid.LightIdService;

/**
 * @author trydofor
 * @since 2019-08-15
 */
@Service
public class SecurityJournalService extends DefaultJournalService {

    public SecurityJournalService(LightIdService lightIdService,
                                  BlockIdProvider blockIdProvider,
                                  CommitJournalModify journalModify) {
        super(lightIdService, blockIdProvider, journalModify);
    }

    @NotNull
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Journal commit(@NotNull String eventName,
                          @Nullable String targetKey,
                          @Nullable String otherInfo) {

//        SecurityContextUtil.getDetails()

        String loginInfo = "";
        return super.commit(eventName, loginInfo, targetKey, otherInfo);
    }

}
