package pro.fessional.wings.oracle.sample

import pro.fessional.wings.oracle.tool.jooqgen.WingsCodeGenerator

@Throws(Exception::class)
fun main() {

    WingsCodeGenerator.builder()
            .jdbcDriver("com.mysql.cj.jdbc.Driver")
            .jdbcUrl("jdbc:mysql://127.0.0.1/wings")
            .jdbcUser("trydofor")
            .jdbcPassword("moilioncircle")
            .databaseSchema("wings")
            .databaseIncludes(".*")
            .databaseExcludes("""
                .*\${'$'}log # 日志表
                | SPRING.* # Spring
                | SYS_SCALE_SEQUENCE # 特殊处理
            """.trimIndent())
            .databaseVersionProvider("SELECT MAX(REVISION) FROM SYS_SCHEMA_VERSION WHERE APPLY_DT > '1000-01-01'")
            .targetPackage("pro.fessional.wings.oracle.database.autogen")
            .targetDirectory("wings-oracle/src/main/java/")

            .buildAndGenerate()

}