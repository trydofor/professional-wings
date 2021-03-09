package pro.fessional.wings.silencer.concur;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 以WeakHashMap包装ReentrantLock作为底层实现。
 * WeakReference用以控制内存使用和正确的锁。
 *
 * @author trydofor
 * @since 2021-03-08
 */
public class JvmStaticGlobalLock implements GlobalLock {

    private static final Map<Hd, WeakReference<Hd>> locks = new WeakHashMap<>();

    @Override
    public @NotNull Lock getLock(@NotNull String name) {
        return get(name);
    }

    public static @NotNull Lock get(@NotNull String name) {
        final Hd hd = new Hd(name);
        synchronized (locks) {
            final WeakReference<Hd> rf = locks.computeIfAbsent(hd, WeakReference::new);
            final Hd lk = rf.get();
            if (lk == null) {
                throw new IllegalStateException("should not gc if key exist， report bug.");
            }
            return lk;
        }
    }

    public static int countLocks() {
        return locks.size();
    }

    public static class Hd extends ReentrantLock {
        private final String name;

        public Hd(String name) {this.name = name;}

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Hd hd = (Hd) o;
            return name.equals(hd.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name);
        }
    }
}
