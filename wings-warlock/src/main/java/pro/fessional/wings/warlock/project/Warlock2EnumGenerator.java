package pro.fessional.wings.warlock.project;

import lombok.Getter;
import lombok.Setter;
import pro.fessional.wings.faceless.codegen.ConstantEnumGenerator;
import pro.fessional.wings.faceless.codegen.ConstantEnumJdbcLoader;
import pro.fessional.wings.faceless.codegen.JdbcDataLoadHelper;

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
        final JdbcDataLoadHelper helper = JdbcDataLoadHelper.use(jdbc, user, pass);
        gen(ConstantEnumJdbcLoader.load(helper));
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
