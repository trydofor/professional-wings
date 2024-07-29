package pro.fessional.wings.tiny.grow.track;

import org.jetbrains.annotations.NotNull;
import pro.fessional.mirana.cast.EnumConvertor;
import pro.fessional.mirana.cast.MethodConvertor;

import java.lang.reflect.Method;
import java.util.concurrent.FutureTask;

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
    FutureTask<Void> async(Runnable run);

    /**
     * begin a tracking with key and ref
     */
    @NotNull
    TinyTracking begin(@NotNull String key, @NotNull String ref);

    /**
     * post the tracking, fire and forget, never throws
     */
    void track(@NotNull TinyTracking tracking, boolean async);

    /**
     * async post the tracking, fire and forget, never throws
     */
    default void track(@NotNull TinyTracking tracking) {
        track(tracking, true);
    }

    /**
     * raw string key and 'string' ref
     */
    @NotNull
    default TinyTracking begin(@NotNull String key) {
        return begin(key, "string");
    }

    /**
     * method signature key and 'method' ref.
     * e.g. a.b.c.MyClass#method(String,int)
     */
    @NotNull
    default TinyTracking begin(@NotNull Method key) {
        String str = MethodConvertor.method2Str(key);
        return begin(str, "method");
    }

    /**
     * enum signature key and 'enum' ref.
     * e.g. a.b.c.MyEnum#Name
     */
    @NotNull
    default TinyTracking begin(@NotNull Enum<?> key) {
        String str = EnumConvertor.enum2Str(key);
        return begin(str, "enum");
    }

    /**
     * collect tracking to different impl, e.g. Dao to database
     */
    interface Collector {
        void collect(TinyTracking tracking);
    }
}
