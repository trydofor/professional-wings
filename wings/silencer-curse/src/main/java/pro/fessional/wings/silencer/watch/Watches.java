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
     * 以try-close方式使用acquire-release
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

    /**
     * 获取当前线程的StopWatch，建议通过Watch.owner获取
     */
    @Nullable
    public static StopWatch current() {
        return StopWatches.get();
    }

    /**
     * 释放当前计时，并返回全部计时是否都已结束。
     * 若全部计时结束时，是否清空记录(clean)，是否写入日志(token != null)。
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
     * 使用Warn级别日志输出计时记录
     */
    public static void logging(String token, StopWatch watch) {
        log.warn("Watching {} {}", token, watch);
    }
}
