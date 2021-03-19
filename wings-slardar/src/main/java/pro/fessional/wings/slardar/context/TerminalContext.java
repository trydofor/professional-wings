package pro.fessional.wings.slardar.context;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.TimeZone;

/**
 * @author trydofor
 * @since 2019-11-25
 */
public class TerminalContext {

    public static final Context NULL = new Context(null, null, null, null);
    public static final ThreadLocal<Context> context = new ThreadLocal<>();

    @NotNull
    public static Context get() {
        Context ctx = TerminalContext.context.get();
        return ctx == null ? NULL : ctx;
    }

    @NotNull
    public static Context set(@Nullable Locale locale, @Nullable TimeZone timeZone, @Nullable String ip, @Nullable String agent) {
        Context ctx = new Context(locale, timeZone, ip, agent);
        context.set(ctx);
        return ctx;
    }

    public static void clear() {
        context.remove();
    }

    @Data
    public static class Context {
        private final Locale locale;
        private final TimeZone timeZone;
        private final String remoteIp;
        private final String agentInfo;

        public Context(Locale locale, TimeZone timeZone, String remoteIp, String agentInfo) {
            this.locale = locale == null ? Locale.getDefault() : locale;
            this.timeZone = timeZone == null ? TimeZone.getDefault() : timeZone;
            this.remoteIp = remoteIp == null ? "" : remoteIp;
            this.agentInfo = agentInfo == null ? "" : agentInfo;
        }
    }
}
