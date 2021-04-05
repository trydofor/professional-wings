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
@SpringBootTest(properties = {
        "spring.datasource.url=" + Devops0ProjectConstant.JDBC_URL,
        "spring.datasource.username=" + Devops0ProjectConstant.JDBC_USER,
        "spring.datasource.password=" + Devops0ProjectConstant.JDBC_PASS,
        "debug = true"
})
@Disabled("手动初始化")
class Devops1SchemaManagerTest {

    @Setter(onMethod_ = {@Autowired})
    private SchemaRevisionManager schemaRevisionManager;

    @Test
    void initTail0315() {
        final Warlock1SchemaManager manager = new Warlock1SchemaManager(schemaRevisionManager);
//        manager.init(WingsRevision.V04_20_1024_02_RolePermit.revision(),
        manager.init(2021_0315_01L,
                Warlock1SchemaManager.includeWarlockPath(),
                FlywaveRevisionScanner.Helper::master
                );
    }

    @Test
    void resetRevi(){
        long revi = 2021_0315_01L;
        final Warlock1SchemaManager manager = new Warlock1SchemaManager(schemaRevisionManager);
        final FlywaveRevisionScanner.Helper helper = FlywaveRevisionScanner.helper();
        Warlock1SchemaManager.includeWarlockPath().accept(helper);
        helper.master();
        manager.forceDownThenMergePub(helper.scan(), 0, revi);
    }
}
