package pro.fessional.wings.faceless.jooqgen;

import lombok.val;
import org.jooq.Converter;
import org.jooq.codegen.GenerationTool;
import org.jooq.meta.TableDefinition;
import org.jooq.meta.jaxb.Configuration;
import org.jooq.meta.jaxb.ForcedType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.fessional.mirana.data.CodeEnum;
import pro.fessional.mirana.data.Null;
import pro.fessional.mirana.io.InputStreams;
import pro.fessional.mirana.pain.IORuntimeException;
import pro.fessional.wings.faceless.database.jooq.converter.JooqCodeEnumConverter;
import pro.fessional.wings.faceless.database.jooq.converter.JooqConsEnumConverter;
import pro.fessional.wings.faceless.database.jooq.converter.JooqLocaleConverter;
import pro.fessional.wings.faceless.database.jooq.converter.JooqZoneIdConverter;
import pro.fessional.wings.faceless.enums.ConstantEnum;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author trydofor
 * @since 2019-05-31
 */
public class WingsCodeGenerator {

    public static final String JOOQ_XML = "/wings-flywave/jooq-codegen-faceless.xml";
    private static final Logger logger = LoggerFactory.getLogger(WingsCodeGenerator.class);

    /**
     * 生成 Jooq 代码
     *
     * @param conf        配置文件，建议使用 #Builder 生产。
     * @param incremental 是否增量生成，即不删除本次中不存在的文件。
     */

    public static void generate(Configuration conf, boolean incremental) {
        if (conf == null) {
            conf = config();
        }

        try {
            val src = conf.getGenerator().getTarget().getDirectory();
            val pkg = conf.getGenerator().getTarget().getPackageName().replace('.', '/');
            // tmp dir
            val tmp = Files.createTempDirectory("jooq-safe-gen").toFile();
            val tdr = tmp.getAbsolutePath();
            conf.getGenerator().getTarget().setDirectory(tdr);
            logger.info("safely generate, tmp-dir=" + tdr);

            // generator
            GenerationTool.generate(conf);

            // clean and move
            safeCopy(tdr, src, pkg, incremental);
            Files.walk(tmp.toPath())
                 .map(Path::toFile)
                 .sorted(Comparator.reverseOrder())
                 .forEach(File::delete);
        } catch (Exception e) {
            logger.error("failed to generate", e);
        }
    }


    public static Builder builder() {
        return new Builder(config());
    }


    public static Configuration config() {
        try {
            return GenerationTool.load(WingsCodeGenerator.class.getResourceAsStream(JOOQ_XML));
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    @SuppressWarnings("all")
    private static Map<String, File> walkDir(String root, String child) throws IOException {
        File src = new File(root);
        File file = new File(src, child);
        if (!file.exists()) {
            file.mkdirs();
        }
        int off = src.getAbsolutePath().length() + (child.length() > 0 ? 1 : 0);
        return Files.walk(file.toPath())
                    .filter(Files::isRegularFile)
                    .collect(Collectors.toMap(it -> {
                        String abs = it.toAbsolutePath().toString();
                        String str = abs.substring(off);
                        return str;
                    }, Path::toFile));
    }

    private static void safeCopy(String tmp, String src, String pkg, boolean inc) throws IOException {

        val from = walkDir(tmp, pkg);
        val dest = walkDir(src, pkg);

        if (!inc && dest.size() > 0) {
            logger.info("not incremental, Removing excess files in " + src);
            for (Map.Entry<String, File> entry : dest.entrySet()) {
                String key = entry.getKey();
                if (!from.containsKey(key)) {
                    boolean r = entry.getValue().delete();
                    logger.info("delete [" + r + "] excess file=" + key);
                }
            }
        }

        // 忽略注释，import排序和serialVersionUID
        // The table <code>jetplus_20200515.jp_account</code>.
        // The schema <code>jetplus</code>.
        // date = "2019-09-09T01:33:51.762Z",
        // schema version:2019090903
        // serialVersionUID = 319604016;
        val ignoreRegex = Pattern.compile(String.join("|",
                "(import +[^\r\n]+;[\r\n ]+)+",
                "The\\s+table\\s+<code>[^.]+",
                "The\\s+schema\\s+<code>[^<]+",
                "@Generated[^)]+",
                "serialVersionUID[^;]+",
                "[\r\n]+"), Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);

        for (Map.Entry<String, File> entry : from.entrySet()) {
            val k = entry.getKey();
            val f = entry.getValue();
            val d = dest.get(k);
            if (d == null) {
                val t = new File(src, k);
                t.getParentFile().mkdirs();
                Files.copy(f.toPath(), t.toPath(), StandardCopyOption.REPLACE_EXISTING);
                logger.info("create new file=" + k);
            } else {
                val ft = ignoreRegex.matcher(InputStreams.readText(new FileInputStream(f))).replaceAll(Null.Str);
                val dt = ignoreRegex.matcher(InputStreams.readText(new FileInputStream(d))).replaceAll(Null.Str);
                if (ft.equals(dt)) {
                    logger.info("skip main same file=" + k);
                } else {
                    Files.copy(f.toPath(), d.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    logger.info("copy new file=" + k);
                }
            }
        }
    }

    public static class Builder {

        private final Configuration conf;
        private boolean incr = false;

        public Builder(Configuration conf) {
            this.conf = conf;
        }

        /**
         * 增量生成，即不删除本次中不存在的文件
         *
         * @param t 是否增量生成
         */
        public Builder incremental(boolean t) {
            incr = t;
            return this;
        }

        /**
         * 直接生成代码
         */
        public void buildAndGenerate() {
            generate(conf, incr);
        }

        public Builder springRepository(boolean b) {
            this.conf.getGenerator().getGenerate().withSpringAnnotations(b);
            return this;
        }

        public Builder jdbcDriver(String str) {
            this.conf.getJdbc().setDriver(str);
            return this;
        }

        public Builder jdbcUrl(String str) {
            // jdbc:mysql://localhost:3306/wings_warlock
            int p3 = str.indexOf("?");
            int p2 = p3 > 0 ? p3 : str.length();
            int p1 = str.lastIndexOf("/", p2);
            if (p2 > p1) {
                final String db = str.substring(p1 + 1, p2);
                databaseSchema(db);
            }
            this.conf.getJdbc().setUrl(str);
            return this;
        }

        public Builder jdbcUser(String str) {
            this.conf.getJdbc().setUser(str);
            return this;
        }

        public Builder jdbcPassword(String str) {
            this.conf.getJdbc().setPassword(str);
            return this;
        }

        public Builder targetPackage(String str) {
            this.conf.getGenerator().getTarget().setPackageName(str);
            return this;
        }

        public Builder targetDirectory(String str) {
            this.conf.getGenerator().getTarget().setDirectory(str);
            return this;
        }

        public Builder databaseSchema(String str) {
            this.conf.getGenerator().getDatabase().setInputSchema(str);
            return this;
        }

        /**
         * 支持正则的注释
         * whitespace is ignored, and embedded comments starting with # are ignored until the end of a line.
         *
         * @param reg 正则
         * @return builder
         * @see Pattern#COMMENTS
         */
        public Builder databaseIncludes(String... reg) {
            final String join = String.join("|", reg);
            this.conf.getGenerator().getDatabase().setIncludes(join);
            return this;
        }

        /**
         * 支持正则的注释
         * whitespace is ignored, and embedded comments starting with # are ignored until the end of a line.
         *
         * @param reg 正则
         * @return builder
         * @see Pattern#COMMENTS
         */
        public Builder databaseExcludes(String... reg) {
            final String join = String.join("|", reg);
            this.conf.getGenerator().getDatabase().setExcludes(join);
            return this;
        }

        public Builder databaseVersionProvider(String str) {
            this.conf.getGenerator().getDatabase().setSchemaVersionProvider(str);
            return this;
        }

        public Builder forceRegenerate() {
            this.conf.getGenerator().getDatabase().setSchemaVersionProvider(Null.Str);
            return this;
        }

        public Builder forcedType(ForcedType ft) {
            this.conf.getGenerator().getDatabase().getForcedTypes().add(ft);
            return this;
        }

        public Builder forcedType(Class<?> userType, Class<? extends Converter<?, ?>> converter, String... reg) {
            ForcedType ft = new ForcedType()
                    .withUserType(userType.getName())
                    .withConverter(converter.getName())
                    .withExpression(String.join("|", reg));
            return forcedType(ft);
        }

        /**
         * jooq中匹配 含`.`且&lt;&gt;或[]结尾，做 `new %s()`，否则做 %s
         * 参考 JavaGenerator#converterTemplate
         *
         * @param ft         ForcedType
         * @param sortImport 以import代替全限定引用，仅wingsGenerator有效
         * @return this
         */
        public Builder forcedType(ForcedType ft, String sortImport) {
            WingsJavaGenerator.shortImport4Table(sortImport);
            return forcedType(ft);
        }

        public <E extends Enum<E> & CodeEnum> Builder forcedStrCodeEnum(Class<E> en, String... reg) {
            return forcedJooqEnum(en, JooqCodeEnumConverter.class, reg);
        }

        public <E extends Enum<E> & ConstantEnum> Builder forcedIntConsEnum(Class<E> en, String... reg) {
            return forcedJooqEnum(en, JooqConsEnumConverter.class, reg);
        }

        public Builder forcedLocale(String... reg) {
            return forcedType(Locale.class, JooqLocaleConverter.class, reg);
        }

        public Builder forcedZoneId(String... reg) {
            return forcedType(ZoneId.class, JooqZoneIdConverter.class, reg);
        }

        public Builder funSeqName(Function<TableDefinition, String> fn) {
            WingsJooqGenHelp.funSeqName.set(fn);
            return this;
        }

        //
        private <E extends Enum<E>> Builder forcedJooqEnum(Class<E> userType, Class<?> converter, String... reg) {
            final String cv = converter.getName();
            ForcedType ft = new ForcedType()
                    .withUserType(userType.getName())
                    // new JooqConsEnumConverter(StandardLanguage.class)
                    .withConverter("new " + cv + "(" + userType.getName() + ".class)")
                    .withExpression(String.join("|", reg));

            return forcedType(ft, cv);
        }
    }
}
