package pro.fessional.wings.slardar.spring.bean;

import io.qameta.allure.TmsLink;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pro.fessional.mirana.time.Sleep;
import pro.fessional.wings.slardar.app.service.TestMyCacheService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author trydofor
 * @since 2020-08-10
 */

@SpringBootTest(properties = {
        "wings.slardar.cache.level.general.max-live=5"
})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Slf4j
public class SlardarCacheConfigurationTest {

    @Setter(onMethod_ = {@Autowired})
    private TestMyCacheService cacheService;

    @Test
    @org.junit.jupiter.api.Order(1)
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
    @org.junit.jupiter.api.Order(2)
    public void testTtl() {
        int c1 = cacheService.cacheMemory("cacheCall");
        assertEquals(1, c1);
        c1 = cacheService.cacheMemory("cacheCall");
        assertEquals(1, c1);

        int c2 = cacheService.cacheServer("cacheCall");
        assertEquals(2, c2);
        c2 = cacheService.cacheServer("cacheCall");
        assertEquals(2, c2);

        log.info("sleep 10 s");
        Sleep.ignoreInterrupt(10000);

        c1 = cacheService.cacheMemory("cacheCall");
        c2 = cacheService.cacheServer("cacheCall");
        log.info("c1=" + c1 + ", c2=" + c2);

        assertTrue(c1 >= 2);
        assertTrue(c2 >= 3);
    }

    @Test
    @TmsLink("C13024")
    @org.junit.jupiter.api.Order(3)
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
