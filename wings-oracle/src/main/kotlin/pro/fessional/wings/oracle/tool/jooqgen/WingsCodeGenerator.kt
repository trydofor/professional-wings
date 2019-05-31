package pro.fessional.wings.oracle.tool.jooqgen

import org.jooq.codegen.GenerationTool
import org.jooq.meta.jaxb.Configuration

/**
 * @author trydofor
 * @since 2019-05-31
 */
object WingsCodeGenerator {

    val jooqXml = "/schema/jooq-codegen-oracle.xml"

    @JvmStatic
    fun generate(conf: Configuration = config()) = GenerationTool.generate(conf)

    @JvmStatic
    fun builder(): Builder = Builder(config())

    @JvmStatic
    fun config(): Configuration = GenerationTool.load(this.javaClass.getResourceAsStream(jooqXml))

    class Builder(val conf: Configuration) {
        fun buildAndGenerate() = generate(conf)

        fun jdbcDriver(str: String) = apply { this.conf.jdbc.driver = str }
        fun jdbcUrl(str: String) = apply { this.conf.jdbc.url = str }
        fun jdbcUser(str: String) = apply { this.conf.jdbc.user = str }
        fun jdbcPassword(str: String) = apply { this.conf.jdbc.password = str }

        fun targetPackage(str: String) = apply { this.conf.generator.target.packageName = str }
        fun targetDirectory(str: String) = apply { this.conf.generator.target.directory = str }

        fun databaseSchema(str: String) = apply { this.conf.generator.database.inputSchema = str }
        fun databaseIncludes(str: String) = apply { this.conf.generator.database.includes = str }
        fun databaseExcludes(str: String) = apply { this.conf.generator.database.excludes = str }
        fun databaseVersionProvider(str: String) = apply { this.conf.generator.database.schemaVersionProvider = str }
    }
}