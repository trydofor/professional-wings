package pro.fessional.wings.faceless.jooqgen

import org.jooq.codegen.GenerationTool
import org.jooq.meta.jaxb.Configuration
import org.slf4j.LoggerFactory
import java.io.File
import java.nio.file.Files

/**
 * @author trydofor
 * @since 2019-05-31
 */
object WingsCodeGenerator {

    val logger = LoggerFactory.getLogger(WingsCodeGenerator::class.java)
    const val JOOQ_XML = "/wings-flywave/jooq-codegen-faceless.xml"

    @JvmStatic
    fun generate(conf: Configuration = config(), safe: Boolean = true) {
        if (safe) {
            val src = conf.generator.target.directory
            val pkg = conf.generator.target.packageName.replace('.', '/')
            // tmp dir
            val tmp = Files.createTempDirectory("jooq-safe-gen").toFile()

            val tdr = tmp.absolutePath
            conf.generator.target.directory = tdr
            logger.info("safely generate, tmp-dir=$tdr")

            // generator
            GenerationTool.generate(conf)
            // clean and move
            val ren = File(tmp, pkg)
            try {
                val dir = File(src, pkg)
                val del = dir.deleteRecursively()
                logger.info("delete [$del] target dir=${dir.absolutePath}")

                val rst = ren.copyRecursively(dir, true)
                logger.info("copy [$rst] to ${dir.absolutePath}")
                tmp.deleteRecursively()
            } catch (e: Exception) {
                logger.error("failed to copy file from ${ren.absolutePath}", e)
            }
        } else {
            GenerationTool.generate(conf)
        }
    }

    @JvmStatic
    fun builder(): Builder = Builder(config())

    @JvmStatic
    fun config(): Configuration = GenerationTool.load(this.javaClass.getResourceAsStream(JOOQ_XML))

    class Builder(val conf: Configuration) {
        private var safe = true
        fun unsafe() = apply { safe = false }
        fun buildAndGenerate() = generate(conf, safe)

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