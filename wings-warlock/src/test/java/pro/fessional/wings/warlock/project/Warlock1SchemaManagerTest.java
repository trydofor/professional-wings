package pro.fessional.wings.warlock.project;

import lombok.Setter;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pro.fessional.wings.faceless.flywave.SchemaRevisionManager;

/**
 * @author trydofor
 * @since 2021-02-22
 */
@SpringBootTest(properties = {
        "spring.datasource.url=" + Warlock0CodegenConstant.JDBC,
        "spring.datasource.username=" + Warlock0CodegenConstant.USER,
        "spring.datasource.password=" + Warlock0CodegenConstant.PASS,
        "debug = true"
})
@Disabled("手动初始化")
class Warlock1SchemaManagerTest {

    @Setter(onMethod_ = {@Autowired})
    private SchemaRevisionManager schemaRevisionManager;

    @Test
    void init04Auth() {
        final Warlock1SchemaManager manager = new Warlock1SchemaManager(schemaRevisionManager);
        manager.init04Auth();
    }

}
