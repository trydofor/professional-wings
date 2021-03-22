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
import pro.fessional.wings.slardar.context.SecurityContextUtil;

import java.util.function.Function;

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
    public @NotNull <R> R submit(@NotNull String eventName, @Nullable String loginInfo, @Nullable String targetKey, @Nullable String otherInfo, @NotNull Function<Journal, R> commitSet) {
        if (loginInfo == null || loginInfo.isEmpty()) {
            Object principal = SecurityContextUtil.getPrincipal();
            if (principal instanceof WingsExampleUserDetails) {
                loginInfo = ((WingsExampleUserDetails) principal).toLoginInfo();
            } else if (principal instanceof CharSequence) {
                loginInfo = principal.toString();
            }
        }
        return super.submit(eventName, loginInfo, targetKey, otherInfo, commitSet);
    }
}
