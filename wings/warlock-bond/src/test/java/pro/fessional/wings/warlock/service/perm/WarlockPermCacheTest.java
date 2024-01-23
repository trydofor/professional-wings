package pro.fessional.wings.warlock.service.perm;

import io.qameta.allure.TmsLink;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.sql.init.dependency.DependsOnDatabaseInitialization;
import org.springframework.boot.test.context.SpringBootTest;
import pro.fessional.wings.testing.silencer.TestingLoggerAssert;

/**
 * @author trydofor
 * @since 2021-06-19
 */
@Slf4j
@SpringBootTest(properties = {
        "logging.level.root=DEBUG", // AssertionLogger
})
@DependsOnDatabaseInitialization
class WarlockPermCacheTest {

    @Setter(onMethod_ = {@Autowired})
    private WarlockPermService warlockPermServer;
    @Setter(onMethod_ = {@Autowired})
    private WarlockRoleService warlockRoleService;

    @Test
    @TmsLink("C14064")
    void cleanCache() throws InterruptedException {
        TestingLoggerAssert al = TestingLoggerAssert.install();
        al.rule("loadPermAll", event -> event.getFormattedMessage().contains("loadPermAll size="));
        al.rule("loadRoleAll", event -> event.getFormattedMessage().contains("loadRoleAll size="));
        al.start();

        log.warn("No cache, select Perm from Db");
        warlockPermServer.loadPermAll();
        log.warn("No cache, select Role from Db");
        warlockRoleService.loadRoleAll();

        log.warn("Cache, no Db select");
        warlockPermServer.loadPermAll();
        warlockRoleService.loadRoleAll();

        log.warn("Modify Perm=1, trigger jooq CUD event");
        warlockPermServer.modify(1, "test cleanCache");
        log.info("Sleep 2s, Wait async event");
        Thread.sleep(2000);

        log.warn("No cache, select Perm from Db");
        warlockPermServer.loadPermAll();
        log.warn("Cache, no Db select");
        warlockRoleService.loadRoleAll();
        log.warn("Check the log");

        Assertions.assertEquals(2, al.getAssertCount("loadPermAll"));
        Assertions.assertEquals(1, al.getAssertCount("loadRoleAll"));
        al.uninstall();
    }
}
