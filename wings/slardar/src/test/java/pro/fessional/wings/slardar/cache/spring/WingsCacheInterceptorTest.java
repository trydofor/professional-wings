package pro.fessional.wings.slardar.cache.spring;

import io.qameta.allure.TmsLink;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pro.fessional.wings.slardar.app.service.TestCachingService;

/**
 * @author trydofor
 * @since 2024-02-29
 */
@SpringBootTest
@Slf4j
class WingsCacheInterceptorTest {

    @Setter(onMethod_ = {@Autowired})
    protected TestCachingService testCachingService;

    @Test
    @TmsLink("C13119")
    void doEvict() {
        TestCachingService.Key k1a = new TestCachingService.Key("key1");
        TestCachingService.Key k1b = new TestCachingService.Key("key" + 1);

        {
            TestCachingService.Key v1a = testCachingService.cache(k1a);
            Assertions.assertSame(k1a, v1a);

            TestCachingService.Key v1b = testCachingService.cache(k1b);
            Assertions.assertSame(k1a, v1b);

            testCachingService.evict(new CacheEvictKey());
            TestCachingService.Key v1c = testCachingService.cache(k1b);
            Assertions.assertSame(k1b, v1c);
        }

        TestCachingService.Key k2a = new TestCachingService.Key("key2");
        TestCachingService.Key k2b = new TestCachingService.Key("key" + 2);
        {
            TestCachingService.Key v2a = testCachingService.cache(k2a);
            Assertions.assertSame(k2a, v2a);

            testCachingService.evict(new CacheEvictKey().add(k1a).add(k2a));

            TestCachingService.Key v1a = testCachingService.cache(k1a);
            Assertions.assertSame(k1a, v1a);

            TestCachingService.Key v2b = testCachingService.cache(k2b);
            Assertions.assertSame(k2b, v2b);
        }
    }
}