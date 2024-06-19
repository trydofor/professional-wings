package pro.fessional.wings.devs.init;

import lombok.Setter;
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
// @SpringBootTest(properties = "testing.dbname=wings_faceless")
@EnabledIfSystemProperty(named = "devs-autogen", matches = "true")
@TestMethodOrder(MethodOrderer.MethodName.class)
public class AutogenMainCodeTest {
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
    void autogen01AllMainCode() {
        // use wings database
        autogen10FacelessAutogen(); // faceless/enums
        autogen20WarlockAutogenEnum(); // warlock/enums
        autogen20WarlockAutogenAuth(); // warlock/security

        autogen21WarlockAutogenJooq(); // warlock/database
        autogen31TinyMailAutogenJooq(); // tiny/mail
        autogen31TinyTaskAutogenJooq(); // tiny/task
    }

    // ////////////////// individual test  //////////////////

    void autogen10FacelessAutogen() {
        final JdbcDataLoadHelper helper = JdbcDataLoadHelper.use(dataSource);
        final List<ConstantEnumGenerator.ConstantEnum> enums = ConstantEnumJdbcLoader.load(helper);
        ConstantEnumGenerator.builder()
                             .targetDirectory(projectRoot + "wings/faceless/src/main/java-gen/")
                             .targetPackage("pro.fessional.wings.faceless.enums.autogen")
                             .includeType(ProjectEnumGenerator.facelessEnums)
                             .generate(enums);
    }

    void autogen20WarlockAutogenEnum() {
        Warlock2EnumGenerator generator = new Warlock2EnumGenerator();
        generator.setTargetDir(projectRoot + "wings/warlock/src/main/java-gen/");
        generator.setTargetPkg("pro.fessional.wings.warlock.enums.autogen");
        generator.gen(jdbcUrl, jdbcUser, jdbcPass,
                h -> h.includeType(Warlock2EnumGenerator.warlockEnums));
    }

    void autogen20WarlockAutogenAuth() {
        JdbcDataLoadHelper helper = JdbcDataLoadHelper.use(jdbcUrl, jdbcUser, jdbcPass);
        Warlock4AuthGenerator generator = new Warlock4AuthGenerator();
        generator.setTargetDir(projectRoot + "wings/warlock/src/main/java-gen/");
        generator.setTargetPkg("pro.fessional.wings.warlock.security.autogen");
        generator.genPerm(helper);
        generator.genRole(helper);
    }

    void autogen21WarlockAutogenJooq() {
        Warlock3JooqGenerator generator = new Warlock3JooqGenerator();
        generator.setTargetDir(projectRoot + "wings/warlock/src/main/java-gen/");
        generator.setTargetPkg("pro.fessional.wings.warlock.database.autogen");
        generator.gen(jdbcUrl, jdbcUser, jdbcPass,
            Warlock3JooqGenerator.includeWarlockBase(false),
            Warlock3JooqGenerator.includeWarlockBond(true),
            bd -> bd.setGlobalSuffix("Warlock"));
    }

    void autogen31TinyMailAutogenJooq() {
        ProjectJooqGenerator generator = new ProjectJooqGenerator();
        generator.setTargetDir(projectRoot + "radiant/tiny-mail/src/main/java-gen/");
        generator.setTargetPkg("pro.fessional.wings.tiny.mail.database.autogen");
        generator.gen(jdbcUrl, jdbcUser, jdbcPass,
                bd -> bd.databaseIncludes("win_mail_sender"),
                bd -> bd.setGlobalSuffix("TinyMail"));
    }

    void autogen31TinyTaskAutogenJooq() {
        ProjectJooqGenerator generator = new ProjectJooqGenerator();
        generator.setTargetDir(projectRoot + "radiant/tiny-task/src/main/java-gen/");
        generator.setTargetPkg("pro.fessional.wings.tiny.task.database.autogen");
        generator.gen(jdbcUrl, jdbcUser, jdbcPass,
                bd -> bd.databaseIncludes("win_task_define", "win_task_result"),
                bd -> bd.setGlobalSuffix("TinyTask"));
    }
}
