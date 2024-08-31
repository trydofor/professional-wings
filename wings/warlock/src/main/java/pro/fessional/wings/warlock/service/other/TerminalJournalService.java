package pro.fessional.wings.warlock.service.other;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pro.fessional.mirana.text.JsonTemplate;
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
    @NotNull
    public Journal create(long parentId, @NotNull String eventName, @Nullable String loginInfo, @Nullable String targetKey, @Nullable String otherInfo) {
        return super.create(parentId, eventName, terminal(loginInfo), targetKey, otherInfo);
    }

    @Override
    @NotNull
    public <R> R submit(@NotNull String eventName, @Nullable String loginInfo, @Nullable String targetKey, @Nullable String otherInfo, @NotNull Function<Journal, R> commitSet) {
        return super.submit(eventName, terminal(loginInfo), targetKey, otherInfo, commitSet);
    }

    private String terminal(String loginInfo) {
        if (loginInfo == null || loginInfo.isBlank()) {
            final TerminalContext.Context ctx = TerminalContext.get(false);
            if (!ctx.isNull()) {
                loginInfo = JsonTemplate.obj(obj -> {
                    obj.putVal("userId", ctx.getUserId());
                    obj.putVal("locale", ctx.getLocale().toLanguageTag());
                    obj.putVal("zoneid", ctx.getZoneId().getId());
                    obj.putVal("authType", ctx.getAuthType().name());
                    obj.putVal("username", ctx.getUsername());
                });
            }
        }

        return loginInfo;
    }
}
