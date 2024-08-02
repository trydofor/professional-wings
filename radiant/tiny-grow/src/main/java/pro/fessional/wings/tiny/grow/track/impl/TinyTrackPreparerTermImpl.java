package pro.fessional.wings.tiny.grow.track.impl;

import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Service;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;
import pro.fessional.wings.slardar.context.TerminalContext;
import pro.fessional.wings.tiny.grow.track.TinyTrackService;
import pro.fessional.wings.tiny.grow.track.TinyTracking;

import static pro.fessional.wings.slardar.context.TerminalAttribute.TerminalAddr;
import static pro.fessional.wings.slardar.context.TerminalAttribute.TerminalAgent;

/**
 * @author trydofor
 * @since 2024-08-01
 */
@Service
@ConditionalWingsEnabled
@ConditionalOnClass(TerminalContext.class)
public class TinyTrackPreparerTermImpl implements TinyTrackService.Preparer {

    @Override
    public void prepare(@NotNull TinyTracking tracking) {
        final TerminalContext.Context ctx = TerminalContext.get(false);
        if (ctx.isNull()) return;

        tracking.addEnv("locale", ctx.getLocale().toLanguageTag());
        tracking.addEnv("zoneid", ctx.getZoneId().getId());
        long id = ctx.getUserId();
        tracking.addEnv("userId", id);

        if (id > 0) {
            tracking.addEnv("authType", ctx.getAuthType().name());
            tracking.addEnv("username", ctx.getUsername());
        }

        String adr = ctx.getTerminal(TerminalAddr);
        if (adr != null) tracking.addEnv("addr", id);
        String agt = ctx.getTerminal(TerminalAgent);
        if (agt != null) tracking.addEnv("agent", agt);
    }
}
