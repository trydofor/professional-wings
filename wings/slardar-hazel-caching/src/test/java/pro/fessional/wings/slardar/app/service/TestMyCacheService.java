package pro.fessional.wings.slardar.app.service;

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
@CacheConfig(cacheNames = Level.General + "TestMyCacheService")
@Service
public class TestMyCacheService {
    public static final ConcurrentHashMap<String, AtomicInteger> innerCount = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<String, AtomicInteger> outerCount = new ConcurrentHashMap<>();

    @Cacheable(cacheManager = Manager.Memory)
    public int cacheMemory(String key) {
        return directMemory(key);
    }

    public int directMemory(String key) {
        AtomicInteger n = innerCount.computeIfAbsent(key, s -> new AtomicInteger(0));
        return n.incrementAndGet();
    }

    @Cacheable(cacheManager = Manager.Server)
    public int cacheServer(String key) {
        return directMemory(key);
    }

    public int directServer(String key) {
        AtomicInteger n = outerCount.computeIfAbsent(key, s -> new AtomicInteger(0));
        return n.incrementAndGet();
    }

    @Cacheable
    public int cachePrimary(String key) {
        return directPrimary(key);
    }

    public int directPrimary(String key) {
        AtomicInteger n = outerCount.computeIfAbsent(key, s -> new AtomicInteger(0));
        return n.incrementAndGet();
    }
}
