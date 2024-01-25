package pro.fessional.wings.slardar.spring.bean;

import com.hazelcast.spring.cache.HazelcastCacheManager;
import io.qameta.allure.TmsLink;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.cache2k.extra.spring.SpringCache2kCacheManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pro.fessional.mirana.time.Sleep;
import pro.fessional.wings.slardar.app.service.TestMyCacheService;
import pro.fessional.wings.slardar.cache.WingsCacheHelper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

/**
 * @author trydofor
 * @since 2020-08-10
 */

@SpringBootTest
@Slf4j
public class SlardarCacheConfigurationTest {

    @Setter(onMethod_ = {@Autowired})
    private TestMyCacheService cacheService;

    @Test
    @TmsLink("C13022")
    public void cacheCall() {

        assertInstanceOf(SpringCache2kCacheManager.class, WingsCacheHelper.getMemory());
        assertInstanceOf(HazelcastCacheManager.class, WingsCacheHelper.getServer());

        String keyMemory = "cacheCall.cacheMemory";
        int c1 = cacheService.cacheMemory(keyMemory);
        assertEquals(1, c1);
        c1 = cacheService.cacheMemory(keyMemory);
        assertEquals(1, c1);

        String keyServer = "cacheCall.cacheServer";
        int c2 = cacheService.cacheServer(keyServer);
        assertEquals(1, c2);
        c2 = cacheService.cacheServer(keyServer);
        assertEquals(1, c2);

        //
        int c3 = cacheService.cachePrimary(keyMemory);
        assertEquals(c1, c3);
        c3 = cacheService.cachePrimary(keyMemory);
        assertEquals(c1, c3);
    }

    @Test
    @TmsLink("C13023")
    public void timeoutTtl() {
        String keyMemory = "timeoutTtl.cacheMemory";
        int c1 = cacheService.cacheMemory(keyMemory);
        assertEquals(1, c1);
        c1 = cacheService.cacheMemory(keyMemory);
        assertEquals(1, c1);

        String keyServer = "timeoutTtl.cacheServer";
        int c2 = cacheService.cacheServer(keyServer);
        assertEquals(1, c2);
        c2 = cacheService.cacheServer(keyServer);
        assertEquals(1, c2);

        log.debug("sleep 15 s for cache ttl");
        Sleep.ignoreInterrupt(15_000);

        c1 = cacheService.cacheMemory(keyMemory);
        c2 = cacheService.cacheServer(keyServer);
        log.info("c1={}, c2={}", c1, c2);

        assertEquals(2, c1, "memory cache ttl");
        assertEquals(2, c2, "server cache ttl");
    }

    @Test
    @TmsLink("C13024")
    public void directCall() {
        String keyMemory = "directCall.directMemory";
        int c1 = cacheService.directMemory(keyMemory);
        assertEquals(1, c1);

        c1 = cacheService.directMemory(keyMemory);
        assertEquals(2, c1);

        String keyServer = "directCall.directServer";
        int c2 = cacheService.directServer(keyServer);
        assertEquals(1, c2);

        c2 = cacheService.directServer(keyServer);
        assertEquals(2, c2);
    }
}
