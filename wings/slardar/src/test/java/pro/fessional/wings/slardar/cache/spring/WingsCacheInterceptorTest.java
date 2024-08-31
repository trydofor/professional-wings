package pro.fessional.wings.slardar.cache.spring;

import io.qameta.allure.TmsLink;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import pro.fessional.wings.slardar.app.service.TestCachingService;
import pro.fessional.wings.slardar.app.service.TestCachingService.Key;
import pro.fessional.wings.slardar.cache.SimpleCacheTemplate;
import pro.fessional.wings.slardar.cache.WingsCache;

/**
 * @author trydofor
 * @since 2024-02-29
 */
@SpringBootTest
@Slf4j
class WingsCacheInterceptorTest {

    private final SimpleCacheTemplate<Key> cacheTemplate = new SimpleCacheTemplate<>(
        WingsCache.Manager.Memory, TestCachingService.CacheName);

    @Setter(onMethod_ = { @Autowired })
    protected TestCachingService testCachingService;

    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.cacheTemplate.setBeanFactory(applicationContext);
    }

    @Test
    @TmsLink("C13119")
    void doEvict() {
        Key k1a = new Key("key1");
        Key k1b = new Key("key" + 1);

        {
            Key v1a = testCachingService.cache(k1a);
            Assertions.assertSame(k1a, v1a);

            Key v1b = testCachingService.cache(k1b);
            Assertions.assertSame(k1a, v1b);

            testCachingService.evict(new CacheEvictKey());
            Key v1c = testCachingService.cache(k1b);
            Assertions.assertSame(k1b, v1c);
        }

        Key k2a = new Key("key2");
        Key k2b = new Key("key" + 2);
        {
            Key v2a = testCachingService.cache(k2a);
            Assertions.assertSame(k2a, v2a);

            testCachingService.evict(new CacheEvictKey().add(k1a).add(k2a));

            Key v1a = testCachingService.cache(k1a);
            Assertions.assertSame(k1a, v1a);

            Key v2b = testCachingService.cache(k2b);
            Assertions.assertSame(k2b, v2b);
        }

        Key k3a = new Key("key3");
        Key k3b = new Key("key" + 3);
        Key k3c = new Key("0key3".substring(1));

        {
            // assert same cache
            Key v2b = cacheTemplate.getArgKey(k2a);
            Assertions.assertSame(k2b, v2b);

            // assert action
            cacheTemplate.putArgKey(k3a, k3a);
            Key v3a = cacheTemplate.getArgKey(k3b);
            Assertions.assertSame(k3a, v3a);

            cacheTemplate.evictArgKey(k3a);
            Key v3c = cacheTemplate.getArgKey(() -> k3c, k3b);
            Assertions.assertSame(k3c, v3c);
        }
    }
}