package pro.fessional.wings.faceless.sample;

import pro.fessional.wings.faceless.jooqgen.WingsCodeGenerator;

/**
 * @author trydofor
 * @since 2019-10-12
 */
public class TstJooqCodeGen {

    // 需要 版本 20190521_01
    public static void main(String[] args) {

        String database = "wings_0";
        WingsCodeGenerator.builder()
                          .jdbcDriver("com.mysql.cj.jdbc.Driver")
                          .jdbcUrl("jdbc:mysql://127.0.0.1/" + database)
                          .jdbcUser("trydofor")
                          .jdbcPassword("moilioncircle")
                          .databaseSchema(database)
                          .databaseIncludes("tst_中文也分表")
                          .databaseVersionProvider("")
                          .targetPackage("pro.fessional.wings.faceless.database.autogen")
                          .targetDirectory("wings-faceless/src/test/java/")
                          .buildAndGenerate();
    }
}
