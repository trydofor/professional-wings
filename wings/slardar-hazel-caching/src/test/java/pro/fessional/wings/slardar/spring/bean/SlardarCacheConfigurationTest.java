package pro.fessional.wings.slardar.spring.bean;

import io.qameta.allure.TmsLink;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pro.fessional.wings.slardar.app.service.TestMyCacheService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author trydofor
 * @since 2020-08-10
 */

@SpringBootTest(properties = {"wings.slardar.cache.level.general.maxLive=10"})
@Slf4j
public class SlardarCacheConfigurationTest {

    @Setter(onMethod_ = {@Autowired})
    private TestMyCacheService cacheService;

    @Test
    @TmsLink("C13022")
    public void cacheCall() {
        int c1 = cacheService.cacheMemory("cacheCall");
        assertEquals(1, c1);

        c1 = cacheService.cacheMemory("cacheCall");
        assertEquals(1, c1);

        int c2 = cacheService.cacheServer("cacheCall");
        assertEquals(2, c2);

        c2 = cacheService.cacheServer("cacheCall");
        assertEquals(2, c2);

        int c3 = cacheService.cachePrimary("cacheCall");
        assertEquals(1, c3);
    }

    @Test
    @TmsLink("C13023")
    @Disabled("Mock slow handling ttl=20")
    public void testTtl() throws InterruptedException {
        int c1 = cacheService.cacheMemory("cacheCall");
        assertEquals(1, c1);
        c1 = cacheService.cacheMemory("cacheCall");
        assertEquals(1, c1);

        int c2 = cacheService.cacheServer("cacheCall");
        assertEquals(2, c2);
        c2 = cacheService.cacheServer("cacheCall");
        assertEquals(2, c2);

        log.info("sleep 20 s");
        Thread.sleep(20 * 1000);

        c1 = cacheService.cacheMemory("cacheCall");
        c2 = cacheService.cacheServer("cacheCall");
        log.info("c1=" + c1 + ", c2=" + c2);

        assertTrue(c1 >= 2);
        assertTrue(c2 >= 3);
    }

    @Test
    @TmsLink("C13024")
    public void directCall() {
        int c1 = cacheService.directMemory("directCall");
        assertEquals(1, c1);

        c1 = cacheService.directMemory("directCall");
        assertEquals(2, c1);

        int c2 = cacheService.directServer("directCall");
        assertEquals(2, c2);

        c2 = cacheService.directServer("directCall");
        assertEquals(3, c2);
    }
}
