package pro.fessional.wings.faceless.jooqgen

import org.jooq.codegen.GenerationTool
import org.jooq.meta.jaxb.Configuration

/**
 * @author trydofor
 * @since 2019-05-31
 */
object WingsCodeGenerator {

    const val JOOQ_XML = "/wings-flywave/jooq-codegen-faceless.xml"

    @JvmStatic
    fun generate(conf: Configuration = config()) = GenerationTool.generate(conf)

    @JvmStatic
    fun builder(): Builder = Builder(config())

    @JvmStatic
    fun config(): Configuration = GenerationTool.load(this.javaClass.getResourceAsStream(JOOQ_XML))

    class Builder(val conf: Configuration) {
        fun buildAndGenerate() = generate(conf)

        fun jdbcDriver(str: String) = apply { this.conf.jdbc.driver = str }
        fun jdbcUrl(str: String) = apply { this.conf.jdbc.url = str }
        fun jdbcUser(str: String) = apply { this.conf.jdbc.user = str }
        fun jdbcPassword(str: String) = apply { this.conf.jdbc.password = str }

        fun targetPackage(str: String) = apply { this.conf.generator.target.packageName = str }
        fun targetDirectory(str: String) = apply { this.conf.generator.target.directory = str }

        fun databaseSchema(str: String) = apply { this.conf.generator.database.inputSchema = str }
        fun databaseIncludes(reg: String) = apply { this.conf.generator.database.includes = reg }
        fun databaseExcludes(reg: String) = apply { this.conf.generator.database.excludes = reg }
        fun databaseVersionProvider(str: String) = apply { this.conf.generator.database.schemaVersionProvider = str }
        fun forceRegenerate() = apply { this.conf.generator.database.schemaVersionProvider = "" }
    }
}