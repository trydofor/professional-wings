package pro.fessional.wings.slardar.context;

import com.alibaba.ttl.TransmittableThreadLocal;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.TimeZone;

import static pro.fessional.wings.slardar.context.TerminalContext.Context.Guest;

/**
 * 比spring security context有更好的线程继承性
 *
 * @author trydofor
 * @since 2019-11-25
 */
public class TerminalContext {

    public static final TransmittableThreadLocal<Context> context = new TransmittableThreadLocal<>();

    @NotNull
    public static Context get() {
        Context ctx = TerminalContext.context.get();
        return ctx == null ? Context.NULL : ctx;
    }

    @NotNull
    public static Context login(long userId, @Nullable Locale locale, @Nullable TimeZone timeZone, @Nullable String ip, @Nullable String agent) {
        Context ctx = new Context(userId, locale, timeZone, ip, agent);
        context.set(ctx);
        return ctx;
    }

    @NotNull
    public static Context guest(@Nullable Locale locale, @Nullable TimeZone timeZone, @Nullable String ip, @Nullable String agent) {
        Context ctx = new Context(Guest, locale, timeZone, ip, agent);
        context.set(ctx);
        return ctx;
    }

    public static void clear() {
        context.remove();
    }

    @Data
    public static class Context {
        public static final long Guest = Integer.MIN_VALUE;
        public static final Context NULL = new Context(Guest, null, null, null, null);

        private final long userId;
        private final Locale locale;
        private final TimeZone timeZone;
        private final String remoteIp;
        private final String agentInfo;

        public Context(long userId, Locale locale, TimeZone timeZone, String remoteIp, String agentInfo) {
            this.userId = userId;
            this.locale = locale == null ? Locale.getDefault() : locale;
            this.timeZone = timeZone == null ? TimeZone.getDefault() : timeZone;
            this.remoteIp = remoteIp == null ? "" : remoteIp;
            this.agentInfo = agentInfo == null ? "" : agentInfo;
        }

        /**
         * userId == Guest
         *
         * @return 是否等于Guest
         * @see #Guest
         */
        public boolean isGuest() {
            return userId == Guest;
        }

        /**
         * userId != Guest
         *
         * @return Login
         * @see #Guest
         */
        public boolean isLogin() {
            return userId != Guest;
        }
    }
}
