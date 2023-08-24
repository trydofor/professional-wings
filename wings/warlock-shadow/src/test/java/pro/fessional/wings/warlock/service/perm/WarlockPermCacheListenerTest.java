package pro.fessional.wings.warlock.service.perm;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author trydofor
 * @since 2021-06-19
 */
@Slf4j
@SpringBootTest
class WarlockPermCacheListenerTest {

    @Setter(onMethod_ = {@Autowired})
    private WarlockPermService warlockPermServer;
    @Setter(onMethod_ = {@Autowired})
    private WarlockRoleService warlockRoleService;

    @Test
    @Disabled("Simulate slow processing and observe cache changes")
    void cleanCache() throws InterruptedException {
        log.warn("No cache, select Perm from Db");
        warlockPermServer.loadPermAll();
        log.warn("No cache, select Role from Db");
        warlockRoleService.loadRoleAll();
        log.warn("Cache, no Db select");
        warlockPermServer.loadPermAll();
        warlockRoleService.loadRoleAll();
        log.warn("Modify Perm=1, trigger jooq CUD event");
        warlockPermServer.modify(1, "test cleanCache");
        log.info("Sleep 3s, Wait async event");
        Thread.sleep(3000);
        log.warn("No cache, select Perm from Db");
        warlockPermServer.loadPermAll();
        log.warn("Cache, no Db select");
        warlockRoleService.loadRoleAll();
        log.warn("Check the log");
    }
}
