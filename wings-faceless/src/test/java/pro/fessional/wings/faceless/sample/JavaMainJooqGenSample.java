package pro.fessional.wings.faceless.sample;

import pro.fessional.wings.faceless.jooqgen.WingsCodeGenerator;

/**
 * @author trydofor
 * @since 2019-05-31
 */
public class JavaMainJooqGenSample {

    // WingsFlywaveInitDatabaseSample
    // 注意在目标工程中，应该注释掉.springRepository(false)，使Dao自动加载
    public static void main(String[] args) {
        String database = "wings";
        WingsCodeGenerator.builder()
                          .jdbcDriver("com.mysql.cj.jdbc.Driver")
                          .jdbcUrl("jdbc:mysql://127.0.0.1/" + database)
                          .jdbcUser("trydofor")
                          .jdbcPassword("moilioncircle")
                          .databaseSchema(database)
                          .databaseIncludes("sys_commit_journal|sys_constant_enum|sys_standard_i18n")
                          .databaseExcludes("sys_light_sequence|sys_schema_journal|sys_schema_version") // jdbc实现
                          .databaseVersionProvider("SELECT MAX(revision) FROM sys_schema_version WHERE apply_dt > '1000-01-01'")
                          .targetPackage("pro.fessional.wings.faceless.database.autogen")
                          .targetDirectory("wings-faceless/src/main/java/")
                          .springRepository(false)
                          .buildAndGenerate();
    }
}
