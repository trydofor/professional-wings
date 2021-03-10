package pro.fessional.wings.slardar.service;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import pro.fessional.wings.slardar.cache.WingsCache.Level;
import pro.fessional.wings.slardar.cache.WingsCache.Manager;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author trydofor
 * @since 2020-08-10
 */
@CacheConfig(cacheNames = Level.General + "MyCacheService")
@Service
public class TestMyCacheService {
    public static ConcurrentHashMap<String, AtomicInteger> innerCount = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<String, AtomicInteger> outerCount = new ConcurrentHashMap<>();

    @Cacheable(cacheManager = Manager.Memory)
    public int cacheMemory(String email) {
        return directMemory(email);
    }

    public int directMemory(String email) {
        AtomicInteger n = innerCount.computeIfAbsent(email, s -> new AtomicInteger(0));
        return n.incrementAndGet();
    }

    @Cacheable(cacheManager = Manager.Server)
    public int cacheServer(String email) {
        return directMemory(email);
    }

    public int directServer(String email) {
        AtomicInteger n = outerCount.computeIfAbsent(email, s -> new AtomicInteger(1));
        return n.incrementAndGet();
    }

    @Cacheable
    public int cachePrimary(String email) {
        return directPrimary(email);
    }

    public int directPrimary(String email) {
        AtomicInteger n = outerCount.computeIfAbsent(email, s -> new AtomicInteger(1));
        return n.incrementAndGet();
    }
}
