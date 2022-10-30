package pro.fessional.wings.slardar.context;

import com.alibaba.ttl.TransmittableThreadLocal;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pro.fessional.mirana.best.DummyBlock;
import pro.fessional.mirana.best.TypedKey;

import java.time.ZoneId;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;

import static pro.fessional.wings.slardar.security.DefaultUserId.Guest;

/**
 * 在service层使用，TransmittableThreadLocal有自动的线程继承性。
 * 由 TerminalInterceptor.preHandle 设值，afterCompletion清理。
 * 注：无 WeakReference Leak，因static及Interceptor清理。
 *
 * @author trydofor
 * @since 2019-11-25
 */
public class TerminalContext {

    public static final Context NULL = new Context(Guest, null, null, null);
    public static final TypedKey<String> RemoteIp = new TypedKey<>() {};
    public static final TypedKey<String> AgentInfo = new TypedKey<>() {};

    /** no leak, for static and Interceptor clean */
    private static final TransmittableThreadLocal<Context> ContextLocal = new TransmittableThreadLocal<>();
    private static final ConcurrentHashMap<String, ContextChangeListener> ContextListeners = new ConcurrentHashMap<>();

    private static volatile boolean Active;
    @NotNull
    private static volatile TimeZone DefaultTimeZone = TimeZone.getDefault();
    @NotNull
    private static volatile Locale DefaultLocale = Locale.getDefault();

    /**
     * 是否处于激活状态，可以正确使用
     */
    public static boolean isActive() {
        return Active;
    }

    /**
     * 初始激活状态，标记功能是否能够正常使用。
     */

    public static void initActive(boolean b) {
        Active = b;
    }

    /**
     * 设置默认时区
     */
    public static void initTimeZone(@NotNull TimeZone zoneId) {
        DefaultTimeZone = zoneId;
    }

    /**
     * 设置默认语言
     */
    public static void initLocale(@NotNull Locale locale) {
        DefaultLocale = locale;
    }


    /**
     * 默认时区
     */
    @NotNull
    public static ZoneId defaultZoneId() {
        return DefaultTimeZone.toZoneId();
    }

    /**
     * 默认时区
     */
    @NotNull
    public static TimeZone defaultTimeZone() {
        return DefaultTimeZone;
    }


    /**
     * 默认语言
     */
    @NotNull
    public static Locale defaultLocale() {
        return DefaultLocale;
    }


    /**
     * 设置的login和logout的THreadLocal监听，context为null时，为logout，否则为login
     *
     * @param name     名字
     * @param listener 监听器
     * @return null或旧值
     */
    public static ContextChangeListener registerListener(String name, ContextChangeListener listener) {
        return ContextListeners.put(name, listener);
    }

    /**
     * 移除监听器
     *
     * @param name 名字
     * @return null或旧值
     */
    public static ContextChangeListener removeListener(String name) {
        return ContextListeners.remove(name);
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
        Context ctx = TerminalContext.ContextLocal.get();
        if (ctx == null) ctx = NULL;
        if (onlyLogin && ctx.isGuest()) {
            throw new IllegalStateException("must login user");
        }
        return ctx;
    }

    @NotNull
    public static Builder login() {
        return new Builder();
    }

    /**
     * login，当ctx为null时，执行为logout
     *
     * @param ctx 上下文
     */
    public static void login(Context ctx) {
        if (ctx == null) {
            final Context old = ContextLocal.get();
            ContextLocal.remove();
            fireContextChange(false, old);
        }
        else {
            ContextLocal.set(ctx);
            fireContextChange(true, ctx);
        }
        Active = true;
    }

    public static void logout() {
        login(null);
    }

    private static void fireContextChange(boolean assign, Context ctx) {
        if (ContextListeners.isEmpty()) return;

        final Collection<ContextChangeListener> vs = ContextListeners.values();
        for (ContextChangeListener listener : ContextListeners.values()) {
            try {
                if (assign) {
                    listener.assign(ctx);
                }
                else {
                    listener.remove(ctx);
                }
            }
            catch (RuntimeException e) {
                DummyBlock.ignore(e);
            }
        }
    }

    public interface ContextChangeListener {
        /**
         * 登入现值
         */
        void assign(@NotNull Context ctx);

        /**
         * 登出前值
         */
        void remove(@Nullable Context ctx);
    }

    public static class Context {

        private final long userId;
        @Nullable
        private final Locale locale;
        @Nullable
        private final TimeZone timeZone;
        @NotNull
        private final Map<TypedKey<?>, Object> terminal;

        public Context(long userId, Locale locale, TimeZone timeZone) {
            this(userId, locale, timeZone, null);
        }

        public Context(long userId, @Nullable Locale locale, @Nullable TimeZone timeZone, Map<TypedKey<?>, Object> params) {
            this.userId = userId;
            this.locale = locale;
            this.timeZone = timeZone;
            this.terminal = params != null ? params : new HashMap<>();
        }

        /**
         * userId == DefaultUserId#Guest
         */
        public boolean isGuest() {
            return userId == Guest;
        }

        /**
         * userId >= DefaultUserId#Guest
         */
        public boolean asLogin() {
            return userId >= Guest;
        }

        public long getUserId() {
            return userId;
        }

        @NotNull
        public Locale getLocale() {
            return locale != null ? locale : defaultLocale();
        }

        @NotNull
        public TimeZone getTimeZone() {
            return timeZone != null ? timeZone : defaultTimeZone();
        }

        @NotNull
        public ZoneId getZoneId() {
            return getTimeZone().toZoneId();
        }

        public <T> void putTerminal(@NotNull TypedKey<T> key, T value) {
            terminal.put(key, value);
        }

        @Nullable
        public <T> T getTerminal(@NotNull TypedKey<T> key) {
            return key.get(terminal);
        }

        @Contract("_,!null->!null")
        public <T> T tryTerminal(@NotNull TypedKey<T> key, T elze) {
            return key.tryOr(terminal, elze);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Context)) return false;
            Context context = (Context) o;
            return userId == context.userId &&
                   Objects.equals(locale, context.locale) &&
                   Objects.equals(timeZone, context.timeZone) &&
                   terminal.equals(context.terminal);
        }

        @Override
        public int hashCode() {
            return Objects.hash(userId, locale, timeZone, terminal);
        }

        @Override
        public String toString() {
            return "Context{" +
                   "userId=" + userId +
                   ", locale=" + locale +
                   ", timeZone=" + timeZone +
                   ", terminal=" + terminal +
                   '}';
        }
    }

    public static class Builder {
        private Locale locale;
        private TimeZone timeZone;
        private final Map<TypedKey<?>, Object> params = new HashMap<>();

        public Builder withLocale(Locale lcl) {
            locale = lcl;
            return this;
        }

        public Builder withTimeZone(TimeZone tz) {
            timeZone = tz;
            return this;
        }

        public Builder withTimeZone(ZoneId tz) {
            timeZone = TimeZone.getTimeZone(tz);
            return this;
        }

        public <V> Builder withTerminal(TypedKey<V> key, V value) {
            params.put(key, value);
            return this;
        }

        public Builder withTerminal(Map<TypedKey<?>, Object> kvs) {
            if (kvs != null) {
                params.putAll(kvs);
            }
            return this;
        }

        public <V> Builder withRemoteIp(String value) {
            params.put(RemoteIp, value);
            return this;
        }

        public <V> Builder withAgentInfo(String value) {
            params.put(AgentInfo, value);
            return this;
        }

        public Context asUser(long uid) {
            final Context ctx = new Context(uid, locale, timeZone, params);
            TerminalContext.login(ctx);
            return ctx;
        }

        public Context asGuest() {
            return asUser(Guest);
        }
    }
}
