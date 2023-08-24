package com.moilioncircle.wings.devops.project;

import lombok.Setter;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pro.fessional.wings.faceless.flywave.SchemaRevisionManager;
import pro.fessional.wings.faceless.util.FlywaveRevisionScanner;
import pro.fessional.wings.faceless.util.FlywaveRevisionScanner.Helper;
import pro.fessional.wings.warlock.project.Warlock1SchemaManager;

import static pro.fessional.wings.warlock.project.Warlock1SchemaManager.includeWarlockPath;

/**
 * @author trydofor
 * @since 2021-02-22
 */
@Disabled("Database Version")
@SpringBootTest(properties = {
        "spring.datasource.url=" + Devops0ProjectConstant.JDBC_URL,
        "spring.datasource.username=" + Devops0ProjectConstant.JDBC_USER,
        "spring.datasource.password=" + Devops0ProjectConstant.JDBC_PASS,
})
class Devops1SchemaManagerTest {

    @Setter(onMethod_ = {@Autowired})
    private SchemaRevisionManager schemaRevisionManager;

    @Test
    void initSchema() {
        final Warlock1SchemaManager manager = new Warlock1SchemaManager(schemaRevisionManager);
//        manager.init(WingsRevision.V04_20_1024_02_RolePermit.revision(),
        manager.mergePublish(9999_9999_01L,
                includeWarlockPath(),
                Helper::master
        );
    }

    @Test
    void resetSchema() {
        long revi = 9999_9999_01L;
        final Warlock1SchemaManager manager = new Warlock1SchemaManager(schemaRevisionManager);
        final Helper helper = FlywaveRevisionScanner.helper();
        includeWarlockPath().accept(helper);
        helper.master();
        manager.downThenMergePublish(helper.scan(), 0, revi);
    }

    @Test
    void forceApply() {
        final Warlock1SchemaManager manager = new Warlock1SchemaManager(schemaRevisionManager);
        final Helper helper = FlywaveRevisionScanner.helper()
                .master("05-conf");
        manager.mergeForceApply(helper.scan(), 0, true);
    }
}
