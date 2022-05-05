package pro.fessional.wings.warlock.service.conf;

import lombok.Data;
import lombok.Setter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import pro.fessional.wings.slardar.cache.WingsCacheHelper;
import pro.fessional.wings.warlock.caching.CacheConst;
import pro.fessional.wings.warlock.service.conf.impl.RuntimeConfServiceImpl;
import pro.fessional.wings.warlock.service.conf.mode.RunMode;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author trydofor
 * @since 2022-03-09
 */
@SpringBootTest(properties = {"debug = true", "logging.level.pro.fessional=DEBUG"})
class RuntimeConfServiceTest {

    @Setter(onMethod_ = {@Autowired})
    private RuntimeConfService runtimeConfService;

    @Test
    void testSimple() {
        assertSimple(BigDecimal.class, new BigDecimal("10.00"));
        assertSimple(String.class, "string");
        // 注意，丢失精度 SSS
        final LocalDateTime ldt = LocalDateTime.of(2022, 2, 1, 12, 34, 56);
        assertSimple(LocalDateTime.class, ldt);
        assertSimple(ZonedDateTime.class, ZonedDateTime.of(ldt, ZoneId.of("Asia/Shanghai")));
        assertSimple(Long.class, 1023L);
        assertSimple(Integer.class, 10);
        //
        final Map<String, CacheManager> mgr = WingsCacheHelper.getManager(RuntimeConfServiceImpl.class);
        Assertions.assertTrue(mgr.containsKey(CacheConst.RuntimeConfService.CacheManager));

        final Map<String, Set<String>> cas = WingsCacheHelper.getCacheMeta(RuntimeConfServiceImpl.class, "getObject");
        final Set<String> v = cas.get(CacheConst.RuntimeConfService.CacheManager);
        Assertions.assertNotNull(v);
        Assertions.assertTrue(v.contains(CacheConst.RuntimeConfService.CacheName));
    }

    <T> void assertSimple(Class<T> clz, T obj) {
        runtimeConfService.newObject(clz, obj, "test " + clz.getSimpleName());
        final T obj1 = runtimeConfService.getSimple(clz, clz);
        Assertions.assertEquals(obj, obj1);
    }

    @Test
    void testCollection() {
        List<String> ls = List.of("Jan", "Fer");
        runtimeConfService.newObject(List.class, ls, "test list");
        final List<String> ls1 = runtimeConfService.getList(List.class, String.class);
        Assertions.assertEquals(ls, ls1);

        Map<String, Boolean> map = new HashMap<>();
        map.put("Jan", true);
        map.put("Fer", false);
        runtimeConfService.newObject(Map.class, map, "test map");
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
    void testJson() {
        Dto dto = new Dto();
        runtimeConfService.newObject(Dto.class, dto, "test dto");
        final Dto dto1 = runtimeConfService.getSimple(Dto.class, Dto.class);
        Assertions.assertEquals(dto, dto1);
    }

    @Test
    void testKryo() {
        Dto dto = new Dto();
        dto.setLdt(LocalDateTime.now());
        runtimeConfService.newObject(Dto.class, dto, "test dto", RuntimeConfServiceImpl.KryoHandler);
        final Dto dto1 = runtimeConfService.getSimple(Dto.class, Dto.class);
        Assertions.assertEquals(dto, dto1);
    }

    @Test
    void testMode() {
        final List<RunMode> arm = List.of(RunMode.Develop, RunMode.Local);
        runtimeConfService.newObject(RunMode.class, arm, "test RunMode");
        final List<RunMode> arm1 = runtimeConfService.getEnums(RunMode.class);
        Assertions.assertEquals(arm, arm1);

        runtimeConfService.setObject(RunMode.class, RunMode.Develop);
        final RunMode rm1 = runtimeConfService.getEnum(RunMode.class);
        Assertions.assertEquals(RunMode.Develop, rm1);
    }
}
