package pro.fessional.wings.slardar.concur;

import lombok.Getter;
import org.cache2k.Cache;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pro.fessional.mirana.code.LeapCode;
import pro.fessional.wings.slardar.cache.cache2k.Cache2kSlot;


import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author trydofor
 * @since 2021-03-15
 */
public class ProgressContext {

    private static final Cache2kSlot slots = new Cache2kSlot(24 * 3600, 300);
    private static final AtomicLong count = new AtomicLong(0);
    private static final LeapCode leapCode = new LeapCode();

    /**
     * 通过内建的key，获得Bar
     *
     * @param key 内建key
     * @return bar
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
     * 通过外部的key，获得Bar
     *
     * @param key 外部key
     * @return bar
     */
    @Nullable
    public static Bar get(Object key, int second) {
        if (key == null) return null;

        final Cache<Object, Object> cache = slots.getCache(second);
        final Object obj = cache.get(key);
        if (obj instanceof Bar) {
            return (Bar) obj;
        }
        else {
            return null;
        }
    }

    /**
     * 通过 ttl获得对应的Cache
     *
     * @param second ttl
     * @return cache
     */
    @NotNull
    public static Cache<Object, Object> get(int second) {
        return slots.getCache(second);
    }

    /**
     * 生成个Bar
     *
     * @param key     外部Key，可以用来获取 Bar
     * @param started 开始毫秒数
     * @param second  ttl秒数
     * @return Bar
     */
    @NotNull
    public static Bar gen(Object key, long started, int second) {
        final Cache<Object, Object> cache = slots.getCache(second);
        Bar bar = new Bar(key(started, second), started);
        cache.put(bar.key, bar);
        if (key != null) {
            cache.put(key, bar);
        }
        return bar;
    }

    /**
     * 内建key
     *
     * @param started 开始毫秒数
     * @param second  ttl秒数
     * @return 内建key
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

        private transient volatile boolean done = false;
        private transient volatile Object result = null;
        private transient volatile Throwable error = null;

        public Bar(String key, long started) {
            this.key = key;
            this.started = started;
        }

        public void fail(Throwable error) {
            this.error = error;
            this.done = true;
        }

        public void ok(Object result) {
            this.result = result;
            this.done = true;
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

        @Override
        public String toString() {
            return "Bar{" +
                   "key='" + key + '\'' +
                   ", started=" + started +
                   ", done=" + done +
                   ", error=" + error +
                   '}';
        }
    }
}
