package pro.fessional.wings.tiny.project;

import lombok.Setter;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pro.fessional.wings.faceless.flywave.SchemaRevisionManager;
import pro.fessional.wings.faceless.project.ProjectJooqGenerator;
import pro.fessional.wings.faceless.project.ProjectSchemaManager;


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
        "debug = true"
})
public class TinyTaskCodeGenTest {

    public static final String JDBC = "jdbc:mysql://localhost:3306/wings_radiant?connectionTimeZone=%2B08:00&forceConnectionTimeZoneToSession=true";
    public static final String USER = "trydofor";
    public static final String PASS = "moilioncircle";
    public static final String BASE = "../";

    @Setter(onMethod_ = {@Autowired})
    private SchemaRevisionManager schemaRevisionManager;

    @Test
    @Disabled
    void initAll() {
        initMaster();
        genJooq();
    }

    @Test
    @Disabled
    void initMaster() {
        final ProjectSchemaManager manager = new ProjectSchemaManager(schemaRevisionManager);
        manager.mergeForceApply(true,
                hp -> hp.master().exclude(2020_1023_01)
        );
    }

    @Test
    @Disabled
    public void genJooq() {
        ProjectJooqGenerator generator = new ProjectJooqGenerator();
        generator.setTargetDir(BASE + "tiny-task/src/main/java/");
        generator.setTargetPkg("pro.fessional.wings.tiny.task.database.autogen");
        generator.gen(JDBC, USER, PASS,
                bd -> bd.databaseIncludes("win_task_delayed"),
                bd -> bd.setGlobalSuffix("TinyTask"));
    }

}
