package pro.fessional.wings.faceless.jooqgen;

import lombok.val;
import org.jooq.Converter;
import org.jooq.codegen.GenerationTool;
import org.jooq.meta.TableDefinition;
import org.jooq.meta.jaxb.Configuration;
import org.jooq.meta.jaxb.Database;
import org.jooq.meta.jaxb.ForcedType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import pro.fessional.mirana.data.CodeEnum;
import pro.fessional.mirana.data.Null;
import pro.fessional.mirana.io.InputStreams;
import pro.fessional.mirana.pain.IORuntimeException;
import pro.fessional.wings.faceless.database.jooq.converter.JooqCodeEnumConverter;
import pro.fessional.wings.faceless.database.jooq.converter.JooqConsEnumConverter;
import pro.fessional.wings.faceless.database.jooq.converter.JooqLocaleConverter;
import pro.fessional.wings.faceless.database.jooq.converter.JooqZoneIdConverter;
import pro.fessional.wings.faceless.database.jooq.converter.JooqZoneStrConverter;
import pro.fessional.wings.faceless.enums.ConstantEnum;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 对 http://www.jooq.org/xsd/jooq-codegen-3.14.0.xsd 的包装
 *
 * @author trydofor
 * @since 2019-05-31
 */
public class WingsCodeGenerator {

    public static final String JOOQ_XML = "/wings-flywave/jooq-codegen-faceless.xml";
    private static final Logger log = LoggerFactory.getLogger(WingsCodeGenerator.class);

    /**
     * 生成 Jooq 代码
     *
     * @param conf        配置文件，建议使用 #Builder 生产。
     * @param incremental 是否增量生成，即不删除本次中不存在的文件。
     * @param suffix      为DefaultCatalog，DefaultSchema和Global对象增加后缀，以区分增量生成。
     */

    public static void generate(Configuration conf, boolean incremental, String suffix) {
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
            log.info("safely generate, tmp-dir=" + tdr);

            // generator
            WingsCodeGenConf.setGlobalSuffix(suffix);
            GenerationTool.generate(conf);

            // clean and move
            safeCopy(tdr, src, pkg, incremental);
            //noinspection ResultOfMethodCallIgnored
            Files.walk(tmp.toPath())
                 .map(Path::toFile)
                 .sorted(Comparator.reverseOrder())
                 .forEach(File::delete);
        }
        catch (Exception e) {
            log.error("failed to generate", e);
        }
    }


    public static Builder builder() {
        return builder(config());
    }

    public static Builder builder(Configuration conf) {
        return new Builder(conf == null ? config() : conf);
    }

    public static Configuration config() {
        return config(WingsCodeGenerator.class.getResourceAsStream(JOOQ_XML));
    }

    public static Configuration config(InputStream ins) {
        Objects.requireNonNull(ins);
        try (ins) {
            return GenerationTool.load(ins);
        }
        catch (IOException e) {
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
            log.info("not incremental, Removing excess files in " + src);
            for (Map.Entry<String, File> entry : dest.entrySet()) {
                String key = entry.getKey();
                if (!from.containsKey(key)) {
                    boolean r = entry.getValue().delete();
                    log.info("delete [" + r + "] excess file=" + key);
                }
            }
        }

        // 忽略注释，import排序和serialVersionUID
        // The table <code>trydofor_20200515.jp_account</code>.
        // The schema <code>trydofor</code>.
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
                //noinspection ResultOfMethodCallIgnored
                t.getParentFile().mkdirs();
                Files.copy(f.toPath(), t.toPath(), StandardCopyOption.REPLACE_EXISTING);
                log.info("create new file=" + k);
            }
            else {
                val ft = ignoreRegex.matcher(InputStreams.readText(new FileInputStream(f))).replaceAll(Null.Str);
                val dt = ignoreRegex.matcher(InputStreams.readText(new FileInputStream(d))).replaceAll(Null.Str);
                if (ft.equals(dt)) {
                    log.info("skip main same file=" + k);
                }
                else {
                    Files.copy(f.toPath(), d.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    log.info("copy new file=" + k);
                }
            }
        }
    }

    public static class Builder {

        private final Configuration conf;
        private boolean incr = false;
        private String suffix = null;

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

        public Builder setGlobalSuffix(String suffix) {
            this.suffix = suffix;
            return this;
        }

        /**
         * 直接生成代码
         */
        public void buildAndGenerate() {
            generate(conf, incr, suffix);
        }

        public Configuration configuration() {
            return conf;
        }

        public Builder springRepository(boolean b) {
            this.conf.getGenerator().getGenerate().withSpringAnnotations(b);
            return this;
        }

        public Builder h2() {
            jdbcDriver("org.h2.Driver");
            databaseName("org.jooq.meta.h2.H2Database");
            databaseSchema("PUBLIC");
            return this;
        }

        public Builder jdbcDriver(String str) {
            this.conf.getJdbc().setDriver(str);
            return this;
        }

        public Builder jdbcUrl(String str) {
            this.conf.getJdbc().setUrl(str);
            if (str.contains(":h2:")) {
                h2();
            }
            else {
                // jdbc:mysql://localhost:3306/wings_warlock
                int p3 = str.indexOf("?");
                int p2 = p3 > 0 ? p3 : str.length();
                int p1 = str.lastIndexOf("/", p2);
                if (p2 > p1) {
                    final String db = str.substring(p1 + 1, p2);
                    databaseSchema(db);
                }
            }
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
         * configuration/generator/database/name
         * <p>
         * CDATA[The database dialect from jooq-meta.
         * Available dialects are named <code>org.util.[database].[database]Database</code>.
         * <p>
         * Natively supported values are:
         * <ul>
         * <li>{@link org.jooq.meta.derby.DerbyDatabase}</li>
         * <li>{@link org.jooq.meta.firebird.FirebirdDatabase}</li>
         * <li>{@link org.jooq.meta.h2.H2Database}</li>
         * <li>{@link org.jooq.meta.hsqldb.HSQLDBDatabase}</li>
         * <li>{@link org.jooq.meta.mariadb.MariaDBDatabase}</li>
         * <li>{@link org.jooq.meta.mysql.MySQLDatabase}</li>
         * <li>{@link org.jooq.meta.postgres.PostgresDatabase}</li>
         * <li>{@link org.jooq.meta.sqlite.SQLiteDatabase}</li>
         * </ul>
         * <p>
         * This value can be used to reverse-engineer generic JDBC DatabaseMetaData (e.g. for MS Access).
         * <ul>
         * <li>{@link org.jooq.meta.jdbc.JDBCDatabase}</li>
         * </ul>
         * <p>
         * This value can be used to reverse-engineer standard jOOQ-meta XML formats.
         * <ul>
         * <li>{@link org.jooq.meta.xml.XMLDatabase}</li>
         * </ul>
         * <p>
         * You can also provide your own org.jooq.meta.Database implementation
         * here, if your database is currently not supported
         *
         * @param str AbstractDatabase
         * @return Builder
         */
        public Builder databaseName(String str) {
            this.conf.getGenerator().getDatabase().setName(str);
            return this;
        }

        /**
         * 注意，匹配的格式为QualifiedName，如db.table，详见 org.jooq.meta.AbstractDatabase#matches
         * <p>
         * replace configuration/generator/database/includes
         * <p>
         * All elements that are generated from your schema.
         * <p>
         * This is a Java regular expression. Use the pipe to separate several expressions.
         * Watch out for case-sensitivity. Depending on your database, this might be
         * important!
         * <p>
         * You can create case-insensitive regular expressions
         * using this syntax: <code>(?i:expr)</code>
         * <p>
         * Whitespace is ignored and comments are possible unless overridden in getRegexFlags(). default COMMENTS CASE_INSENSITIVE
         *
         * @param reg 正则
         * @return Builder
         * @see Pattern#COMMENTS
         */
        public Builder databaseIncludes(String... reg) {
            return databaseIncludes(false, reg);
        }

        /**
         * append or replace configuration/generator/database/includes
         */
        public Builder databaseIncludes(boolean append, String... reg) {
            final String join = String.join("|", reg);
            final Database db = this.conf.getGenerator().getDatabase();
            final String old = db.getIncludes();
            if (append && StringUtils.hasText(old)) {
                db.setIncludes(old + "|" + join);
            }
            else {
                db.setIncludes(join);
            }
            return this;
        }

        /**
         * replace configuration/generator/database/excludes
         * <p>
         * All elements that are excluded from your schema.
         * <p>
         * This is a Java regular expression. Use the pipe to separate several expressions.
         * Excludes match before includes, i.e. excludes have a higher priority.
         *
         * @param reg 正则
         * @return Builder
         * @see Pattern#COMMENTS
         */
        public Builder databaseExcludes(String... reg) {
            return databaseExcludes(false, reg);
        }

        /**
         * append or replace configuration/generator/database/excludes
         */
        public Builder databaseExcludes(boolean append, String... reg) {
            final String join = String.join("|", reg);
            final Database db = this.conf.getGenerator().getDatabase();
            final String old = db.getExcludes();
            if (append && StringUtils.hasText(old)) {
                db.setExcludes(old + "|" + join);
            }
            else {
                db.setExcludes(join);
            }
            return this;
        }

        /**
         * configuration/generator/database/schemaVersionProvider
         * <p>
         * A custom version number that, if available, will be used to assess whether the
         * getInputSchema() will need to be regenerated.
         * <p>
         * There are three operation modes for this element:
         * <ul>
         * <li>The value is a class that can be found on the classpath and that implements
         *   {@link org.jooq.meta.SchemaVersionProvider}. Such classes must provide a default constructor</li>
         * <li>The value is a SELECT statement that returns one record with one column. The
         *   SELECT statement may contain a named variable called :schema_name</li>
         * <li>The value is a constant, such as a Maven property</li>
         * </ul>
         * <p>
         * Schema versions will be generated into the {@link javax.annotation.Generated} annotation on
         * generated artefacts.
         *
         * @param str sql
         * @return Builder
         */
        public Builder databaseVersionProvider(String str) {
            this.conf.getGenerator().getDatabase().setSchemaVersionProvider(str);
            return this;
        }

        /**
         * setSchemaVersionProvider to empty
         *
         * @return Builder
         */
        public Builder forceRegenerate() {
            this.conf.getGenerator().getDatabase().setSchemaVersionProvider(Null.Str);
            return this;
        }

        public Builder forcedType(String name, String type) {
            ForcedType ft = new ForcedType();
            ft.setName(name);
            ft.setIncludeTypes(type);
            return forcedType(ft);
        }

        public Builder forcedType(ForcedType... ft) {
            final List<ForcedType> fts = this.conf.getGenerator().getDatabase().getForcedTypes();
            fts.addAll(Arrays.asList(ft));
            return this;
        }

        public Builder forcedType(Class<?> userType, Class<? extends Converter<?, ?>> converter, String... reg) {
            ForcedType ft = new ForcedType()
                    .withUserType(userType.getName())
                    .withConverter(converter.getName())
                    .withIncludeExpression(String.join("|", reg));
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
            WingsCodeGenConf.shortImport4Table(sortImport);
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
            final String zid = ZoneId.class.getName();
            final String exp = String.join("|", reg);
            ForcedType ft1 = new ForcedType()
                    .withUserType(zid)
                    .withConverter(JooqZoneIdConverter.class.getName())
                    .withIncludeTypes("INT.*")
                    .withIncludeExpression(exp);
            ForcedType ft2 = new ForcedType()
                    .withUserType(zid)
                    .withConverter(JooqZoneStrConverter.class.getName())
                    .withIncludeTypes("(VAR)?CHAR.*")
                    .withIncludeExpression(exp);
            return forcedType(ft1, ft2);
        }

        public Builder funSeqName(Function<TableDefinition, String> fn) {
            WingsJooqGenHelper.funSeqName.set(fn);
            return this;
        }

        //
        private <E extends Enum<E>> Builder forcedJooqEnum(Class<E> userType, Class<?> converter, String... reg) {
            final String cv = converter.getName();
            ForcedType ft = new ForcedType()
                    .withUserType(userType.getName())
                    // new JooqConsEnumConverter(StandardLanguage.class)
                    .withConverter("new " + cv + "(" + userType.getName() + ".class)")
                    .withIncludeExpression(String.join("|", reg));

            return forcedType(ft, cv);
        }
    }
}
