package pro.fessional.wings.devs.codegen;

import lombok.Setter;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
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
@Disabled("Automatic code generation, manual initialization")
@SpringBootTest
@TestMethodOrder(MethodOrderer.MethodName.class)
public class AutogenDependencyTest {
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
    void test01AllTestCode() {
        test11FacelessJooqTest();// faceless-jooq
        test11FacelessShardTest();// faceless-shard
    }

    @Test
    void test01AllMainCode() {
        test10FacelessAutogen();// faceless-autogen/enums
        test20WarlockAutogenEnum();// warlock-autogen/enums
        test20WarlockAutogenAuth();// warlock-autogen/security
        test21WarlockAutogenJooq();// warlock-autogen/database
        test31TinyMailAutogenJooq();// tiny-autogen/mail
        test31TinyTaskAutogenJooq();// tiny-autogen/task
    }

    // ////////////////// individual test  //////////////////

    @Test
    void test10FacelessAutogen() {
        final JdbcDataLoadHelper helper = JdbcDataLoadHelper.use(dataSource);
        final List<ConstantEnumGenerator.ConstantEnum> enums = ConstantEnumJdbcLoader.load(helper);
        ConstantEnumGenerator.builder()
                             .targetDirectory(projectRoot + "wings/faceless-autogen/src/main/java/")
                             .targetPackage("pro.fessional.wings.faceless.enums.autogen")
                             .includeType(ProjectEnumGenerator.facelessEnums)
                             .generate(enums);
    }

    @Test
    void test11FacelessJooqTest() {
        Warlock3JooqGenerator generator = new Warlock3JooqGenerator();
        generator.setTargetDir(projectRoot + "wings/faceless-jooq/src/test/java/");
        generator.setTargetPkg("pro.fessional.wings.faceless.database.autogen");
        generator.gen(jdbcUrl, jdbcUser, jdbcPass,
                h -> h.databaseIncludes("sys_constant_enum", "sys_standard_i18n", "tst_sharding", "tst_normal_table")
                      .forcedIntConsEnum(StandardLanguage.class, "tst_sharding.language")
                      .forcedIntConsEnum(StandardLanguage.class, "tst_normal_table.value_lang")
        );
    }

    @Test
    void test11FacelessShardTest() {
        Warlock3JooqGenerator generator = new Warlock3JooqGenerator();
        generator.setTargetDir(projectRoot + "wings/faceless-shard/src/test/java/");
        generator.setTargetPkg("pro.fessional.wings.faceless.database.autogen");
        generator.gen(jdbcUrl, jdbcUser, jdbcPass,
                h -> h.databaseIncludes("tst_sharding", "tst_normal_table"));
    }

    @Test
    void test20WarlockAutogenEnum() {
        Warlock2EnumGenerator generator = new Warlock2EnumGenerator();
        generator.setTargetDir(projectRoot + "wings/warlock-autogen/src/main/java/");
        generator.gen(jdbcUrl, jdbcUser, jdbcPass,
                h -> h.includeType(Warlock2EnumGenerator.warlockEnums));
    }

    @Test
    void test20WarlockAutogenAuth() {
        JdbcDataLoadHelper helper = JdbcDataLoadHelper.use(jdbcUrl, jdbcUser, jdbcPass);
        Warlock4AuthGenerator generator = new Warlock4AuthGenerator();
        generator.setTargetDir(projectRoot + "wings/warlock-autogen/src/main/java/");
        generator.genPerm(helper);
        generator.genRole(helper);
    }

    @Test
    void test21WarlockAutogenJooq() {
        Warlock3JooqGenerator generator = new Warlock3JooqGenerator();
        generator.setTargetDir(projectRoot + "wings/warlock-autogen/src/main/java/");
        generator.gen(jdbcUrl, jdbcUser, jdbcPass,
                Warlock3JooqGenerator.includeWarlockBase(false),
                Warlock3JooqGenerator.includeWarlockBond(true),
                bd -> bd.setGlobalSuffix("Warlock"));
    }

    @Test
    void test31TinyMailAutogenJooq() {
        ProjectJooqGenerator generator = new ProjectJooqGenerator();
        generator.setTargetDir(projectRoot + "radiant/tiny-autogen/src/main/java/");
        generator.setTargetPkg("pro.fessional.wings.tiny.mail.database.autogen");
        generator.gen(jdbcUrl, jdbcUser, jdbcPass,
                bd -> bd.databaseIncludes("win_mail_sender"),
                bd -> bd.setGlobalSuffix("TinyMail"));
    }

    @Test
    void test31TinyTaskAutogenJooq() {
        ProjectJooqGenerator generator = new ProjectJooqGenerator();
        generator.setTargetDir(projectRoot + "radiant/tiny-autogen/src/main/java/");
        generator.setTargetPkg("pro.fessional.wings.tiny.task.database.autogen");
        generator.gen(jdbcUrl, jdbcUser, jdbcPass,
                bd -> bd.databaseIncludes("win_task_define", "win_task_result"),
                bd -> bd.setGlobalSuffix("TinyTask"));

    }
}
