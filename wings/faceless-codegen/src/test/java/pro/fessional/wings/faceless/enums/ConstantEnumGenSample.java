package pro.fessional.wings.faceless.enums;

import org.springframework.jdbc.core.JdbcTemplate;
import pro.fessional.wings.faceless.codegen.ConstantEnumGenerator;
import pro.fessional.wings.faceless.codegen.ConstantEnumJdbcLoader;
import pro.fessional.wings.faceless.codegen.JdbcDataLoadHelper;
import pro.fessional.wings.testing.database.ExecSql;

import java.util.List;

/**
 * Customize Config
 *
 * @author trydofor
 * @since 2020-06-10
 */

public class ConstantEnumGenSample {

    public static void main(String[] args) {
        String jdbc = "jdbc:mysql://localhost/wings";
        String user = "trydofor";
        String pass = "moilioncircle";
        final JdbcDataLoadHelper helper = JdbcDataLoadHelper.use(jdbc, user, pass);
        // init
        JdbcTemplate jdbcTemplate = helper.getJdbcTemplate();
        ExecSql.execWingsSql(jdbcTemplate, "master/01-light/2019-05-20u01-light-commit.sql");
        ExecSql.execWingsSql(jdbcTemplate, "master/01-light/2019-05-20v01-light-commit.sql");
        ExecSql.execWingsSql(jdbcTemplate, "branch/feature/01-enum-i18n/2019-05-21u01-enum-i18n.sql");
        ExecSql.execWingsSql(jdbcTemplate, "branch/feature/01-enum-i18n/2019-05-21v01-enum-i18n.sql");

        final List<ConstantEnumGenerator.ConstantEnum> enums = ConstantEnumJdbcLoader.load(helper);
        ConstantEnumGenerator.builder()
                             .targetDirectory("wings/faceless/src/main/java/")
                             .targetPackage("pro.fessional.wings.faceless.enums.autogen")
                             .excludeType("standard_boolean")
                             .generate(enums);
    }
}
