package pro.fessional.wings.slardar.context;

import com.alibaba.ttl.TransmittableThreadLocal;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pro.fessional.mirana.best.DummyBlock;
import pro.fessional.mirana.best.TypedKey;
import pro.fessional.mirana.time.ThreadNow;
import pro.fessional.wings.slardar.errcode.AuthnErrorEnum;
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
 * Used at the service level, TransmittableThreadLocal has automatic thread inheritance.
 * Set by TerminalInterceptor.preHandle, cleaned up by afterCompletion.
 * Note: No WeakReference Leak due to static and Interceptor cleanup.
 *
 * @author trydofor
 * @see <a href="https://github.com/alibaba/transmittable-thread-local/blob/master/docs/developer-guide-en.md#-frameworkmiddleware-integration-to-ttl-transmittance">Framework/Middleware integration to TTL transmittance</a>
 * @since 2019-11-25
 */
public class TerminalContext {

    public static final Context Null = new Context(
            DefaultUserId.Null,
            Locale.getDefault(),
            TimeZone.getDefault(),
            Collections.emptyMap(),
            pro.fessional.mirana.data.Null.Enm,
            pro.fessional.mirana.data.Null.Str,
            Collections.emptySet());

    /**
     * no leak, for static and Interceptor clean
     */
    private static final TransmittableThreadLocal<Context> ContextLocal = new TransmittableThreadLocal<>();
    private static final ConcurrentHashMap<String, Listener> ContextListeners = new ConcurrentHashMap<>();

    private static volatile boolean Active;

    @NotNull
    private static volatile TimeZone DefaultTimeZone = ThreadNow.sysTimeZone();
    @NotNull
    private static volatile Locale DefaultLocale = Locale.getDefault();

    /**
     * init default zoneId
     */
    public static void initTimeZone(@NotNull TimeZone zoneId) {
        DefaultTimeZone = zoneId;
    }

    /**
     * init default locale
     */
    public static void initLocale(@NotNull Locale locale) {
        DefaultLocale = locale;
    }


    /**
     * get default zoneId
     */
    @NotNull
    public static ZoneId defaultZoneId() {
        return DefaultTimeZone.toZoneId();
    }

    /**
     * get default timezone
     */
    @NotNull
    public static TimeZone defaultTimeZone() {
        return DefaultTimeZone;
    }


    /**
     * get default locale
     */
    @NotNull
    public static Locale defaultLocale() {
        return DefaultLocale;
    }

    /**
     * get current use or system zoneId
     *
     * @return zoneId
     */
    @NotNull
    public static ZoneId currentZoneId() {
        return currentTimeZone().toZoneId();
    }

    /**
     * get current use or system timezone
     */
    @NotNull
    public static TimeZone currentTimeZone() {
        final Context context = get(false);
        return context.getTimeZone();
    }

    /**
     * get current use or system locale
     *
     * @return locale
     */
    @NotNull
    public static Locale currentLocale() {
        final Context context = get(false);
        return context.getLocale();
    }

    /**
     * set login and logout listener. context is null for logout, else for login.
     *
     * @param name     name of listener
     * @param listener the listener
     * @return null or old value
     */
    public static Listener registerListener(String name, Listener listener) {
        return ContextListeners.put(name, listener);
    }

    /**
     * remove login and logout listener by name
     *
     * @param name name of listener
     * @return null or old value
     */
    public static Listener removeListener(String name) {
        return ContextListeners.remove(name);
    }

    /**
     * only login user, throw TerminalException if not login
     *
     * @throws TerminalContextException if not login
     */
    @NotNull
    public static Context get() throws TerminalContextException {
        return get(true);
    }

    /**
     * unlogined user as Guest or throw TerminalException
     *
     * @param onlyLogin only login user or guest
     * @throws TerminalContextException if onlyLogin and not login
     */
    @NotNull
    public static Context get(boolean onlyLogin) throws TerminalContextException {
        Context ctx = null;
        if (Active) {
            ctx = ContextLocal.get();
        }
        if (ctx == null) {
            ctx = Null;
        }
        if (onlyLogin && ctx.isNull()) {
            throw new TerminalContextException(AuthnErrorEnum.Unauthorized, "find null context, must be user or guest");
        }
        return ctx;
    }

    /**
     * login if ctx is not null/Null, else logout
     */
    public static void login(@Nullable Context ctx) {
        if (ctx == null || ctx.isNull()) {
            logout();
        }
        else {
            Active = true;
            ContextLocal.set(ctx);
            fireContextChange(false, ctx);
        }
    }

    /**
     * logout the context and fireContextChange
     */
    @Nullable
    public static Context logout() {
        return logout(true);
    }

    /**
     * logout the context and whether to fireContextChange
     *
     * @param fire whether to fireContextChange
     */
    @Nullable
    public static Context logout(boolean fire) {
        final Context old = ContextLocal.get();
        if (old != null) {
            ContextLocal.remove();
            if (fire) {
                fireContextChange(true, old);
            }
        }
        return old;
    }

    private static void fireContextChange(boolean del, @NotNull Context ctx) {
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
         * set new value or delete old value, new value is NotNull, old value maybe Null
         *
         * @param del whether to delete, else set value
         * @param ctx new to set, or old to delete
         */
        void onChange(boolean del, @NotNull Context ctx);
    }

    public static class Context {

        private final long userId;
        private final Locale locale;
        private final TimeZone timeZone;
        private final Enum<?> authType;
        private final String username;
        private final Set<String> authPerm;
        private final Map<TypedKey<?>, Object> terminal;

        public Context(long userId, Locale locale, TimeZone timeZone, Map<TypedKey<?>,
                Object> params, Enum<?> authType, String username, Set<String> authPerm) {
            this.userId = userId;
            this.locale = locale != null ? locale : DefaultLocale;
            this.timeZone = timeZone != null ? timeZone : DefaultTimeZone;
            this.terminal = params != null ? params : Collections.emptyMap();
            this.authType = authType != null ? authType : pro.fessional.mirana.data.Null.Enm;
            this.username = username != null ? username : pro.fessional.mirana.data.Null.Str;
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
        public String getUsername() {
            return username;
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

        /**
         * key must be defined by TerminalAttribute or its subclasses
         *
         * @see TerminalAttribute
         */
        @Nullable
        public <T> T getTerminal(@NotNull TypedKey<T> key) {
            return key.get(terminal);
        }

        /**
         * key must be defined by TerminalAttribute or its subclasses
         *
         * @see TerminalAttribute
         */
        @Contract("_,true->!null")
        public <T> T getTerminal(@NotNull TypedKey<T> key, boolean notnull) {
            final T t = key.get(terminal);
            if (t == null && notnull) {
                throw new NullPointerException("Terminal Key " + key + " returned null");
            }
            return t;
        }

        /**
         * key must be defined by TerminalAttribute or its subclasses
         *
         * @see TerminalAttribute
         */
        @Contract("_,!null->!null")
        public <T> T tryTerminal(@NotNull TypedKey<T> key, T elze) {
            return key.tryOr(terminal, elze);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Context context)) return false;
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
                   ", authType=" + authType +
                   ", username='" + username + '\'' +
                   '}';
        }
    }

    public static class Builder {
        private long userId = Null.userId;
        private Locale locale;
        private TimeZone timeZone;
        private Enum<?> authType;
        private String username;
        private final Set<String> authPerm = new HashSet<>();
        private final Map<TypedKey<?>, Object> terminal = new HashMap<>();

        @Contract("_->this")
        public Builder locale(Locale lcl) {
            locale = lcl;
            return this;
        }

        @Contract("_->this")
        public Builder localeIfAbsent(Locale lcl) {
            if (locale == null) {
                locale = lcl;
            }
            return this;
        }

        @Contract("_->this")
        public Builder timeZone(TimeZone tz) {
            timeZone = tz;
            return this;
        }

        @Contract("_->this")
        public Builder timeZoneIfAbsent(TimeZone tz) {
            if (timeZone == null) {
                timeZone = tz;
            }
            return this;
        }

        @Contract("_->this")
        public Builder timeZone(ZoneId tz) {
            timeZone = TimeZone.getTimeZone(tz);
            return this;
        }

        @Contract("_->this")
        public Builder timeZoneIfAbsent(ZoneId tz) {
            if (timeZone == null) {
                timeZone = TimeZone.getTimeZone(tz);
            }
            return this;
        }

        @Contract("_->this")
        public Builder authType(Enum<?> at) {
            authType = at;
            return this;
        }

        @Contract("_->this")
        public Builder authTypeIfAbsent(Enum<?> at) {
            if (authType == null) {
                authType = at;
            }
            return this;
        }

        @Contract("_->this")
        public Builder username(String un) {
            username = un;
            return this;
        }

        @Contract("_->this")
        public Builder usernameIfAbsent(String un) {
            if (username == null) {
                username = un;
            }
            return this;
        }

        @Contract("_->this")
        public Builder authPerm(String pm) {
            authPerm.add(pm);
            return this;
        }

        @Contract("_->this")
        public Builder authPerm(Collection<String> pm) {
            authPerm.addAll(pm);
            return this;
        }

        @Contract("_,_->this")
        public <V> Builder terminal(TypedKey<V> key, V value) {
            terminal.put(key, value);
            return this;
        }

        @Contract("_,_->this")
        public <V> Builder terminalIfAbsent(TypedKey<V> key, V value) {
            terminal.putIfAbsent(key, value);
            return this;
        }

        @Contract("_->this")
        public Builder terminal(Map<TypedKey<?>, Object> kvs) {
            if (kvs != null) {
                terminal.putAll(kvs);
            }
            return this;
        }

        @Contract("_->this")
        public Builder terminalIfAbsent(Map<TypedKey<?>, Object> kvs) {
            if (kvs != null) {
                for (Map.Entry<TypedKey<?>, Object> en : kvs.entrySet()) {
                    terminal.putIfAbsent(en.getKey(), en.getValue());
                }
            }
            return this;
        }

        @Contract("_->this")
        public Builder user(long uid) {
            userId = uid;
            return this;
        }

        @Contract("_->this")
        public Builder userIfAbsent(Long uid) {
            if (userId == Null.userId && uid != null) {
                userId = uid;
            }
            return this;
        }

        @Contract("_->this")
        public Builder userOrGuest(Long uid) {
            userId = uid == null ? DefaultUserId.Guest : uid;
            return this;
        }

        @Contract("->this")
        public Builder guest() {
            return user(DefaultUserId.Guest);
        }

        @NotNull
        public Context build() {
            if (userId == Null.userId) {
                throw new IllegalArgumentException("invalid userid");
            }
            return new Context(userId, locale, timeZone, terminal, authType, username, authPerm);
        }
    }
}
