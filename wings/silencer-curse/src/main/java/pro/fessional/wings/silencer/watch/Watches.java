package pro.fessional.wings.silencer.watch;

import com.alibaba.ttl.TransmittableThreadLocal;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.fessional.mirana.best.DummyBlock;
import pro.fessional.mirana.time.StopWatch;
import pro.fessional.mirana.time.StopWatch.Watch;

import java.util.function.BiConsumer;

/**
 * Performance Monitoring
 *
 * @author trydofor
 * @see StopWatch
 * @since 2022-11-21
 */
public class Watches {

    public static final Logger log = LoggerFactory.getLogger(Watches.class);

    private static final ThreadLocal<StopWatch> StopWatches = new TransmittableThreadLocal<>();

    /**
     * acquire and release with try-close style
     */
    @NotNull
    public static StopWatch acquire() {
        StopWatch watch = StopWatches.get();
        if (watch == null) {
            watch = new StopWatch();
            StopWatches.set(watch);
        }
        return watch;
    }

    @NotNull
    public static Watch acquire(String name) {
        return acquire().start(name);
    }

    @NotNull
    public static Threshold threshold(String name, long mills) {
        return new Threshold(acquire().start(name), mills);
    }

    /**
     * Get the StopWatch at the current thread.
     */
    @Nullable
    public static StopWatch current() {
        return StopWatches.get();
    }

    /**
     * Get the StopWatch at the current thread.
     */
    @Nullable
    public static Watch current(String name) {
        final StopWatch watch = StopWatches.get();
        return watch == null ? null : watch.start(name);
    }

    /**
     * Release the current timer and returns whether all timers have finished.
     * When all timings are finished, clear the watches if clean.
     */
    public static boolean release(boolean clean) {
        return release(clean, null, WatchHandler);
    }

    /**
     * Release the current timer and returns whether all timers have finished.
     * When all timings are finished, handle the StopWatch if token is not null, clear the watches if clean.
     */
    public static boolean release(boolean clean, @Nullable String token) {
        return release(clean, token, WatchHandler);
    }

    /**
     * Release the current timer and returns whether all timers have finished.
     * When all timings are finished, handle the StopWatch if token is not null, clear the watches if clean.
     */
    public static boolean release(boolean clean, @Nullable String token, @NotNull BiConsumer<String, StopWatch> handle) {
        StopWatch watch = StopWatches.get();
        if (watch == null || watch.isRunning()) return false;

        StopWatches.remove();
        if (token != null) {
            try {
                handle.accept(token, watch);
            }
            catch (Exception e) {
                DummyBlock.ignore(e);
            }
        }

        if (clean) {
            watch.clear();
        }
        return true;
    }

    /**
     * to handle the StopWatch when timings are finished and token is not null.
     * should handle the StopWatch before its clean.
     *
     * @param handler token and StopWatch are not null
     */
    @SuppressWarnings("LombokSetterMayBeUsed")
    public static void setWatchHandler(BiConsumer<String, StopWatch> handler) {
        WatchHandler = handler;
    }

    private static volatile BiConsumer<String, StopWatch> WatchHandler = (token, watch) -> log.warn("Watching {} {}", token, watch);

    @Data
    public static class Threshold {
        public final Watch watch;
        public final long millis;

        private volatile long elapse = -1;

        /**
         * whether the ElapseMs ge threshold
         */
        public boolean reach() {
            if (elapse < 0) {
                watch.close();
                elapse = watch.getElapseMs();
            }
            return elapse >= millis;
        }

        public long elapse() {
            return elapse;
        }
    }
}
