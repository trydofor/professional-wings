package pro.fessional.wings.slardar.app.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import pro.fessional.wings.slardar.cache.WingsCache;
import pro.fessional.wings.slardar.cache.spring.CacheEvictKey;

/**
 * @author trydofor
 * @since 2022-12-03
 */
@Service
@Slf4j
@CacheConfig(cacheNames = WingsCache.Level.Service + "TestCachingService", cacheManager = WingsCache.Manager.Memory)
public class TestCachingService {

    @Cacheable
    public Key cache(Key key) {
        return key;
    }

    @CacheEvict(key = "#result")
    public CacheEvictKey evict(CacheEvictKey keys) {
        return keys;
    }

    public record Key(String key) {}
}
