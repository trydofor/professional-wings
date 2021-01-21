package pro.fessional.wings.faceless.util;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.StreamUtils;
import pro.fessional.mirana.text.BuilderHelper;
import pro.fessional.wings.faceless.convention.EmptySugar;
import pro.fessional.wings.faceless.flywave.SchemaRevisionManager;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author trydofor
 * @since 2019-06-14
 */
public class FlywaveRevisionScanner {

    private static final Logger logger = LoggerFactory.getLogger(ConstantEnumGenerator.class);

    public static final String REVISION_PATH_MASTER = "classpath*:/wings-flywave/master/**/*.sql";
    public static final String REVISION_PATH_BRANCH_HEAD = "classpath*:/wings-flywave/branch/";
    public static final String REVISION_PATH_FEATURE_HEAD = REVISION_PATH_BRANCH_HEAD + "feature/";
    public static final String REVISION_PATH_SUPPORT_HEAD = REVISION_PATH_BRANCH_HEAD + "support/";
    public static final String REVISION_PATH_SOMEFIX_HEAD = REVISION_PATH_BRANCH_HEAD + "somefix/";
    public static final String REVISION_PATH_BRANCH_TAIL = "**/*.sql";
    public static final String REVISION_PATH_BRANCH_FULL = REVISION_PATH_BRANCH_HEAD + REVISION_PATH_BRANCH_TAIL;
    public static final String REVISION_PATH_BRANCH_3RD_ENU18N = featurePath("01-enum-i18n");
    public static final String REVISION_PATH_BRANCH_FIX_V227 = somefixPath("v227-fix");

    public static final long REVISION_1ST_SCHEMA = 2019_0512_01L;
    public static final long REVISION_2ND_IDLOGS = 2019_0520_01L;
    public static final long REVISION_3RD_ENU18N = 2019_0521_01L;

    @NotNull
    public static String somefixPath(String name) {
        return prefixPath(REVISION_PATH_SOMEFIX_HEAD, name);
    }

    @NotNull
    public static String supportPath(String name) {
        return prefixPath(REVISION_PATH_SUPPORT_HEAD, name);
    }

    @NotNull
    public static String featurePath(String name) {
        return prefixPath(REVISION_PATH_FEATURE_HEAD, name);
    }

    @NotNull
    public static String branchPath(String name) {
        return prefixPath(REVISION_PATH_BRANCH_HEAD, name);
    }

    @NotNull
    private static String prefixPath(String prefix, String name) {
        StringBuilder sb = new StringBuilder(100);
        sb.append(prefix);
        if (name != null) {
            for (String pt : name.split("/+")) {
                pt = pt.trim();
                if (!pt.isEmpty()) {
                    sb.append(pt).append("/");
                }
            }
        }
        sb.append(REVISION_PATH_BRANCH_TAIL);
        return sb.toString();
    }

    @NotNull
    public static String commentInfo(String... path) {
        Pattern tknRegex = Pattern.compile("[/\\\\]wings-flywave[/\\\\]([^:]*[/\\\\])[-_0-9]{8,}[uv][0-9]{2,}([^/]*\\.sql)$", Pattern.CASE_INSENSITIVE);

        LinkedHashSet<String> info = new LinkedHashSet<>();
        for (String s : path) {
            Matcher m = tknRegex.matcher(s);
            if (m.find()) {
                BuilderHelper.W sb = BuilderHelper.w();
                sb.append(m.group(1));
                sb.append("*");
                sb.append(m.group(2));
                info.add(sb.toString());
            } else {
                info.add(s);
            }
        }

        return String.join(", ", info);
    }

    @NotNull
    public static SortedMap<Long, SchemaRevisionManager.RevisionSql> scanMaster() {
        return scan(REVISION_PATH_MASTER);
    }

    @NotNull
    public static SortedMap<Long, SchemaRevisionManager.RevisionSql> scanBranch(String... name) {
        TreeMap<Long, SchemaRevisionManager.RevisionSql> result = new TreeMap<>();
        for (String n : name) {
            scan(result, branchPath(n));
        }
        return result;
    }

    /**
     * 把指定或默认位置的*.sql文件都加载进来，并按文件名的字母顺序排序。
     * String path = "classpath*:/wings-flywave/master/"; // 全部类路径
     * String path = "classpath:/wings-flywave/master/";  // 当前类路径
     * String path = "file:src/main/resources/wings-flywave/master/"; // 具体文件
     *
     * @param path 按Spring的格式写，classpath*:,classpath:等，默认[REVISIONSQL_PATH]
     * @return 按版本号升序排列的TreeMap
     * @see PathMatchingResourcePatternResolver
     */
    @NotNull
    public static SortedMap<Long, SchemaRevisionManager.RevisionSql> scan(@NotNull String... path) {
        TreeMap<Long, SchemaRevisionManager.RevisionSql> result = new TreeMap<>();
        for (String p : path) {
            scan(result, p);
        }
        return result;
    }

    /**
     * @param result 排序map
     * @param path   扫描路径
     * @see #scan(String...)
     */
    public static void scan(SortedMap<Long, SchemaRevisionManager.RevisionSql> result, String path) {
        String file = null;
        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources(path);
            logger.info("[FlywaveRevisionScanner]🐝 scanned " + resources.length + " resources in path=" + path);
            Pattern reviRegex = Pattern.compile("([-_0-9]{8,})([uv])([0-9]{2,})[^/]*\\.sql$", Pattern.CASE_INSENSITIVE);
            Charset utf8 = StandardCharsets.UTF_8;

            final HashSet<Long> newRevi = new HashSet<>();
            final HashSet<Long> rplRevi = new HashSet<>();
            for (Resource res : resources) {
                file = res.getURL().getPath();
                Matcher m = reviRegex.matcher(file);
                if (!m.find()) {
                    logger.info("[FlywaveRevisionScanner]🐝 skip unsupported resource=" + file);
                    continue;
                }
                boolean undo = m.group(2).equalsIgnoreCase("u");
                StringBuilder sb = new StringBuilder(10);
                String g1 = m.group(1);
                for (int i = 0; i < g1.length(); i++) {
                    char c = g1.charAt(i);
                    if (c >= '0' && c <= '9') sb.append(c);
                }
                sb.append(m.group(3));
                final long revi = Long.parseLong(sb.toString());
                newRevi.add(revi);

                SchemaRevisionManager.RevisionSql d = result.computeIfAbsent(revi, key -> {
                    SchemaRevisionManager.RevisionSql sql = new SchemaRevisionManager.RevisionSql();
                    sql.setRevision(revi);
                    return sql;
                });

                String text = StreamUtils.copyToString(res.getInputStream(), utf8);

                if (undo) {
                    final String ou = d.getUndoPath();
                    if (EmptySugar.asEmptyValue(ou)) {
                        logger.info("[FlywaveRevisionScanner]🐝 scan " + revi + " undo↓ resource=" + file);
                    } else {
                        rplRevi.add(revi);
                        logger.warn("[FlywaveRevisionScanner]🐝 replace " + revi + " undo↓ new=" + file + ", old=" + ou);
                    }
                    d.setUndoPath(file);
                    d.setUndoText(text);
                } else {
                    final String ou = d.getUptoPath();
                    if (EmptySugar.asEmptyValue(ou)) {
                        logger.info("[FlywaveRevisionScanner]🐝 scan " + revi + " upto↑ resource=" + file);
                    } else {
                        rplRevi.add(revi);
                        logger.warn("[FlywaveRevisionScanner]🐝 replace " + revi + " upto↑ new=" + file + ", old=" + ou);
                    }
                    d.setUptoPath(file);
                    d.setUptoText(text);
                }
            }
            logger.info("[FlywaveRevisionScanner]🐝 scanned revisions new=" + newRevi.size() + ", replace=" + rplRevi.size());
        } catch (Exception e) {
            throw new IllegalStateException("failed to scan path = " + path + ", file=" + file, e);
        }
    }

    /**
     * 读取所有降级脚本
     *
     * @param sqls sqls
     * @return sql
     */
    @NotNull
    public static String undo(SortedMap<Long, SchemaRevisionManager.RevisionSql> sqls) {
        BuilderHelper.W sb = BuilderHelper.w();
        if (sqls != null) {
            sb.join(true, "\n", sqls.values(), SchemaRevisionManager.RevisionSql::getUndoText);
        }
        return sb.toString();
    }

    /**
     * 读取所有升级脚本
     *
     * @param sqls sqls
     * @return sql
     */
    @NotNull
    public static String upto(SortedMap<Long, SchemaRevisionManager.RevisionSql> sqls) {
        BuilderHelper.W sb = BuilderHelper.w();
        if (sqls != null) {
            sb.join(true, "\n", sqls.values(), SchemaRevisionManager.RevisionSql::getUptoText);
        }
        return sb.toString();
    }

    /**
     * 可应用版本过滤器和重命名
     *
     * @return 构造器
     * @see Builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * 执行以下步骤
     * ①scan全路径
     * ②rename版本
     * ③include过滤器
     * ④exclude过滤器
     */
    public static class Builder {
        private final LinkedHashMap<Predicate<SchemaRevisionManager.RevisionSql>, String> includes = new LinkedHashMap<>();
        private final LinkedHashMap<Predicate<SchemaRevisionManager.RevisionSql>, String> excludes = new LinkedHashMap<>();
        private final HashMap<Long, Map.Entry<Long, String>> renames = new HashMap<>();
        private final LinkedHashSet<String> paths = new LinkedHashSet<>();

        public Builder path(String... path) {
            Collections.addAll(paths, path);
            return this;
        }

        public Builder master() {
            paths.add(REVISION_PATH_MASTER);
            return this;
        }

        public Builder branch(String... path) {
            for (String s : path) {
                paths.add(FlywaveRevisionScanner.branchPath(s));
            }
            return this;
        }

        public Builder feature(String... path) {
            for (String s : path) {
                paths.add(FlywaveRevisionScanner.featurePath(s));
            }
            return this;
        }

        public Builder somefix(String... path) {
            for (String s : path) {
                paths.add(FlywaveRevisionScanner.somefixPath(s));
            }
            return this;
        }

        public Builder support(String... path) {
            for (String s : path) {
                paths.add(FlywaveRevisionScanner.supportPath(s));
            }
            return this;
        }

        /**
         * 版本改名，覆盖报错
         *
         * @param old 旧版本
         * @param to  新版本
         * @return builder
         */
        public Builder rename(long old, long to) {
            return rename(old, to, "do NOT replace");
        }

        /**
         * 版本改名，如果没有force信息，old不存在或to存在在报错
         *
         * @param old   旧版本
         * @param to    新版本
         * @param force 强制覆盖信息，无信息且覆盖时抛出异常
         * @return builder
         */
        public Builder rename(long old, long to, String force) {
            if (old < 0 || to < 0) throw new IllegalArgumentException("revi must >0");
            if (force == null) force = "";
            renames.put(old, new AbstractMap.SimpleEntry<>(to, force));
            return this;
        }

        public Builder include(long... revi) {
            return include("", revi);
        }

        public Builder include(String info, long... revi) {
            final HashSet<Long> rvs = new HashSet<>();
            for (long l : revi) {
                rvs.add(l);
            }
            return include(info, it -> rvs.contains(it.getRevision()));
        }

        public Builder include(Predicate<SchemaRevisionManager.RevisionSql> inc) {
            return include("", inc);
        }

        public Builder include(String info, Predicate<SchemaRevisionManager.RevisionSql> inc) {
            includes.put(inc, info);
            return this;
        }


        public Builder exclude(long... revi) {
            return exclude("", revi);
        }

        public Builder exclude(String info, long... revi) {
            final HashSet<Long> rvs = new HashSet<>();
            for (long l : revi) {
                rvs.add(l);
            }
            return exclude(info, it -> rvs.contains(it.getRevision()));
        }

        public Builder exclude(Predicate<SchemaRevisionManager.RevisionSql> exc) {
            return exclude("", exc);
        }

        public Builder exclude(String info, Predicate<SchemaRevisionManager.RevisionSql> exc) {
            excludes.put(exc, info);
            return this;
        }

        public SortedMap<Long, SchemaRevisionManager.RevisionSql> scan() {

            TreeMap<Long, SchemaRevisionManager.RevisionSql> result = new TreeMap<>();
            // scan
            for (String p : paths) {
                FlywaveRevisionScanner.scan(result, p);
            }

            if (result.isEmpty()) return result;

            // rename
            for (Map.Entry<Long, Map.Entry<Long, String>> ent : renames.entrySet()) {
                final Long old = ent.getKey();
                final SchemaRevisionManager.RevisionSql revi = result.remove(old);
                final Map.Entry<Long, String> vlu = ent.getValue();
                final Long rto = vlu.getKey();
                final String frc = vlu.getValue();

                if (revi == null) {
                    if (frc.isEmpty()) {
                        throw new IllegalStateException("failed to rename not-exist from=" + old + " to=" + rto);
                    } else {
                        logger.info("[FlywaveRevisionScanner]🐝 rename skipped not-exist from=" + old + " to=" + rto + ", force=" + frc);
                    }
                } else {
                    revi.setRevision(rto);
                    final SchemaRevisionManager.RevisionSql est = result.put(rto, revi);

                    if (est == null) {
                        logger.info("[FlywaveRevisionScanner]🐝 rename revi from=" + old + " to=" + rto);
                    } else {
                        if (frc.isEmpty()) {
                            throw new IllegalStateException("failed to rename from=" + old + " exist-to=" + rto);
                        } else {
                            logger.info("[FlywaveRevisionScanner]🐝 rename revi from=" + old + " exist-to=" + rto +
                                    ", force=" + frc +
                                    ", remove=\n" + est);
                        }
                    }
                }
            }

            if (includes.isEmpty() && excludes.isEmpty()) {
                return result;
            }

            // include
            result.entrySet().removeIf(it -> {
                for (Map.Entry<Predicate<SchemaRevisionManager.RevisionSql>, String> ent : includes.entrySet()) {
                    if (ent.getKey().test(it.getValue())) {
                        final String info = ent.getValue();
                        if (info != null && !info.isEmpty()) {
                            logger.info("[FlywaveRevisionScanner]🐝 include filter matches " + it.getKey() + " by " + info);
                        }
                        return false;
                    }
                }
                logger.info("[FlywaveRevisionScanner]🐝 include filter remove " + it.getKey());
                return true;
            });

            // exclude
            result.entrySet().removeIf(it -> {
                for (Map.Entry<Predicate<SchemaRevisionManager.RevisionSql>, String> ent : excludes.entrySet()) {
                    if (ent.getKey().test(it.getValue())) {
                        final String info = ent.getValue();
                        if (info == null || info.isEmpty()) {
                            logger.info("[FlywaveRevisionScanner]🐝 exclude filter remove " + it.getKey());
                        } else {
                            logger.info("[FlywaveRevisionScanner]🐝 exclude filter remove " + it.getKey() + " by " + info);
                        }
                        return true;
                    }
                }
                return false;
            });

            return result;
        }
    }
}
