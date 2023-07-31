package pro.fessional.wings.silencer.watch;

import com.alibaba.ttl.TransmittableThreadLocal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.fessional.mirana.time.StopWatch;

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
    public static StopWatch.Watch acquire(String name) {
        return acquire().start(name);
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
    public static StopWatch.Watch current(String name) {
        final StopWatch watch = StopWatches.get();
        return watch == null ? null : watch.start(name);
    }

    /**
     * Release the current timer and returns whether all timers have finished.
     * When all timings are finished, clear the log if clean, write to log if token != null.
     */
    public static boolean release(boolean clean, String token) {
        StopWatch watch = StopWatches.get();
        if (watch == null || watch.isRunning()) return false;

        StopWatches.remove();
        if (token != null) {
            logging(token, watch);
        }
        if (clean) {
            watch.clear();
        }
        return true;
    }

    /**
     * output info to the log with token at Warn level
     */
    public static void logging(String token, StopWatch watch) {
        log.warn("Watching {} {}", token, watch);
    }
}
