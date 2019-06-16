package pro.fessional.wings.faceless.sample

import pro.fessional.wings.faceless.jooqgen.WingsCodeGenerator

object WingsJooqCodeGenKotlinSample {

    @Throws(Exception::class)
    fun main() {
        // 注释的部分，使用默认值就可
        WingsCodeGenerator.builder()
                .jdbcDriver("com.mysql.cj.jdbc.Driver")
                .jdbcUrl("jdbc:mysql://127.0.0.1/wings")
                .jdbcUser("trydofor")
                .jdbcPassword("moilioncircle")
                .databaseSchema("wings")
//            .databaseIncludes(".*")
//            .databaseExcludes("""
//                .*\${'$'}log # 日志表
//                | SPRING.* # Spring
//                | SYS_SCALE_SEQUENCE # 特殊处理
//            """.trimIndent())
                //.databaseVersionProvider("SELECT MAX(REVISION) FROM SYS_SCHEMA_VERSION WHERE APPLY_DT > '1000-01-01'")
                .targetPackage("pro.fessional.wings.faceless.database.autogen")
                .targetDirectory("wings-faceless/src/main/java/")
                .forceRegenerate()
                .buildAndGenerate()
    }
}