package pro.fessional.wings.warlock.project;

import lombok.Getter;
import lombok.Setter;
import org.springframework.jdbc.core.JdbcTemplate;
import pro.fessional.wings.faceless.codegen.ConstantEnumGenerator;
import pro.fessional.wings.faceless.codegen.ConstantEnumJdbcLoader;

import javax.sql.DataSource;
import java.util.List;

/**
 * idea中，main函数执行和spring执行，workdir不同
 *
 * @author trydofor
 * @since 2021-02-20
 */
@Setter
@Getter
public class Warlock2EnumGenerator {

    private String targetPkg = "./wings-warlock/src/main/java/";
    private String targetDir = "pro.fessional.wings.warlock.enums.autogen";

    public void gen(String jdbc, String user, String pass) {
        final ConstantEnumJdbcLoader loader = ConstantEnumJdbcLoader.use(jdbc, user, pass);
        gen(loader.load());
    }

    public void gen(JdbcTemplate jdbcTemplate) {
        final ConstantEnumJdbcLoader loader = ConstantEnumJdbcLoader.use(jdbcTemplate);
        gen(loader.load());
    }

    public void gen(DataSource dataSource) {
        final ConstantEnumJdbcLoader loader = ConstantEnumJdbcLoader.use(dataSource);
        gen(loader.load());
    }

    public void gen(List<ConstantEnumGenerator.ConstantEnum> enumItems) {

        ConstantEnumGenerator.builder()
                             .targetDirectory(targetPkg)
                             .targetPackage(targetDir)
                             .excludeType("standard_boolean")
                             .excludeType("standard_language")
                             .excludeType("standard_timezone")
                             .generate(enumItems);
    }
}
