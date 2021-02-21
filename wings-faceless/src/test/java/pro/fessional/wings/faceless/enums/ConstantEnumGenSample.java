package pro.fessional.wings.faceless.enums;

import org.springframework.jdbc.core.JdbcTemplate;
import pro.fessional.wings.faceless.codegen.ConstantEnumGenerator;
import pro.fessional.wings.faceless.codegen.ConstantEnumJdbcLoader;
import pro.fessional.wings.faceless.util.ExecSql;

import java.io.IOException;
import java.util.List;

/**
 * 可以自己设置配置文件
 *
 * @author trydofor
 * @since 2020-06-10
 */

public class ConstantEnumGenSample {

    public static void main(String[] args) throws IOException {
        String jdbc = "jdbc:mysql://127.0.0.1/wings";
        String user = "trydofor";
        String pass = "moilioncircle";
        final ConstantEnumJdbcLoader loader = ConstantEnumJdbcLoader.use(jdbc, user, pass);
        // init
        JdbcTemplate jdbcTemplate = new JdbcTemplate(loader.getDataSource());
        ExecSql.execWingsSql(jdbcTemplate, "branch/feature/01-enum-i18n/2019-05-21u01-enum-i18n.sql");
        ExecSql.execWingsSql(jdbcTemplate, "branch/feature/01-enum-i18n/2019-05-21v01-enum-i18n.sql");

        final List<ConstantEnumGenerator.ConstantEnum> enums = loader.load();
        ConstantEnumGenerator.builder()
                             .setJavaSource("wings-faceless/src/main/java/")
                             .setJavaPackage("pro.fessional.wings.faceless.enums.autogen")
                             .addExcludeType("standard_boolean")
                             .generate(enums);
    }
}
