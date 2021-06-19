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
@SpringBootTest(properties = "logging.level.root=DEBUG")
class WarlockPermCacheListenerTest {

    @Setter(onMethod_ = {@Autowired})
    private WarlockPermService warlockPermServer;

    @Test
    @Disabled("手动看日志")
    void cleanCache() throws InterruptedException {
        log.info("无缓存，从数据库加载");
        warlockPermServer.loadPermAll();
        log.info("有缓存，无数据库加载");
        warlockPermServer.loadPermAll();
        log.info("修改Perm=1，触发jooq CUD事件");
        warlockPermServer.modify(1, "test cleanCache");
        log.info("睡眠3秒，等待async事件");
        Thread.sleep(3000);
        log.info("无缓存，从数据库加载");
        warlockPermServer.loadPermAll();
        log.info("查看日志");
    }
}
