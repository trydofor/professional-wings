package com.moilioncircle.roshan.devops.project;

import lombok.Setter;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pro.fessional.wings.faceless.flywave.SchemaRevisionManager;
import pro.fessional.wings.faceless.util.FlywaveRevisionScanner;
import pro.fessional.wings.warlock.project.Warlock1SchemaManager;

/**
 * @author trydofor
 * @since 2021-02-22
 */
@Disabled("手动初始化")
@SpringBootTest(properties = {
        "spring.datasource.url=" + Devops0ProjectConstant.JDBC_URL,
        "spring.datasource.username=" + Devops0ProjectConstant.JDBC_USER,
        "spring.datasource.password=" + Devops0ProjectConstant.JDBC_PASS,
        "debug = true"
})
class Devops1SchemaManagerTest {

    @Setter(onMethod_ = {@Autowired})
    private SchemaRevisionManager schemaRevisionManager;

    @Test
    void initDemo() {
        final Warlock1SchemaManager manager = new Warlock1SchemaManager(schemaRevisionManager);
//        manager.init(WingsRevision.V04_20_1024_02_RolePermit.revision(),
        manager.init(9999_9999_01L,
                Warlock1SchemaManager.includeWarlockPath(),
                FlywaveRevisionScanner.Helper::master
        );
    }

    @Test
    void resetDemo() {
        long revi = 9999_9999_01L;
        final Warlock1SchemaManager manager = new Warlock1SchemaManager(schemaRevisionManager);
        final FlywaveRevisionScanner.Helper helper = FlywaveRevisionScanner.helper();
        Warlock1SchemaManager.includeWarlockPath().accept(helper);
        helper.master();
        manager.forceDownThenMergePub(helper.scan(), 0, revi);
    }
}
