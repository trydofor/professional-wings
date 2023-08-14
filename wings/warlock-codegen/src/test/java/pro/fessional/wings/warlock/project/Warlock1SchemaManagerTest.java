package pro.fessional.wings.warlock.project;

import lombok.Setter;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pro.fessional.wings.faceless.flywave.SchemaRevisionManager;
import pro.fessional.wings.faceless.flywave.WingsRevision;
import pro.fessional.wings.faceless.util.FlywaveRevisionScanner;

import java.util.SortedMap;

/**
 * @author trydofor
 * @since 2021-02-22
 */
@Disabled("Init database, project template, managed by devops")
@SpringBootTest(properties = {
        "spring.datasource.url=" + Warlock0CodegenConstant.JDBC,
        "spring.datasource.username=" + Warlock0CodegenConstant.USER,
        "spring.datasource.password=" + Warlock0CodegenConstant.PASS,
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
                                .include(2020_1024_03));
    }


    @Test
    void bugfixExecute() {
        final FlywaveRevisionScanner.Helper helper = FlywaveRevisionScanner.helper();
        helper.somefix("01-authn-fix");
        final SortedMap<Long, SchemaRevisionManager.RevisionSql> sqls = helper.scan();
        schemaRevisionManager.forceExecuteSql(sqls, true);
    }
}
