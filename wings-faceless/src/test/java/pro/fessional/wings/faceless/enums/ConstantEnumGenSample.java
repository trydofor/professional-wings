package pro.fessional.wings.faceless.enums;

import org.springframework.jdbc.core.JdbcTemplate;
import pro.fessional.wings.faceless.codegen.ConstantEnumGenerator;
import pro.fessional.wings.faceless.codegen.ConstantEnumJdbcLoader;
import pro.fessional.wings.faceless.util.ExecSql;

import java.util.List;

/**
 * 可以自己设置配置文件
 *
 * @author trydofor
 * @since 2020-06-10
 */

public class ConstantEnumGenSample {

    public static void main(String[] args) {
        String jdbc = "jdbc:mysql://127.0.0.1/wings";
        String user = "trydofor";
        String pass = "moilioncircle";
        final ConstantEnumJdbcLoader loader = ConstantEnumJdbcLoader.use(jdbc, user, pass);
        // init
        JdbcTemplate jdbcTemplate = loader.getJdbcTemplate();
        ExecSql.execWingsSql(jdbcTemplate, "branch/feature/01-enum-i18n/2019-05-21u01-enum-i18n.sql");
        ExecSql.execWingsSql(jdbcTemplate, "branch/feature/01-enum-i18n/2019-05-21v01-enum-i18n.sql");

        final List<ConstantEnumGenerator.ConstantEnum> enums = loader.load();
        ConstantEnumGenerator.builder()
                             .targetDirectory("wings-faceless/src/main/java/")
                             .targetPackage("pro.fessional.wings.faceless.enums.autogen")
                             .excludeType("standard_boolean")
                             .generate(enums);
    }
}
