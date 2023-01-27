package pro.fessional.wings.slardar.cache.cache2k;

import org.cache2k.Cache;
import org.cache2k.Cache2kBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * @author trydofor
 * @since 2023-01-25
 */
public class Cache2kSlot {

    private final Map<Integer, Cache<Object, Object>> slot;
    private final int step;
    private final int max;

    /**
     * 以ConcurrentHashMap构造一个按ttl分片的缓存
     *
     * @param ttl  最大ttl秒
     * @param step 分片步长秒
     */
    public Cache2kSlot(int ttl, int step) {
        this(new ConcurrentHashMap<>(), ttl, step);
    }

    /**
     * 构造一个按ttl分片的缓存
     *
     * @param slot slot
     * @param ttl  最大ttl秒
     * @param step 分片步长秒
     */
    public Cache2kSlot(Map<Integer, Cache<Object, Object>> slot, int ttl, int step) {
        this.slot = slot;
        this.step = step;
        this.max = ttl / step;
    }

    /**
     * 根据秒数，获得最大24小时，误差step秒的无界缓存
     *
     * @param second ttl
     * @return 缓存
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

        return this.slot.computeIfAbsent(slot,
                k -> Cache2kBuilder.forUnknownTypes()
                        .entryCapacity(Integer.MAX_VALUE)
                        .expireAfterWrite(k.longValue() * step, SECONDS)
                        .build()
        );
    }
}
