package pro.fessional.wings.faceless.sample;

import pro.fessional.wings.faceless.enums.autogen.StandardLanguage;
import pro.fessional.wings.faceless.jooqgen.WingsCodeGenerator;

/**
 * @author trydofor
 * @since 2019-05-31
 */
public class JooqCodeAutoGenSample {

    // 需要设置 Working Directory=$MODULE_WORKING_DIR$
    // 需要 版本 20190601_01，手动执行亦可
    // WingsJooqDaoImplTest#test0𓃬清表重置
    // 注意在目标工程中，应该注释掉.springRepository(false)，使Dao自动加载
    public static void main(String[] args) {
        String database = "wings_test";
        WingsCodeGenerator.builder()
                          .jdbcDriver("com.mysql.cj.jdbc.Driver")
                          .jdbcUrl("jdbc:mysql://localhost/" + database)
                          .jdbcUser("trydofor")
                          .jdbcPassword("moilioncircle")
                          .databaseSchema(database)
                          // 支持 pattern的注释写法
                          .databaseIncludes("sys_constant_enum" +
                                  "|sys_standard_i18n" +
                                  "|tst_中文也分表")
                          .databaseVersionProvider("SELECT MAX(revision) FROM sys_schema_version WHERE apply_dt > '1000-01-01'")
                          .targetPackage("pro.fessional.wings.faceless.database.autogen")
                          .targetDirectory("../faceless-jooq/src/test/java/")
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
}
