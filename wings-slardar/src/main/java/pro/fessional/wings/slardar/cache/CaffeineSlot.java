package pro.fessional.wings.slardar.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * @author trydofor
 * @since 2021-03-16
 */
public class CaffeineSlot {

    private final Map<Integer, Cache<Object, Object>> slot;
    private final int step;
    private final int max;

    /**
     * 以ConcurrentHashMap构造一个按ttl分片的缓存
     *
     * @param ttl  最大ttl秒
     * @param step 分片步长秒
     */
    public CaffeineSlot(int ttl, int step) {
        this(new ConcurrentHashMap<>(), ttl, step);
    }

    /**
     * 构造一个按ttl分片的缓存
     *
     * @param slot  slot
     * @param ttl  最大ttl秒
     * @param step 分片步长秒
     */
    public CaffeineSlot(Map<Integer, Cache<Object, Object>> slot, int ttl, int step) {
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
        } else if (slot >= max) {
            slot = max;
        }
        return this.slot.computeIfAbsent(slot,
                k -> Caffeine.newBuilder()
                             .maximumSize(Integer.MAX_VALUE)
                             .expireAfterWrite(k * step, SECONDS)
                             .build()
        );
    }
}
