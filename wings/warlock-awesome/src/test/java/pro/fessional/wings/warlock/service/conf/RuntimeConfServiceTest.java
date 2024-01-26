package pro.fessional.wings.warlock.service.conf;

import io.qameta.allure.TmsLink;
import lombok.Data;
import lombok.Setter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.sql.init.dependency.DependsOnDatabaseInitialization;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import pro.fessional.mirana.time.Sleep;
import pro.fessional.wings.silencer.modulate.RunMode;
import pro.fessional.wings.slardar.cache.WingsCacheHelper;
import pro.fessional.wings.testing.silencer.TestingLoggerAssert;
import pro.fessional.wings.warlock.caching.CacheConst;
import pro.fessional.wings.warlock.service.conf.impl.RuntimeConfServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Need init database via BootDatabaseTest
 * Need sleep for cache evict
 *
 * @author trydofor
 * @since 2022-03-09
 */
@SpringBootTest(properties = {
        "logging.level.root=DEBUG", // AssertionLogger
        "wings.faceless.jooq.cud.table[win_conf_runtime]=key,current,handler",
})
@DependsOnDatabaseInitialization
class RuntimeConfServiceTest {

    @Setter(onMethod_ = {@Autowired})
    private RuntimeConfService runtimeConfService;

    @Test
    @TmsLink("C14008")
    void testSimple() {
        assertSimple(BigDecimal.class, new BigDecimal("10.00"));
        assertSimple(String.class, "string");
        // Note, lost precision, SSS
        final LocalDateTime ldt = LocalDateTime.of(2022, 2, 1, 12, 34, 56);
        assertSimple(LocalDateTime.class, ldt);
        assertSimple(ZonedDateTime.class, ZonedDateTime.of(ldt, ZoneId.of("Asia/Shanghai")));
        assertSimple(Long.class, 1023L);
        assertSimple(Integer.class, 10);
        //
        final Map<CacheManager, Set<String>> mgr = WingsCacheHelper.getManager(RuntimeConfServiceImpl.class);
        Assertions.assertEquals(1, mgr.size());
        final Set<String> names = mgr.values().iterator().next();
        Assertions.assertTrue(names.contains(CacheConst.RuntimeConfService.CacheManager));
        Assertions.assertTrue(names.contains(CacheConst.RuntimeConfService.CacheResolver));

        final Map<String, Set<String>> cas = WingsCacheHelper.getCacheMeta(RuntimeConfServiceImpl.class, "getObjectCache");
        final Set<String> v = cas.get(CacheConst.RuntimeConfService.CacheManager);
        Assertions.assertNotNull(v);
        Assertions.assertTrue(v.contains(CacheConst.RuntimeConfService.CacheName));
    }

    private <T> void assertSimple(Class<T> clz, T obj) {
        runtimeConfService.newObject(clz, obj, "test " + clz.getSimpleName());
        Sleep.ignoreInterrupt(1000); // wait for event sync
        final T obj1 = runtimeConfService.getSimple(clz, clz);
        Assertions.assertEquals(obj, obj1);
    }

    @Test
    @TmsLink("C14009")
    void testCollection() {
        List<String> ls = List.of("Jan", "Fer");
        runtimeConfService.newObject(List.class, ls, "test list");
        Sleep.ignoreInterrupt(1000); // wait for event sync
        final List<String> ls1 = runtimeConfService.getList(List.class, String.class);
        Assertions.assertEquals(ls, ls1);

        Map<String, Boolean> map = new HashMap<>();
        map.put("Jan", true);
        map.put("Fer", false);
        runtimeConfService.newObject(Map.class, map, "test map");
        Sleep.ignoreInterrupt(1000); // wait for event sync
        final Map<String, Boolean> map1 = runtimeConfService.getMap(Map.class, String.class, Boolean.class);
        Assertions.assertEquals(map, map1);
    }

    @Data
    public static class Dto {
        private String name = "Jackson";
        private Integer age = 24;
        private LocalDateTime ldt = null;
    }

    @Test
    @TmsLink("C14010")
    void testJson() {
        Dto dto = new Dto();
        runtimeConfService.newObject(Dto.class, dto, "Need init database via BootDatabaseTest");
        Sleep.ignoreInterrupt(1000); // wait for event sync
        final Dto dto1 = runtimeConfService.getSimple(Dto.class, Dto.class);
        Assertions.assertEquals(dto, dto1);
    }

    @Test
    @TmsLink("C14011")
    void testKryo() {
        Dto dto = new Dto();
        dto.setLdt(LocalDateTime.now());
        runtimeConfService.newObject(Dto.class, dto, "test dto", RuntimeConfServiceImpl.KryoHandler);
        Sleep.ignoreInterrupt(1000); // wait for event sync
        final Dto dto1 = runtimeConfService.getSimple(Dto.class, Dto.class);
        Assertions.assertEquals(dto, dto1);
    }

    @Test
    @TmsLink("C14012")
    void testMode() {
        final List<RunMode> arm = List.of(RunMode.Develop, RunMode.Local);
        final String key = "RuntimeConfServiceTest.testMode";
        runtimeConfService.newObject(key, arm, "test RunMode");
        Sleep.ignoreInterrupt(1000); // wait for event sync
        final List<RunMode> arm1 = runtimeConfService.getList(key, RunMode.class);
        Assertions.assertEquals(arm, arm1);

        runtimeConfService.setObject(key, RunMode.Develop);
        Sleep.ignoreInterrupt(1000); // wait for event sync
        final RunMode rm1 = runtimeConfService.getSimple(key, RunMode.class);
        Assertions.assertEquals(RunMode.Develop, rm1);
    }

    @Test
    @TmsLink("C14013")
    void testCacheWithCud() {
        final TestingLoggerAssert al = TestingLoggerAssert.install();
        al.rule("insert", event -> event.getFormattedMessage().contains("insert into `win_conf_runtime`"));
        al.rule("update", event -> event.getFormattedMessage().contains("update `win_conf_runtime`"));
        al.rule("evictAllConfCache", event -> event.getFormattedMessage().contains("evictAllConfCache by win_conf_runtime, TableChangeEvent"));
        al.start();

        final List<RunMode> arm = List.of(RunMode.Develop, RunMode.Local);
        final String key = "RuntimeConfCacheTest.testCache";
        // insert on duplicated key
        runtimeConfService.newObject(key, arm, "test RunMode");
        Sleep.ignoreInterrupt(1000); // wait for event sync
        final List<RunMode> arm1 = runtimeConfService.getList(key, RunMode.class);
        final List<RunMode> arm2 = runtimeConfService.getList(key, RunMode.class);

        // update `win_conf_runtime`
        // evictAllConfCache by win_conf_runtime, TableChangeEvent
        runtimeConfService.setObject(key, arm);
        Sleep.ignoreInterrupt(2_000);

        Assertions.assertTrue(al.getAssertCount("insert") > 0);
        Assertions.assertTrue(al.getAssertCount("update") > 0);
        Assertions.assertEquals(2, al.getAssertCount("evictAllConfCache"));
        al.uninstall();

        final List<RunMode> rm1 = runtimeConfService.getList(key, RunMode.class);
        final List<RunMode> rm2 = runtimeConfService.getList(key, RunMode.class);

        Assertions.assertEquals(arm, arm1);
        Assertions.assertSame(arm1, arm2);
        Assertions.assertNotSame(arm1, rm1);
        Assertions.assertSame(rm1, rm2);
    }
}
