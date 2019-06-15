package pro.fessional.wings.oracle.sample;

import pro.fessional.wings.oracle.jooqgen.WingsCodeGenerator;

/**
 * @author trydofor
 * @since 2019-05-31
 */
public class WingsJooqCodeGenJavaSample {

    public static void main(String[] args) {
        WingsCodeGenerator.builder()
                          .jdbcDriver("com.mysql.cj.jdbc.Driver")
                          .jdbcUrl("jdbc:mysql://127.0.0.1/wings")
                          .jdbcUser("trydofor")
                          .jdbcPassword("moilioncircle")
                          .databaseSchema("wings")
                          .databaseIncludes(".*")
                          .databaseExcludes(".*\\$log # 日志表\n"
                                  + "| SPRING.* # Spring\n"
                                  + "| SYS_SCALE_SEQUENCE # 特殊处理")
                          .databaseVersionProvider("SELECT MAX(REVISION) FROM SYS_SCHEMA_VERSION WHERE APPLY_DT > '1000-01-01'")
                          .targetPackage("pro.fessional.wings.oracle.database.autogen")
                          .targetDirectory("wings-oracle/src/main/java/")
                          .buildAndGenerate();
    }
}
