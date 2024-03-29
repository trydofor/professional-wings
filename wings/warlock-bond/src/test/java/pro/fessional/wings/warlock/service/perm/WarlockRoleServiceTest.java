package pro.fessional.wings.warlock.service.perm;

import io.qameta.allure.TmsLink;
import lombok.Setter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.sql.init.dependency.DependsOnDatabaseInitialization;
import org.springframework.boot.test.context.SpringBootTest;
import pro.fessional.mirana.time.Sleep;

import java.util.Map;

/**
 * @author trydofor
 * @since 2023-07-03
 */
@SpringBootTest(properties = {
        "wings.faceless.jooq.cud.table[win_role_entry]=-",
})
@DependsOnDatabaseInitialization
class WarlockRoleServiceTest {

    @Setter(onMethod_ = {@Autowired})
    protected WarlockRoleService warlockRoleService;

    @Test
    @TmsLink("C14017")
    void loadRoleAll() {
        final Map<Long, String> a1 = warlockRoleService.loadRoleAll();
        final Map<Long, String> a2 = warlockRoleService.loadRoleAll();
        Assertions.assertSame(a1, a2);

        warlockRoleService.modify(1, "super user");
        Sleep.ignoreInterrupt(2_000);

        final Map<Long, String> a3 = warlockRoleService.loadRoleAll();
        Assertions.assertNotSame(a1, a3);
    }
}
