package pro.fessional.wings.tiny.grow.track;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import pro.fessional.mirana.cast.EnumConvertor;
import pro.fessional.mirana.cast.MethodConvertor;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Data tracking in async, never throws
 *
 * @author trydofor
 * @since 2024-07-24
 */
public interface TinyTrackService {

    /**
     * async executor
     */
    void async(Runnable run);

    /**
     * begin a tracking with key and ref
     */
    @NotNull
    Tracking begin(@NotNull String key, @NotNull String ref);

    /**
     * post the tracking, fire and forget, never throws
     */
    void track(@NotNull Tracking tracking, boolean async);

    /**
     * async post the tracking, fire and forget, never throws
     */
    default void track(@NotNull Tracking tracking) {
        track(tracking, true);
    }

    /**
     * raw string key and 'string' ref
     */
    @NotNull
    default Tracking begin(@NotNull String key) {
        return begin(key, "string");
    }

    /**
     * method signature key and 'method' ref.
     * e.g. a.b.c.MyClass#method(String,int)
     */
    @NotNull
    default Tracking begin(@NotNull Method key) {
        String str = MethodConvertor.method2Str(key);
        return begin(str, "method");
    }

    /**
     * enum signature key and 'enum' ref.
     * e.g. a.b.c.MyEnum#Name
     */
    @NotNull
    default Tracking begin(@NotNull Enum<?> key) {
        String str = EnumConvertor.enum2Str(key);
        return begin(str, "enum");
    }

    /**
     * collect tracking to different impl, e.g. Dao to database
     */
    interface Collector {
        void collect(Tracking tracking);
    }

    @Data
    class Tracking {
        private final long begin;
        private final String key;
        private final String ref;

        private String app;
        @NotNull
        private Map<String, Object> env = new LinkedHashMap<>();
        @NotNull
        private Object[] ins = new Object[0];
        private Object out;
        private Throwable err;
        private long elapse;

        private long userKey;
        private long userRef;

        private long dataKey;
        private long dataRef;
        private long dataOpt;

        private String codeKey;
        private String codeRef;
        private String codeOpt;

        private String wordRef;

        public void setIns(Object... ins) {
            this.ins = ins;
        }

        public void addEnv(String key, Object value) {
            env.put(key, value);
        }

        public void addEnv(Map<String, Object> envs) {
            env.putAll(envs);
        }
    }
}
