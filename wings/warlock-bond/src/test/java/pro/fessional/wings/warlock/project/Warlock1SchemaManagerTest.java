package pro.fessional.wings.warlock.project;

import lombok.Setter;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pro.fessional.wings.faceless.flywave.SchemaRevisionManager;
import pro.fessional.wings.faceless.flywave.WingsRevision;

/**
 * @author trydofor
 * @since 2021-02-22
 */
@Disabled("手动初始化")
@SpringBootTest(properties = {
        "spring.datasource.url=" + Warlock0CodegenConstant.JDBC,
        "spring.datasource.username=" + Warlock0CodegenConstant.USER,
        "spring.datasource.password=" + Warlock0CodegenConstant.PASS,
        "debug = true"
})
class Warlock1SchemaManagerTest {

    @Setter(onMethod_ = {@Autowired})
    private SchemaRevisionManager schemaRevisionManager;

    @Test
    void init04AuthMain() {
        final Warlock1SchemaManager manager = new Warlock1SchemaManager(schemaRevisionManager);
        manager.mergeForceApply(true,
                h -> h.path(WingsRevision.V04_20_1024_01_UserLogin)
                        .include(WingsRevision.V04_20_1024_01_UserLogin)
                        .include(WingsRevision.V04_20_1024_02_RolePermit)
        );
    }

    @Test
    void init04AuthTest() {
        final Warlock1SchemaManager manager = new Warlock1SchemaManager(schemaRevisionManager);
        manager.mergeForceApply(true,
                h -> h.branch("test/")
                                .include(2020_10_24_03));
    }

}
