package pro.fessional.wings.slardar.context;

import com.alibaba.ttl.TransmittableThreadLocal;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pro.fessional.mirana.best.DummyBlock;
import pro.fessional.mirana.best.TypedKey;
import pro.fessional.wings.slardar.security.DefaultUserId;

import java.time.ZoneId;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 在service层使用，TransmittableThreadLocal有自动的线程继承性。
 * 由 TerminalInterceptor.preHandle 设值，afterCompletion清理。
 * 注：无 WeakReference Leak，因static及Interceptor清理。
 *
 * @author trydofor
 * @since 2019-11-25
 */
public class TerminalContext {

    public static final Context Null = new Context(DefaultUserId.Null, null, null, null);
    public static final TypedKey<String> TerminalAddr = new TypedKey<>() {};
    public static final TypedKey<String> TerminalAgent = new TypedKey<>() {};

    /** no leak, for static and Interceptor clean */
    private static final TransmittableThreadLocal<Context> ContextLocal = new TransmittableThreadLocal<>();
    private static final ConcurrentHashMap<String, Listener> ContextListeners = new ConcurrentHashMap<>();

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
    public static Listener registerListener(String name, Listener listener) {
        return ContextListeners.put(name, listener);
    }

    /**
     * 移除监听器
     *
     * @param name 名字
     * @return null或旧值
     */
    public static Listener removeListener(String name) {
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
        if (ctx == null) ctx = Null;
        if (onlyLogin && ctx.isGuest()) {
            throw new IllegalStateException("must login user");
        }
        return ctx;
    }

    public static Context login(@NotNull Builder builder) {
        final Context ctx = new Context(builder.userId, builder.locale, builder.timeZone, builder.params);
        TerminalContext.login(ctx);
        return ctx;
    }

    /**
     * login，当ctx为null时，执行为logout
     *
     * @param ctx 上下文
     */
    public static void login(Context ctx) {
        if (ctx == null || ctx == Null) {
            final Context old = ContextLocal.get();
            ContextLocal.remove();
            fireContextChange(true, old);
        }
        else {
            ContextLocal.set(ctx);
            fireContextChange(false, ctx);
        }
        Active = true;
    }

    public static void logout() {
        login(Null);
    }

    private static void fireContextChange(boolean del, Context ctx) {
        if (ContextListeners.isEmpty()) return;

        for (Listener listener : ContextListeners.values()) {
            try {
                listener.onChange(del, ctx);
            }
            catch (RuntimeException e) {
                DummyBlock.ignore(e);
            }
        }
    }

    public interface Listener {
        /**
         * 赋新值或删除旧值，新值为NotNull，旧值可能为Null
         *
         * @param del 是否为删除，否则为赋值
         * @param ctx del时为Nullable，否则为NotNull
         */
        @Contract("false,!null->_")
        void onChange(boolean del, Context ctx);
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
            return userId == DefaultUserId.Guest;
        }

        /**
         * userId >= DefaultUserId#Guest
         */
        public boolean asLogin() {
            return userId >= DefaultUserId.Guest;
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
        private long userId;
        private Locale locale;
        private TimeZone timeZone;
        private final Map<TypedKey<?>, Object> params = new HashMap<>();

        public Builder locale(Locale lcl) {
            locale = lcl;
            return this;
        }

        public Builder localeIfAbsent(Locale lcl) {
            if (locale == null) {
                locale = lcl;
            }
            return this;
        }

        public Builder timeZone(TimeZone tz) {
            timeZone = tz;
            return this;
        }

        public Builder timeZoneIfAbsent(TimeZone tz) {
            if (timeZone == null) {
                timeZone = tz;
            }
            return this;
        }

        public Builder timeZone(ZoneId tz) {
            timeZone = TimeZone.getTimeZone(tz);
            return this;
        }

        public Builder timeZoneIfAbsent(ZoneId tz) {
            if (timeZone == null) {
                timeZone = TimeZone.getTimeZone(tz);
            }
            return this;
        }

        public <V> Builder terminal(TypedKey<V> key, V value) {
            params.put(key, value);
            return this;
        }

        public <V> Builder terminalIfAbsent(TypedKey<V> key, V value) {
            params.putIfAbsent(key, value);
            return this;
        }

        public Builder terminal(Map<TypedKey<?>, Object> kvs) {
            if (kvs != null) {
                params.putAll(kvs);
            }
            return this;
        }

        public Builder terminalIfAbsent(Map<TypedKey<?>, Object> kvs) {
            if (kvs != null) {
                for (Map.Entry<TypedKey<?>, Object> en : kvs.entrySet()) {
                    params.putIfAbsent(en.getKey(), en.getValue());
                }
            }
            return this;
        }

        public Builder terminalAddr(String ip) {
            params.put(TerminalAddr, ip);
            return this;
        }

        public Builder terminalAddrIfAbsent(String ip) {
            params.putIfAbsent(TerminalAddr, ip);
            return this;
        }

        public Builder terminalAgent(String info) {
            params.put(TerminalAgent, info);
            return this;
        }

        public Builder terminalAgentIfAbsent(String info) {
            params.putIfAbsent(TerminalAgent, info);
            return this;
        }

        public Builder user(long uid) {
            userId = uid;
            return this;
        }

        public Builder guest() {
            return user(DefaultUserId.Guest);
        }
    }
}
