package pro.fessional.wings.silencer.cache;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author trydofor
 * @since 2020-08-10
 */
@CacheConfig(cacheManager = WingsCache.Manager.CAFFEINE, cacheNames = WingsCache.Level.GENERAL + "MyCacheService")
@Service
public class MyCacheService {
    public static ConcurrentHashMap<String, AtomicInteger> counter = new ConcurrentHashMap<>();

    @Cacheable
    public int cacheMethod(String email) {
        return directMethod(email);
    }

    public int directMethod(String email) {
        AtomicInteger n = counter.computeIfAbsent(email, s -> new AtomicInteger(0));
        return n.incrementAndGet();
    }
}
