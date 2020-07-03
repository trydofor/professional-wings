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
import pro.fessional.wings.slardar.security.SecurityContextUtil;

/**
 * @author trydofor
 * @since 2019-08-15
 */
@Service("journalService")
public class SecurityJournalService extends DefaultJournalService {

    public SecurityJournalService(LightIdService lightIdService,
                                  BlockIdProvider blockIdProvider,
                                  CommitJournalModify journalModify) {
        super(lightIdService, blockIdProvider, journalModify);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public @NotNull Journal commit(@NotNull String eventName, @Nullable String loginInfo, @Nullable String targetKey, @Nullable String otherInfo) {
        if (loginInfo == null || loginInfo.isEmpty()) {
            WingsUserDetail principal = SecurityContextUtil.getPrincipal();
            loginInfo = principal == null ? "" : principal.toLoginInfo();
        }
        return super.commit(eventName, loginInfo, targetKey, otherInfo);
    }
}
