package pro.fessional.wings.faceless.sample

import pro.fessional.wings.faceless.jooqgen.WingsCodeGenerator

object WingsJooqCodeGenKotlinSample {

    @JvmStatic
    fun main(args: Array<String>) {
        // 注释的部分，使用默认值就可
        val database = "wings_0"
        WingsCodeGenerator.builder()
                .jdbcDriver("com.mysql.cj.jdbc.Driver")
                .jdbcUrl("jdbc:mysql://127.0.0.1/${database}")
                .jdbcUser("trydofor")
                .jdbcPassword("moilioncircle")
                .databaseSchema(database)
                .databaseIncludes("sys_commit_journal")
                .databaseExcludes("")
//            .databaseExcludes("""
//                .*\${'$'}log # 日志表
//                | SPRING.* # Spring
//                | SYS_SCALE_SEQUENCE # 特殊处理
//            """.trimIndent())
                .databaseVersionProvider("SELECT MAX(revision) FROM sys_schema_version WHERE apply_dt > '1000-01-01'")
                .targetPackage("pro.fessional.wings.faceless.database.autogen")
                .targetDirectory("wings-faceless/src/main/java/")
                .forceRegenerate()
                .buildAndGenerate()
    }
}