package pro.fessional.wings.tiny.project;

import lombok.Setter;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pro.fessional.wings.faceless.flywave.SchemaRevisionManager;
import pro.fessional.wings.faceless.project.ProjectSchemaManager;
import pro.fessional.wings.faceless.util.FlywaveRevisionScanner;


/**
 * @author trydofor
 * @since 2021-02-22
 */
@SpringBootTest(properties = {
        "spring.wings.faceless.flywave.enabled.module=true",
        "spring.wings.faceless.flywave.enabled.checker=false",
        "spring.wings.tiny.task.enabled.autorun=false",
})
@Disabled("生成代码，已有devs统一管理")
public class TinyTaskCodeGenTest {

    @Setter(onMethod_ = {@Autowired})
    private SchemaRevisionManager schemaRevisionManager;

    @Test
    void initAll() {
        initMaster();
    }

    @Test
    void initMaster() {
        final ProjectSchemaManager manager = new ProjectSchemaManager(schemaRevisionManager);
        final FlywaveRevisionScanner.Helper helper = FlywaveRevisionScanner.helper();
        helper.master().exclude(2020_1023_01);
        manager.downThenMergePublish(helper.scan(), 0, 2020_1026_01L);

//        manager.mergeForceApply(true,
//                hp -> hp.master().exclude(2020_1023_01)
//        );
    }

}
