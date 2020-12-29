package pro.fessional.wings.example.init;

import pro.fessional.wings.faceless.jooqgen.WingsCodeGenerator;

/**
 * ②根据数据库schema自动生成jooq代码
 *
 * @author trydofor
 * @since 2019-05-31
 */
public class Wings2JooqJavaGen {

    // WingsFlywaveInitDatabaseSample
    // 注意在目标工程中，应该注释掉.springRepository(false)，使Dao自动加载
    public static void main(String[] args) {
        String database = "wings_example";
        WingsCodeGenerator.builder()
                          .jdbcDriver("com.mysql.cj.jdbc.Driver")
                          .jdbcUrl("jdbc:mysql://127.0.0.1/" + database)
                          .jdbcUser("trydofor")
                          .jdbcPassword("moilioncircle")
                          .databaseSchema(database)
                          // 支持 pattern的注释写法
                          .databaseIncludes(
                                  "sys_constant_enum # enum表\n" +
                                          "|sys_standard_i18n # i18n表\n" +
                                          "|win_.* # win系列表")
                          .databaseVersionProvider("SELECT MAX(revision) FROM sys_schema_version WHERE apply_dt > '1000-01-01'")
                          .targetPackage("pro.fessional.wings.example.database.autogen")
                          .targetDirectory("wings-example/src/main/java/")
                          .springRepository(true)
                          .buildAndGenerate();
    }
}
