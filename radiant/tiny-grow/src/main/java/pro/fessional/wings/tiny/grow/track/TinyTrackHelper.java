package pro.fessional.wings.tiny.grow.track;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.function.SingletonSupplier;
import pro.fessional.mirana.func.Lam;
import pro.fessional.mirana.pain.ThrowableUtil;
import pro.fessional.mirana.time.ThreadNow;
import pro.fessional.wings.silencer.spring.help.ApplicationContextHelper;

import java.lang.reflect.Method;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author trydofor
 * @since 2024-07-25
 */
@Slf4j
public class TinyTrackHelper {

    private static final SingletonSupplier<TinyTrackService> TrackService = ApplicationContextHelper.getSingletonSupplier(TinyTrackService.class);

    public static <R> R track(@NotNull Lam.Ref key, @NotNull Function<TinyTracking, R> fun) {
        return track(key.method, fun);
    }

    public static <R> R track(@NotNull Method key, @NotNull Function<TinyTracking, R> fun) {
        TinyTrackService service = TrackService.obtain();
        TinyTracking tracking = service.begin(key);
        return track(service, tracking, fun);
    }

    public static <R> R track(@NotNull Enum<?> key, @NotNull Function<TinyTracking, R> fun) {
        TinyTrackService service = TrackService.obtain();
        TinyTracking tracking = service.begin(key);
        return track(service, tracking, fun);
    }

    public static <R> R track(@NotNull String key, @NotNull Function<TinyTracking, R> fun) {
        TinyTrackService service = TrackService.obtain();
        TinyTracking tracking = service.begin(key);
        return track(service, tracking, fun);
    }

    public static <R> R track(@NotNull String key, @NotNull String ref, @NotNull Function<TinyTracking, R> fun) {
        TinyTrackService service = TrackService.obtain();
        TinyTracking tracking = service.begin(key, ref);
        return track(service, tracking, fun);
    }

    private static <R> R track(@NotNull TinyTrackService service, @NotNull TinyTracking tracking, @NotNull Function<TinyTracking, R> fun) {
        try {
            R out = fun.apply(tracking);
            if (tracking.getOut() == null) {
                tracking.setOut(out);
            }
            return out;
        }
        catch (Throwable e) {
            if (tracking.getErr() == null) {
                tracking.setErr(e);
            }
            throw ThrowableUtil.runtime(e);
        }
        finally {
            tracking.setElapse(ThreadNow.millis() - tracking.getBegin());
            service.track(tracking, true);
        }
    }

    public static TrackWrapper track(@NotNull Lam.Ref key) {
        return track(key.method);
    }

    public static TrackWrapper track(@NotNull Method key) {
        TinyTrackService service = TrackService.obtain();
        TinyTracking tracking = service.begin(key);
        return new TrackWrapper(tracking, service);
    }

    public static TrackWrapper track(@NotNull Enum<?> key) {
        TinyTrackService service = TrackService.obtain();
        TinyTracking tracking = service.begin(key);
        return new TrackWrapper(tracking, service);
    }

    public static TrackWrapper track(@NotNull String key) {
        TinyTrackService service = TrackService.obtain();
        TinyTracking tracking = service.begin(key);
        return new TrackWrapper(tracking, service);
    }

    public static TrackWrapper track(@NotNull String key, @NotNull String ref) {
        TinyTrackService service = TrackService.obtain();
        TinyTracking tracking = service.begin(key, ref);
        return new TrackWrapper(tracking, service);
    }

    /**
     * <pre>
     * try(tryTrack) {
     *   // biz logic
     * };
     * </pre>
     */
    @RequiredArgsConstructor
    @Getter
    public static class TrackWrapper implements AutoCloseable {

        @NotNull
        @Delegate(types = TinyTracking.class)
        private final TinyTracking tracking;

        @NotNull
        private final TinyTrackService service;

        /**
         * set properties without exception thrown
         */
        public void safeSet(@NotNull Consumer<TinyTracking> fun) {
            try {
                fun.accept(tracking);
            }
            catch (Throwable e) {
                log.warn("safeSet get error", e);
            }
        }

        /**
         * set properties and out without exception thrown
         */
        public <R> R safeOut(@NotNull Function<TinyTracking, R> fun) {
            R out = null;
            try {
                out = fun.apply(tracking);
                tracking.setOut(out);
            }
            catch (Throwable e) {
                log.warn("safeOut get error", e);
            }
            return out;
        }

        @Override
        public void close() {
            tracking.setElapse(ThreadNow.millis() - tracking.getBegin());
            service.track(tracking, true);
        }
    }
}
