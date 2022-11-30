package pro.fessional.wings.slardar.context;

import com.alibaba.ttl.TransmittableThreadLocal;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pro.fessional.mirana.best.DummyBlock;
import pro.fessional.mirana.best.TypedKey;
import pro.fessional.wings.slardar.security.DefaultUserId;

import java.time.ZoneId;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
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

    public static final Context Null = new Context(DefaultUserId.Null, null, null, null, null, null);

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
        private final Locale locale;
        private final TimeZone timeZone;
        private final Enum<?> authType;
        private final Set<String> authPerm;
        private final Map<TypedKey<?>, Object> terminal;

        public Context(long userId, Locale locale, TimeZone timeZone, Map<TypedKey<?>,
                Object> params, Enum<?> authType, Set<String> authPerm) {
            this.userId = userId;
            this.locale = locale != null ? locale : DefaultLocale;
            this.timeZone = timeZone != null ? timeZone : DefaultTimeZone;
            this.terminal = params != null ? params : Collections.emptyMap();
            this.authType = authType != null ? authType : pro.fessional.mirana.data.Null.Enm;
            this.authPerm = authPerm != null ? authPerm : Collections.emptySet();
        }

        /**
         * userId == DefaultUserId#Null
         */
        public boolean isNull() {
            return userId == DefaultUserId.Null;
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
            return locale;
        }

        @NotNull
        public TimeZone getTimeZone() {
            return timeZone;
        }

        @NotNull
        public ZoneId getZoneId() {
            return getTimeZone().toZoneId();
        }

        @NotNull
        public Enum<?> getAuthType() {
            return authType;
        }

        @NotNull
        public Set<String> getAuthPerm() {
            return authPerm;
        }

        public boolean hasAuthPerm(String auth) {
            return authPerm.contains(auth);
        }

        public boolean anyAuthPerm(Collection<String> auths) {
            if (auths == null) return false;
            for (String auth : auths) {
                if (authPerm.contains(auth)) {
                    return true;
                }
            }
            return false;
        }

        public boolean allAuthPerm(Collection<String> auths) {
            if (auths == null) return true;
            return authPerm.containsAll(auths);
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
        private Enum<?> authType;
        private final Set<String> authPerm = new HashSet<>();
        private final Map<TypedKey<?>, Object> terminal = new HashMap<>();

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

        public Builder authType(Enum<?> at) {
            authType = at;
            return this;
        }

        public Builder authPerm(String pm) {
            authPerm.add(pm);
            return this;
        }

        public Builder authPerm(Collection<String> pm) {
            authPerm.addAll(pm);
            return this;
        }

        public <V> Builder terminal(TypedKey<V> key, V value) {
            terminal.put(key, value);
            return this;
        }

        public <V> Builder terminalIfAbsent(TypedKey<V> key, V value) {
            terminal.putIfAbsent(key, value);
            return this;
        }

        public Builder terminal(Map<TypedKey<?>, Object> kvs) {
            if (kvs != null) {
                terminal.putAll(kvs);
            }
            return this;
        }

        public Builder terminalIfAbsent(Map<TypedKey<?>, Object> kvs) {
            if (kvs != null) {
                for (Map.Entry<TypedKey<?>, Object> en : kvs.entrySet()) {
                    terminal.putIfAbsent(en.getKey(), en.getValue());
                }
            }
            return this;
        }

        public Builder user(long uid) {
            userId = uid;
            return this;
        }

        public Builder guest() {
            return user(DefaultUserId.Guest);
        }

        public Context build() {
            return new Context(userId, locale, timeZone, terminal, authType, authPerm);
        }
    }
}
