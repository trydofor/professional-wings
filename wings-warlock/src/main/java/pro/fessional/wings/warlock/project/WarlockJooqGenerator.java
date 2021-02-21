package pro.fessional.wings.warlock.project;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import pro.fessional.wings.faceless.enums.autogen.StandardLanguage;
import pro.fessional.wings.faceless.jooqgen.WingsCodeGenerator;

/**
 * @author trydofor
 * @since 2021-02-20
 */
@Setter
@Getter
@Builder
public class WarlockJooqGenerator {

    private String database = "wings";
    private String jdbcHost = "127.0.0.1";
    private int jdbcPort = 3306;
    private String jdbcUser = "trydofor";
    private String jdbcPass = "moilioncircle";
    private String targetPkg = "pro.fessional.wings.warlock.database.autogen";
    private String targetDir = "wings-warlock/src/main/java/";

    public void generate() {
        WingsCodeGenerator.builder()
                          .jdbcDriver("com.mysql.cj.jdbc.Driver")
                          .jdbcUrl("jdbc:mysql://" + jdbcHost + ":" + jdbcPort + "/" + database)
                          .jdbcUser(jdbcUser)
                          .jdbcPassword(jdbcPass)
                          .databaseSchema(database)
                          // 支持 pattern的注释写法
                          .databaseIncludes("sys_constant_enum" +
                                  "|sys_standard_i18n" +
                                  "|tst_中文也分表")
                          .databaseVersionProvider("SELECT MAX(revision) FROM sys_schema_version WHERE apply_dt > '1000-01-01'")
                          .targetPackage(targetPkg)
                          .targetDirectory(targetDir)
//  不用spring自动注入
//                          .springRepository(false)
//  使用enum类型
                          .forcedConsEnum(StandardLanguage.class, "win_user_basic.language")
                          .buildAndGenerate();
    }
}
