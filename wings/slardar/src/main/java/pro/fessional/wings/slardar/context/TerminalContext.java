package pro.fessional.wings.slardar.context;

import com.alibaba.ttl.TransmittableThreadLocal;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.ZoneId;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.function.Consumer;

import static pro.fessional.wings.slardar.context.TerminalContext.Context.Guest;

/**
 * 在service层使用，TransmittableThreadLocal有自动的线程继承性。
 * 由 TerminalInterceptor.preHandle 设值，afterCompletion清理。
 * 注：无 WeakReference Leak，因static及Interceptor清理。
 *
 * @author trydofor
 * @since 2019-11-25
 */
public class TerminalContext {

    /** no leak, for static and Interceptor clean */
    private static final TransmittableThreadLocal<Context> context = new TransmittableThreadLocal<>();
    private static final HashMap<String, Consumer<Context>> threadLocalListener = new HashMap<>();

    private static volatile boolean active;
    private static volatile ZoneId defaultZoneId = ZoneId.systemDefault();
    private static volatile Locale defaultLocale = Locale.getDefault();

    /**
     * 是否处于激活状态，可以正确使用
     */
    public static boolean isActive() {
        return active;
    }

    /**
     * 设置激活状态，正确使用与否
     */

    public static void setActive(boolean b) {
        active = b;
    }

    /**
     * 默认时区
     */
    public static ZoneId getDefaultZoneId() {
        return defaultZoneId;
    }

    /**
     * 设置默认时区
     */
    public static void setDefaultZoneId(ZoneId defaultZoneId) {
        TerminalContext.defaultZoneId = defaultZoneId;
    }

    /**
     * 默认语言
     */
    public static Locale getDefaultLocale() {
        return defaultLocale;
    }

    /**
     * 设置默认语言
     */
    public static void setDefaultLocale(Locale defaultLocale) {
        TerminalContext.defaultLocale = defaultLocale;
    }

    /**
     * 设置的login和logout的THreadLocal监听，context为null时，为logout，否则为login
     *
     * @param name     名字
     * @param listener 监听器
     * @return null或旧值
     */
    public static Consumer<Context> putThreadLocalListener(String name, Consumer<Context> listener) {
        return threadLocalListener.put(name, listener);
    }

    /**
     * 移除监听器
     *
     * @param name 名字
     * @return null或旧值
     */
    public static Consumer<Context> delThreadLocalListener(String name) {
        return threadLocalListener.remove(name);
    }

    /**
     * 仅登录的上下文，若未登录则抛异常
     */
    @NotNull
    public static Context get() {
        return get(true);
    }

    /**
     * 未登录用户以Guest返回，还是抛异常，
     *
     * @param onlyLogin 是否仅登录用户，否则包括Guest
     * @return 上下文
     */
    @NotNull
    public static Context get(boolean onlyLogin) {
        Context ctx = TerminalContext.context.get();
        if (ctx == null) ctx = Context.NULL;
        if (onlyLogin && ctx.isGuest()) {
            throw new IllegalStateException("must login user");
        }
        return ctx;
    }

    @NotNull
    public static Context login(long userId, @Nullable Locale locale, @Nullable TimeZone timeZone, @Nullable String ip, @Nullable String agent) {
        Context ctx = new Context(userId, locale, timeZone, ip, agent);
        login(ctx);
        return ctx;
    }

    @NotNull
    public static Context guest(@Nullable Locale locale, @Nullable TimeZone timeZone, @Nullable String ip, @Nullable String agent) {
        Context ctx = new Context(Guest, locale, timeZone, ip, agent);
        login(ctx);
        return ctx;
    }

    @NotNull
    public static Context login(long userId, @Nullable Locale locale, @Nullable ZoneId timeZone, @Nullable String ip, @Nullable String agent) {
        Context ctx = new Context(userId, locale, timeZone, ip, agent);
        login(ctx);
        return ctx;
    }

    @NotNull
    public static Context guest(@Nullable Locale locale, @Nullable ZoneId timeZone, @Nullable String ip, @Nullable String agent) {
        Context ctx = new Context(Guest, locale, timeZone, ip, agent);
        login(ctx);
        return ctx;
    }

    /**
     * login，当ctx为null时，执行为logout
     *
     * @param ctx 上下文
     */
    public static void login(Context ctx) {
        if (ctx == null) {
            context.remove();
        }
        else {
            context.set(ctx);
        }
        active = true;
        fireThreadLocalListener(ctx);
    }

    public static void logout() {
        login(null);
    }

    private static void fireThreadLocalListener(Context ctx) {
        if (threadLocalListener.isEmpty()) return;
        for (Map.Entry<String, Consumer<Context>> en : threadLocalListener.entrySet()) {
            en.getValue().accept(ctx);
        }
    }

    @Data
    public static class Context {
        public static final long Guest = Integer.MIN_VALUE;
        public static final Context NULL = new Context(Guest, Locale.getDefault(), TimeZone.getDefault(), null, null);

        private final long userId;
        private final Locale locale;
        private final TimeZone timeZone;
        private final ZoneId zoneId;
        private final String remoteIp;
        private final String agentInfo;

        public Context(long userId, Locale locale, TimeZone timeZone, String remoteIp, String agentInfo) {
            this.userId = userId;
            this.locale = locale == null ? defaultLocale : locale;
            this.timeZone = timeZone == null ? TimeZone.getTimeZone(defaultZoneId) : timeZone;
            this.remoteIp = remoteIp == null ? "" : remoteIp;
            this.agentInfo = agentInfo == null ? "" : agentInfo;
            //
            this.zoneId = this.timeZone.toZoneId();
        }

        public Context(long userId, Locale locale, ZoneId timeZone, String remoteIp, String agentInfo) {
            this.userId = userId;
            this.locale = locale == null ? defaultLocale : locale;
            this.zoneId = timeZone == null ? defaultZoneId : timeZone;
            this.remoteIp = remoteIp == null ? "" : remoteIp;
            this.agentInfo = agentInfo == null ? "" : agentInfo;
            //
            this.timeZone = TimeZone.getTimeZone(this.zoneId);
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

        @Override
        public String toString() {
            return "Context{" +
                   "userId=" + userId +
                   "zoneId=" + zoneId +
                   ", remoteIp='" + remoteIp + '\'' +
                   ", agentInfo='" + agentInfo + '\'' +
                   '}';
        }
    }
}
