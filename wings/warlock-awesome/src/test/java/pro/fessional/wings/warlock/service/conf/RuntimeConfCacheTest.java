package pro.fessional.wings.warlock.service.conf;

import lombok.Setter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pro.fessional.mirana.time.Sleep;
import pro.fessional.wings.silencer.modulate.RunMode;

import java.util.List;

/**
 * Need init database via BootDatabaseTest
 *
 * @author trydofor
 * @since 2022-03-09
 */
@SpringBootTest(properties = {
        "wings.faceless.jooq.cud.table[win_conf_runtime]=-",
        "logging.level.root=debug"})
class RuntimeConfCacheTest {

    @Setter(onMethod_ = {@Autowired})
    private RuntimeConfService runtimeConfService;

    @Test
    void testCache() {
        final List<RunMode> arm = List.of(RunMode.Develop, RunMode.Local);
        final String key = "RuntimeConfCacheTest.testCache";
        runtimeConfService.newObject(key, arm, "test RunMode");
        final List<RunMode> arm1 = runtimeConfService.getList(key, RunMode.class);
        final List<RunMode> arm2 = runtimeConfService.getList(key, RunMode.class);

        runtimeConfService.setObject(key, arm);
        Sleep.ignoreInterrupt(2_000);
        // check log TableChangeEvent(source=[pro.fessional.wings.warlock.service.conf.impl.RuntimeConfServiceImpl]

        final List<RunMode> rm1 = runtimeConfService.getList(key, RunMode.class);
        final List<RunMode> rm2 = runtimeConfService.getList(key, RunMode.class);

        Assertions.assertEquals(arm, arm1);
        Assertions.assertSame(arm1, arm2);
        Assertions.assertNotSame(arm1, rm1);
        Assertions.assertSame(rm1, rm2);
    }
}
