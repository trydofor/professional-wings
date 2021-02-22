package pro.fessional.wings.warlock.project;

import lombok.Getter;
import lombok.Setter;
import pro.fessional.wings.faceless.enums.autogen.StandardLanguage;
import pro.fessional.wings.faceless.enums.autogen.StandardTimezone;
import pro.fessional.wings.faceless.jooqgen.WingsCodeGenerator;
import pro.fessional.wings.warlock.enums.autogen.UserGender;
import pro.fessional.wings.warlock.enums.autogen.UserStatus;

/**
 * idea中，main函数执行和spring执行，workdir不同
 *
 * @author trydofor
 * @since 2021-02-20
 */
@Setter
@Getter
public class Warlock3JooqGenerator {

    private String targetDir = "./wings-warlock/src/main/java/";
    private String targetPkg = "pro.fessional.wings.warlock.database.autogen";

    public void gen(String jdbc, String user, String pass) {
        WingsCodeGenerator.builder()
                          .jdbcDriver("com.mysql.cj.jdbc.Driver")
                          .jdbcUrl(jdbc)
                          .jdbcUser(user)
                          .jdbcPassword(pass)
                          // 支持 pattern的注释写法
                          .databaseIncludes(
                                  "sys_constant_enum",
                                  "sys_standard_i18n",
                                  "win_user_basic",
                                  "win_user_anthn",
                                  "win_user_login"
                          )
                          .databaseVersionProvider("SELECT MAX(revision) FROM sys_schema_version WHERE apply_dt > '1000-01-01'")
                          .targetPackage(targetPkg)
                          .targetDirectory(targetDir)
//  使用enum类型
                          .forcedConsEnum(StandardLanguage.class, "win_user_basic.language")
                          .forcedConsEnum(StandardTimezone.class, "win_user_basic.timezone")
                          .forcedConsEnum(UserGender.class, "win_user_basic.gender")
                          .forcedConsEnum(UserStatus.class, "win_user_basic.status")
                          .forcedConsEnum(UserStatus.class, "win_user_anthn.status")
                          .buildAndGenerate();
    }
}
