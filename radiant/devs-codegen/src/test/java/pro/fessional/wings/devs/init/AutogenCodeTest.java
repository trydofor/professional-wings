package pro.fessional.wings.devs.init;

import lombok.Setter;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import pro.fessional.wings.faceless.codegen.ConstantEnumGenerator;
import pro.fessional.wings.faceless.codegen.ConstantEnumJdbcLoader;
import pro.fessional.wings.faceless.codegen.JdbcDataLoadHelper;
import pro.fessional.wings.faceless.enums.autogen.StandardLanguage;
import pro.fessional.wings.faceless.project.ProjectEnumGenerator;
import pro.fessional.wings.faceless.project.ProjectJooqGenerator;
import pro.fessional.wings.warlock.project.Warlock2EnumGenerator;
import pro.fessional.wings.warlock.project.Warlock3JooqGenerator;
import pro.fessional.wings.warlock.project.Warlock4AuthGenerator;

import javax.sql.DataSource;
import java.util.List;

/**
 * @author trydofor
 * @since 2023-01-23
 */
@SpringBootTest(properties = "testing.dbname=wings")
@EnabledIfSystemProperty(named = "devs-autogen", matches = "true")
@TestMethodOrder(MethodOrderer.MethodName.class)
public class AutogenCodeTest {
    @Setter(onMethod_ = {@Autowired})
    private DataSource dataSource;
    @Setter(onMethod_ = {@Value("${spring.datasource.url}")})
    private String jdbcUrl;
    @Setter(onMethod_ = {@Value("${spring.datasource.username}")})
    private String jdbcUser;
    @Setter(onMethod_ = {@Value("${spring.datasource.password}")})
    private String jdbcPass;

    private final String projectRoot = "../../";

    @Test
    void autogen01AllTestJooq() {
        autogen11FacelessJooqTest();// faceless-jooq
        autogen11FacelessShardTest();// faceless-shard
    }

    @Test
    void autogen01AllMainCode() {
        autogen01AllMainJooq();

        autogen10FacelessAutogen();// faceless/enums
        autogen20WarlockAutogenEnum();// warlock/enums
        autogen20WarlockAutogenAuth();// warlock/security
    }

    @Test
    @Disabled("call by autogen01AllMainCode")
    void autogen01AllMainJooq() {
        autogen21WarlockAutogenJooq();// warlock/database
        autogen31TinyMailAutogenJooq();// tiny/mail
        autogen31TinyTaskAutogenJooq();// tiny/task
    }

    // ////////////////// individual test  //////////////////

    @Test
    @Disabled("call by autogen01AllMainCode")
    void autogen10FacelessAutogen() {
        final JdbcDataLoadHelper helper = JdbcDataLoadHelper.use(dataSource);
        final List<ConstantEnumGenerator.ConstantEnum> enums = ConstantEnumJdbcLoader.load(helper);
        ConstantEnumGenerator.builder()
                             .targetDirectory(projectRoot + "wings/faceless/src/main/java-gen/")
                             .targetPackage("pro.fessional.wings.faceless.enums.autogen")
                             .includeType(ProjectEnumGenerator.facelessEnums)
                             .generate(enums);
    }

    @Test
    @Disabled("call by autogen01AllTestJooq")
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

    @Test
    @Disabled("call by autogen01AllTestJooq")
    void autogen11FacelessShardTest() {
        Warlock3JooqGenerator generator = new Warlock3JooqGenerator();
        generator.setTargetDir(projectRoot + "wings/faceless-shard/src/test/java/");
        generator.setTargetPkg("pro.fessional.wings.faceless.app.database.autogen");
        generator.gen(jdbcUrl, jdbcUser, jdbcPass,
                h -> h.databaseIncludes("tst_sharding", "tst_normal_table"));
    }

    @Test
    @Disabled("call by autogen01AllMainCode")
    void autogen20WarlockAutogenEnum() {
        Warlock2EnumGenerator generator = new Warlock2EnumGenerator();
        generator.setTargetDir(projectRoot + "wings/warlock/src/main/java-gen/");
        generator.setTargetPkg("pro.fessional.wings.warlock.enums.autogen");
        generator.gen(jdbcUrl, jdbcUser, jdbcPass,
                h -> h.includeType(Warlock2EnumGenerator.warlockEnums));
    }

    @Test
    @Disabled("call by autogen01AllMainCode")
    void autogen20WarlockAutogenAuth() {
        JdbcDataLoadHelper helper = JdbcDataLoadHelper.use(jdbcUrl, jdbcUser, jdbcPass);
        Warlock4AuthGenerator generator = new Warlock4AuthGenerator();
        generator.setTargetDir(projectRoot + "wings/warlock/src/main/java-gen/");
        generator.setTargetPkg("pro.fessional.wings.warlock.security.autogen");
        generator.genPerm(helper);
        generator.genRole(helper);
    }

    @Test
    @Disabled("call by autogen01AllMainJooq")
    void autogen21WarlockAutogenJooq() {
        Warlock3JooqGenerator generator = new Warlock3JooqGenerator();
        generator.setTargetDir(projectRoot + "wings/warlock/src/main/java-gen/");
        generator.setTargetPkg("pro.fessional.wings.warlock.database.autogen");
        generator.gen(jdbcUrl, jdbcUser, jdbcPass,
                Warlock3JooqGenerator.includeWarlockBase(false),
                Warlock3JooqGenerator.includeWarlockBond(true),
                bd -> bd.setGlobalSuffix("Warlock"));
    }

    @Test
    @Disabled("call by autogen01AllMainJooq")
    void autogen31TinyMailAutogenJooq() {
        ProjectJooqGenerator generator = new ProjectJooqGenerator();
        generator.setTargetDir(projectRoot + "radiant/tiny-mail/src/main/java-gen/");
        generator.setTargetPkg("pro.fessional.wings.tiny.mail.database.autogen");
        generator.gen(jdbcUrl, jdbcUser, jdbcPass,
                bd -> bd.databaseIncludes("win_mail_sender"),
                bd -> bd.setGlobalSuffix("TinyMail"));
    }

    @Test
    @Disabled("call by autogen01AllMainJooq")
    void autogen31TinyTaskAutogenJooq() {
        ProjectJooqGenerator generator = new ProjectJooqGenerator();
        generator.setTargetDir(projectRoot + "radiant/tiny-task/src/main/java-gen/");
        generator.setTargetPkg("pro.fessional.wings.tiny.task.database.autogen");
        generator.gen(jdbcUrl, jdbcUser, jdbcPass,
                bd -> bd.databaseIncludes("win_task_define", "win_task_result"),
                bd -> bd.setGlobalSuffix("TinyTask"));
    }
}
