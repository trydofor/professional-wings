package pro.fessional.wings.tiny.project;

import lombok.Setter;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pro.fessional.wings.faceless.flywave.SchemaRevisionManager;
import pro.fessional.wings.faceless.project.ProjectJooqGenerator;
import pro.fessional.wings.faceless.project.ProjectSchemaManager;
import pro.fessional.wings.faceless.util.FlywaveRevisionScanner;


/**
 * @author trydofor
 * @since 2021-02-22
 */
@SpringBootTest(properties = {
        "spring.datasource.url=" + TinyTaskCodeGenTest.JDBC,
        "spring.datasource.username=" + TinyTaskCodeGenTest.USER,
        "spring.datasource.password=" + TinyTaskCodeGenTest.PASS,
        "spring.wings.faceless.flywave.enabled.module=true",
        "spring.wings.faceless.flywave.enabled.checker=false",
        "wings.tiny.task.enabled.autorun=false",
        "debug = true"
})
@Disabled("手动执行")
public class TinyTaskCodeGenTest {

    public static final String JDBC = "jdbc:mysql://localhost:3306/wings?connectionTimeZone=%2B08:00&forceConnectionTimeZoneToSession=true";
    public static final String USER = "trydofor";
    public static final String PASS = "moilioncircle";
    public static final String BASE = "../";

    @Setter(onMethod_ = {@Autowired})
    private SchemaRevisionManager schemaRevisionManager;

    @Test
    void initAll() {
        initMaster();
        genJooq();
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

    @Test
    public void genJooq() {
        ProjectJooqGenerator generator = new ProjectJooqGenerator();
        generator.setTargetDir(BASE + "tiny-task/src/main/java/");
        generator.setTargetPkg("pro.fessional.wings.tiny.task.database.autogen");
        generator.gen(JDBC, USER, PASS,
                bd -> bd.databaseIncludes("win_task_define", "win_task_result"),
                bd -> bd.setGlobalSuffix("TinyTask"));
    }

}
