package pro.fessional.wings.warlock.service.other;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pro.fessional.wings.faceless.database.manual.single.modify.commitjournal.CommitJournalModify;
import pro.fessional.wings.faceless.service.journal.impl.DefaultJournalService;
import pro.fessional.wings.faceless.service.lightid.BlockIdProvider;
import pro.fessional.wings.faceless.service.lightid.LightIdService;
import pro.fessional.wings.slardar.context.TerminalContext;

import java.util.function.Function;

/**
 * @author trydofor
 * @since 2019-08-15
 */
public class TerminalJournalService extends DefaultJournalService {

    public TerminalJournalService(LightIdService ids, BlockIdProvider bid, CommitJournalModify mod) {
        super(ids, bid, mod);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public @NotNull <R> R submit(@NotNull String eventName, @Nullable String loginInfo, @Nullable String targetKey, @Nullable String otherInfo, @NotNull Function<Journal, R> commitSet) {
        if (loginInfo == null || loginInfo.isEmpty()) {
            final TerminalContext.Context ctx = TerminalContext.get(false);
            if (ctx.asLogin()) {
                loginInfo = ctx.toString();
            }
        }
        return super.submit(eventName, loginInfo, targetKey, otherInfo, commitSet);
    }
}
