package pro.fessional.wings.slardar.cache.cache2k;

import org.cache2k.Cache;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author trydofor
 * @since 2023-01-25
 */
public class Cache2kSlot {

    public static final Cache2kSlot H24M5 = new Cache2kSlot(24 * 3600, 5 * 60);

    private final Map<Integer, Cache<Object, Object>> slot;
    private final int step;
    private final int max;

    /**
     * Construct a cache slot by ttl with ConcurrentHashMap
     *
     * @param ttl  max ttl in second
     * @param step slot step in second
     */
    public Cache2kSlot(int ttl, int step) {
        this(new ConcurrentHashMap<>(), ttl, step);
    }

    /**
     * Construct a cache slot by ttl with specified Map
     *
     * @param slot slot map
     * @param ttl  max ttl in second
     * @param step slot step in second
     */
    public Cache2kSlot(Map<Integer, Cache<Object, Object>> slot, int ttl, int step) {
        this.slot = slot;
        this.step = step;
        this.max = ttl / step;
    }

    /**
     * Get an unbounded cache with a max ttl of 24 hours and a precision of `step` seconds, based on `second`.
     *
     * @param second ttl
     * @return the cache
     */
    @NotNull
    public Cache<Object, Object> getCache(int second) {
        int slot = second / step;

        int min = 1;
        if (slot <= min) {
            slot = min;
        }
        else if (slot >= max) {
            slot = max;
        }

        return this.slot.computeIfAbsent(slot, k -> WingsCache2k.builder(Cache2kSlot.class, "slot" + (k * step), -1, k * step, -1).build());
    }
}
