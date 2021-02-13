package pro.fessional.wings.slardar.security.trek;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import pro.fessional.wings.silencer.context.WingsI18nContext;

/**
 * @author trydofor
 * @since 2019-11-25
 */
public class WingsTerminalContext {

    public static final Context NULL = new Context();
    public static final ThreadLocal<Context> context = new ThreadLocal<>();

    @NotNull
    public static Context get() {
        Context ctx = WingsTerminalContext.context.get();
        return ctx == null ? NULL : ctx;
    }

    public static Context set(WingsI18nContext i18n, String ip, String agent) {
        Context ctx = new Context();
        ctx.i18nContext = i18n;
        ctx.remoteIp = ip;
        ctx.agentInfo = agent;
        context.set(ctx);
        return ctx;
    }

    public static void clear() {
        context.remove();
    }

    @Getter
    public static class Context {
        private String remoteIp;
        private String agentInfo;
        private WingsI18nContext i18nContext = WingsI18nContext.NULL;
    }
}
