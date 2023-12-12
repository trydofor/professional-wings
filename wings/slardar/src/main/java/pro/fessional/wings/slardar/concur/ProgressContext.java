package pro.fessional.wings.slardar.concur;

import lombok.Getter;
import org.cache2k.Cache;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pro.fessional.mirana.code.LeapCode;
import pro.fessional.mirana.time.ThreadNow;
import pro.fessional.wings.slardar.cache.cache2k.WingsCache2k;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author trydofor
 * @since 2021-03-15
 */
public class ProgressContext {

    private static final Cache<Object, Bar> Cache = WingsCache2k
            .builder(ProgressContext.class, "bar", 0, 24 * 3600, 0, Object.class, Bar.class)
            .build();

    private static final AtomicLong Counter = new AtomicLong(0);
    private static final LeapCode Encoder = new LeapCode();

    /**
     * Get the progress bar by outside key and ttl second.
     */
    @Nullable
    public static Bar get(Object key) {
        return key == null ? null : Cache.get(key);
    }

    /**
     * Generate a progress bar by outside key and ttl second
     */
    @NotNull
    public static Bar gen(Object key, int second) {
        return gen(key, ThreadNow.millis(), second);
    }

    /**
     * Generate a progress bar by outside key, started time and  ttl second
     */
    @NotNull
    public static Bar gen(Object key, long started, int second) {
        final Bar bar = new Bar(key(started, second), started);
        Cache.mutate(bar.key, entry -> {
            entry.setValue(bar);
            entry.setExpiryTime(entry.getStartTime() + second * 1000L);
        });

        if (key != null) {
            Cache.mutate(key, entry -> {
                entry.setValue(bar);
                entry.setExpiryTime(entry.getStartTime() + second * 1000L);
            });
        }
        return bar;
    }

    /**
     * build an inside key by started time and  ttl second
     */
    @NotNull
    public static String key(long started, int second) {
        StringBuilder sb = new StringBuilder(50);
        sb.append(Encoder.encode26(second, 5));
        sb.append('-');
        sb.append(Encoder.encode26(started, 10));
        long cnt = Counter.incrementAndGet();
        while (cnt <= 0) {
            Counter.set(0);
            cnt = Counter.incrementAndGet();
        }
        sb.append('-');
        sb.append(Encoder.encode26(cnt, 5));
        return sb.toString();
    }

    @Getter
    public static class Bar {

        private final String key;
        private final long started;
        private volatile long stopped = -1;
        private volatile int percent = 0;
        private transient volatile Object result = null;
        private transient volatile Object attach = null;
        private transient volatile Throwable error = null;

        public Bar(String key, long started) {
            this.key = key;
            this.started = started;
        }

        public boolean isDone() {
            return stopped > 0;
        }

        /**
         * set current progress percent, from 0 to 100
         */
        public void setPercent(int percent) {
            this.percent = percent;
        }

        /**
         * set attachment to the bar
         */
        public void setAttach(Object attach) {
            this.attach = attach;
        }

        public void fail(Throwable error) {
            this.error = error;
            this.stopped = ThreadNow.millis();
        }

        public void ok(Object result) {
            this.result = result;
            this.stopped = ThreadNow.millis();
            this.percent = 100;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Bar bar = (Bar) o;
            return Objects.equals(key, bar.key);
        }

        @Override
        public int hashCode() {
            return Objects.hash(key);
        }

        @Override public String toString() {
            return "Bar{" +
                   "key='" + key + '\'' +
                   ", started=" + started +
                   ", stopped=" + stopped +
                   ", percent=" + percent +
                   '}';
        }
    }
}
