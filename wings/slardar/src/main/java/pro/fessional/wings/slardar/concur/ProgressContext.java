package pro.fessional.wings.slardar.concur;

import lombok.Getter;
import org.cache2k.Cache;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pro.fessional.mirana.code.LeapCode;
import pro.fessional.mirana.time.ThreadNow;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

import static pro.fessional.wings.slardar.cache.cache2k.Cache2kSlot.H24M5;

/**
 * @author trydofor
 * @since 2021-03-15
 */
public class ProgressContext {

    private static final AtomicLong count = new AtomicLong(0);
    private static final LeapCode leapCode = new LeapCode();

    /**
     * Get the progress bar by inside key
     */
    @Nullable
    public static Bar get(String key) {
        if (key == null) return null;
        int pos = key.indexOf("-");
        if (pos <= 0) return null;

        final long second = leapCode.decode(key, 0, pos);
        return get(key, (int) second);
    }

    /**
     * Get the progress bar by outside key and ttl second.
     */
    @Nullable
    public static Bar get(Object key, int second) {
        if (key == null) return null;

        final Cache<Object, Object> cache = H24M5.getCache(second);
        final Object obj = cache.get(key);
        if (obj instanceof Bar) {
            return (Bar) obj;
        }
        else {
            return null;
        }
    }

    /**
     * Get cache by ttl second
     */
    @NotNull
    public static Cache<Object, Object> get(int second) {
        return H24M5.getCache(second);
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
        final Cache<Object, Object> cache = H24M5.getCache(second);
        Bar bar = new Bar(key(started, second), started);
        cache.put(bar.key, bar);
        if (key != null) {
            cache.put(key, bar);
        }
        return bar;
    }

    /**
     * build an inside key by started time and  ttl second
     */
    @NotNull
    public static String key(long started, int second) {
        StringBuilder sb = new StringBuilder(30);
        sb.append(leapCode.encode26(second, 5));
        sb.append('-');
        sb.append(leapCode.encode26(started, 10));
        long cnt = count.incrementAndGet();
        while (cnt <= 0) {
            count.set(0);
            cnt = count.incrementAndGet();
        }
        sb.append('-');
        sb.append(leapCode.encode26(cnt, 5));
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
