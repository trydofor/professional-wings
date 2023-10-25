package pro.fessional.wings.warlock.service.perm;

import io.qameta.allure.TmsLink;
import lombok.Setter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pro.fessional.mirana.time.Sleep;

import java.util.Map;

/**
 * @author trydofor
 * @since 2023-07-03
 */
@SpringBootTest(properties = {"logging.level.root=debug"})
class WarlockPermServiceTest {

    @Setter(onMethod_ = {@Autowired})
    protected WarlockPermService warlockPermService;

    @Test
    @TmsLink("C14016")
    void loadPermAll() {
        final Map<Long, String> a1 = warlockPermService.loadPermAll();
        final Map<Long, String> a2 = warlockPermService.loadPermAll();
        Assertions.assertSame(a1, a2);

        warlockPermService.modify(1, "super user");
        Sleep.ignoreInterrupt(2_000);

        final Map<Long, String> a3 = warlockPermService.loadPermAll();
        Assertions.assertNotSame(a1, a3);
    }
}
