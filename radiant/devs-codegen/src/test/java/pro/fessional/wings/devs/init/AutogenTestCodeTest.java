package pro.fessional.wings.devs.init;

import lombok.Setter;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import pro.fessional.wings.faceless.enums.autogen.StandardLanguage;
import pro.fessional.wings.warlock.project.Warlock3JooqGenerator;

import javax.sql.DataSource;

/**
 * @author trydofor
 * @since 2023-01-23
 */
@SpringBootTest(properties = "testing.dbname=wings_faceless")
@EnabledIfSystemProperty(named = "devs-autogen", matches = "true")
@TestMethodOrder(MethodOrderer.MethodName.class)
public class AutogenTestCodeTest {
    @Setter(onMethod_ = { @Autowired })
    private DataSource dataSource;
    @Setter(onMethod_ = { @Value("${spring.datasource.url}") })
    private String jdbcUrl;
    @Setter(onMethod_ = { @Value("${spring.datasource.username}") })
    private String jdbcUser;
    @Setter(onMethod_ = { @Value("${spring.datasource.password}") })
    private String jdbcPass;

    private final String projectRoot = "../../";

    @Test
    void autogen01AllTestJooq() {
        // use wings_faceless database
        autogen11FacelessJooqTest(); // faceless-jooq
        autogen11FacelessShardTest(); // faceless-shard
    }


    // ////////////////// individual test  //////////////////

    void autogen11FacelessJooqTest() {
        Warlock3JooqGenerator generator = new Warlock3JooqGenerator();
        generator.setTargetDir(projectRoot + "wings/faceless-jooq/src/test/java/");
        generator.setTargetPkg("pro.fessional.wings.faceless.app.database.autogen");
        generator.gen(jdbcUrl, jdbcUser, jdbcPass,
            h -> h.databaseIncludes("sys_constant_enum", "sys_standard_i18n", "tst_sharding", "tst_normal_table")
                  .forcedIntConsEnum(StandardLanguage.class, "tst_sharding.language")
                  .forcedIntConsEnum(StandardLanguage.class, "tst_normal_table.value_lang")
        );
    }

    void autogen11FacelessShardTest() {
        Warlock3JooqGenerator generator = new Warlock3JooqGenerator();
        generator.setTargetDir(projectRoot + "wings/faceless-shard/src/test/java/");
        generator.setTargetPkg("pro.fessional.wings.faceless.app.database.autogen");
        generator.gen(jdbcUrl, jdbcUser, jdbcPass,
            h -> h.databaseIncludes("tst_sharding", "tst_normal_table"));
    }
}
