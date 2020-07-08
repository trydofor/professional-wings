package pro.fessional.wings.faceless.jooqgen

import org.jooq.codegen.GenerationTool
import org.jooq.meta.jaxb.Configuration
import org.slf4j.LoggerFactory
import pro.fessional.mirana.data.Nulls
import java.io.File
import java.nio.file.Files
import kotlin.text.Charsets.UTF_8

/**
 * @author trydofor
 * @since 2019-05-31
 */
object WingsCodeGenerator {

    val logger = LoggerFactory.getLogger(WingsCodeGenerator::class.java)!!
    const val JOOQ_XML = "/wings-flywave/jooq-codegen-faceless.xml"

    /**
     * 生成 Jooq 代码
     *
     * @param conf 配置文件，建议使用 #Builder 生产。
     * @param incremental 是否增量生成，即不删除本次中不存在的文件。
     */
    @JvmStatic
    fun generate(conf: Configuration = config(), incremental: Boolean = true) {
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
        try {
            safeCopy(tmp.absolutePath, src, pkg, incremental)
            tmp.deleteRecursively()
        } catch (e: Exception) {
            logger.error("failed to copy file from $tdr", e)
        }
    }

    @JvmStatic
    fun builder(): Builder = Builder(config())

    @JvmStatic
    fun config(): Configuration = GenerationTool.load(this.javaClass.getResourceAsStream(JOOQ_XML))

    private fun safeCopy(tmp: String, src: String, pkg: String, inc: Boolean) {

        val from = File(tmp, pkg).walkTopDown().filter { it.isFile }.map {
            it.absolutePath.substringAfter(tmp).removePrefix("/") to it
        }.toMap()

        val srcf = File(src, pkg)
        val dest = srcf.walkTopDown().filter { it.isFile }.map {
            it.absolutePath.substringAfter(src).removePrefix("/") to it
        }.toMap()

        if (!inc) {
            logger.info("not incremental, Removing excess files in $src")
            val over = dest.minus(from.keys)
            for ((k, f) in over) {
                val r = f.delete()
                logger.info("delete [$r] excess file=$k")
            }
        }

        // 忽略注释，import排序和serialVersionUID
        // The table <code>jetplus_20200515.jp_account</code>.
        // The schema <code>jetplus</code>.
        // date = "2019-09-09T01:33:51.762Z",
        // schema version:2019090903
        // serialVersionUID = 319604016;
        val ignoreRegex = arrayOf(
                "(import +[^\r\n]+;[\r\n ]+)+",
                "The\\s+table\\s+<code>[^.]+",
                "The\\s+schema\\s+<code>[^<]+",
                "@Generated[^)]+",
                "serialVersionUID[^;]+",
                "[\r\n]+")
                .joinToString("|")
                .toRegex(RegexOption.MULTILINE)
        for ((k, f) in from) {
            val d = dest[k]
            if (d == null) {
                val t = File(src, k)
                f.copyTo(t, true)
                logger.info("create new file=$k")
            } else {
                val ft = f.readText(UTF_8).replace(ignoreRegex, Nulls.Str)
                val dt = d.readText(UTF_8).replace(ignoreRegex, Nulls.Str)
                if (ft == dt) {
                    logger.info("skip main same file=$k")
                } else {
                    f.copyTo(d, true)
                    logger.info("copy new file=$k")
                }
            }
        }
    }

    class Builder(val conf: Configuration) {
        private var incr = false

        /**
         * 增量生成，即不删除本次中不存在的文件
         * @param t 是否增量生成
         */
        fun incremental(t: Boolean) = apply { incr = t }

        /**
         * 直接生成代码
         */
        fun buildAndGenerate() = generate(conf, incr)

        fun springRepository(b: Boolean) = apply { this.conf.generator.generate.withSpringAnnotations(b) }
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
        fun forceRegenerate() = apply { this.conf.generator.database.schemaVersionProvider = Nulls.Str }
    }
}