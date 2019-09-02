package pro.fessional.wings.faceless.sample;

import pro.fessional.wings.faceless.jooqgen.WingsCodeGenerator;

/**
 * @author trydofor
 * @since 2019-05-31
 */
public class WingsJooqCodeGenJavaSample {

    public static void main(String[] args) {
        WingsCodeGenerator.builder()
                          .jdbcDriver("com.mysql.cj.jdbc.Driver")
                          .jdbcUrl("jdbc:mysql://127.0.0.1/wings_0")
                          .jdbcUser("trydofor")
                          .jdbcPassword("moilioncircle")
                          .databaseSchema("wings_0")
                          .databaseIncludes("sys_commit_journal")
//                          .databaseExcludes(".*\\$log # 日志表\n"
//                                  + "| SPRING.* # Spring\n"
//                                  + "| SYS_SCALE_SEQUENCE # 特殊处理")
                          .databaseVersionProvider("SELECT MAX(revision) FROM sys_schema_version WHERE apply_dt > '1000-01-01'")
                          .targetPackage("pro.fessional.wings.faceless.database.autogen")
                          .targetDirectory("wings-faceless/src/main/java/")
                          .buildAndGenerate();
    }
}
