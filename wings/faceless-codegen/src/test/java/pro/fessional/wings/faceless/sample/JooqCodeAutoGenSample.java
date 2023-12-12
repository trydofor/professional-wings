package pro.fessional.wings.faceless.sample;

import io.qameta.allure.TmsLink;
import pro.fessional.wings.faceless.enums.autogen.StandardLanguage;
import pro.fessional.wings.faceless.jooqgen.WingsCodeGenerator;

/**
 * AutogenDependencyTest instead
 *
 * @author trydofor
 * @since 2019-05-31
 */
public class JooqCodeAutoGenSample {

    /**
     * Pay attention to the path, should be the project top-level directory (pro.fessional.wings).
     * Note that in the target project, you should comment out `.springRepository(false)` to auto load the `Dao`
     */
    @TmsLink("C12022")
    public static void main(String[] args) {
        // === Must Drop And Init ===
        // WingsJooqDaoAliasImplTest#test0DropAndInit

        genJooq();
        genShard();
    }

    private static final String database = "wings_faceless";
    private static final String user = "trydofor";
    private static final String pass = "moilioncircle";

    private static void genJooq() {
        WingsCodeGenerator.builder()
                          .jdbcDriver("com.mysql.cj.jdbc.Driver")
                          .jdbcUrl("jdbc:mysql://localhost/" + database)
                          .jdbcUser(user)
                          .jdbcPassword(pass)
                          .databaseSchema(database)
                          // support Regexp comment
                          .databaseIncludes("sys_constant_enum" +
                                            "|sys_standard_i18n" +
                                            "|tst_normal_table" +
                                            "|tst_sharding")
                          .databaseVersionProvider("SELECT MAX(revision) FROM sys_schema_version WHERE apply_dt > '1000-01-01'")
                          .targetPackage("pro.fessional.wings.faceless.app.database.autogen")
                          .targetDirectory("wings/faceless-jooq/src/test/java/")
//  Disable spring auto scan
//                          .springRepository(false)
//  use enum type
//                          .forcedType(new ForcedType()
//                                  .withUserType("pro.fessional.wings.faceless.enums.auto.StandardLanguage")
//                                  .withConverter("pro.fessional.wings.faceless.database.jooq.converter.JooqConsEnumConverter.of(StandardLanguage.class)")
//                                  .withExpression("tst_sharding.language")
//                          )
                          .forcedIntConsEnum(StandardLanguage.class, "tst_normal_table.value_lang", "tst_sharding.language")
                          .buildAndGenerate();
    }

    private static void genShard() {
        WingsCodeGenerator.builder()
                          .jdbcDriver("com.mysql.cj.jdbc.Driver")
                          .jdbcUrl("jdbc:mysql://localhost/" + database)
                          .jdbcUser(user)
                          .jdbcPassword(pass)
                          .databaseSchema(database)
                          // support Regexp comment
                          .databaseIncludes("tst_sharding", "tst_normal_table")
                          .databaseVersionProvider(null)
                          .targetPackage("pro.fessional.wings.faceless.app.database.autogen")
                          .targetDirectory("wings/faceless-shard/src/test/java/")
                          .buildAndGenerate();
    }
}
