package pro.fessional.wings.faceless.sample;

import pro.fessional.wings.faceless.enums.autogen.StandardLanguage;
import pro.fessional.wings.faceless.jooqgen.WingsCodeGenerator;

/**
 * 可由 FacelessAutogenTest 代替
 *
 * @author trydofor
 * @since 2019-05-31
 */
public class JooqCodeAutoGenSample {

    // 注意路径，为工程顶级目录即可
    // 注意在目标工程中，应该注释掉.springRepository(false)，使Dao自动加载
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
                          // 支持 pattern的注释写法
                          .databaseIncludes("sys_constant_enum" +
                                            "|sys_standard_i18n" +
                                            "|tst_中文也分表")
                          .databaseVersionProvider("SELECT MAX(revision) FROM sys_schema_version WHERE apply_dt > '1000-01-01'")
                          .targetPackage("pro.fessional.wings.faceless.database.autogen")
                          .targetDirectory("wings/faceless-jooq/src/test/java/")
//  不用spring自动注入
//                          .springRepository(false)
//  使用enum类型
//                          .forcedType(new ForcedType()
//                                  .withUserType("pro.fessional.wings.faceless.enums.auto.StandardLanguage")
//                                  .withConverter("pro.fessional.wings.faceless.database.jooq.converter.JooqConsEnumConverter.of(StandardLanguage.class)")
//                                  .withExpression("tst_中文也分表.language")
//                          )
                          .forcedIntConsEnum(StandardLanguage.class, "tst_中文也分表.language")
                          .buildAndGenerate();
    }

    private static void genShard() {
        WingsCodeGenerator.builder()
                          .jdbcDriver("com.mysql.cj.jdbc.Driver")
                          .jdbcUrl("jdbc:mysql://localhost/" + database)
                          .jdbcUser(user)
                          .jdbcPassword(pass)
                          .databaseSchema(database)
                          // 支持 pattern的注释写法
                          .databaseIncludes("tst_中文也分表")
                          .databaseVersionProvider(null)
                          .targetPackage("pro.fessional.wings.faceless.database.autogen")
                          .targetDirectory("wings/faceless-shard/src/test/java/")
                          .buildAndGenerate();
    }
}
