package pro.fessional.wings.faceless.util;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.StreamUtils;
import pro.fessional.mirana.text.BuilderHelper;
import pro.fessional.wings.faceless.convention.EmptySugar;
import pro.fessional.wings.faceless.flywave.RevisionRegister;
import pro.fessional.wings.faceless.flywave.SchemaRevisionManager.RevisionSql;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author trydofor
 * @since 2019-06-14
 */
public class FlywaveRevisionScanner {

    private static final Logger log = LoggerFactory.getLogger(FlywaveRevisionScanner.class);

    public static final String REVISION_PATH_REVIFILE_EXTN = ".sql";
    public static final String REVISION_PATH_REVIFILE_TAIL = "**/*" + REVISION_PATH_REVIFILE_EXTN;
    public static final String REVISION_PATH_FLYWAVE_HEAD = "classpath*:/wings-flywave/";
    public static final String REVISION_PATH_MASTER_HEAD = REVISION_PATH_FLYWAVE_HEAD + "master/";
    public static final String REVISION_PATH_MASTER = REVISION_PATH_MASTER_HEAD + REVISION_PATH_REVIFILE_TAIL;
    public static final String REVISION_PATH_BRANCH_HEAD = REVISION_PATH_FLYWAVE_HEAD + "branch/";
    public static final String REVISION_PATH_FEATURE_HEAD = REVISION_PATH_BRANCH_HEAD + "feature/";
    public static final String REVISION_PATH_SUPPORT_HEAD = REVISION_PATH_BRANCH_HEAD + "support/";
    public static final String REVISION_PATH_SOMEFIX_HEAD = REVISION_PATH_BRANCH_HEAD + "somefix/";
    public static final String REVISION_PATH_BRANCH_FULL = REVISION_PATH_BRANCH_HEAD + REVISION_PATH_REVIFILE_TAIL;

    @NotNull
    public static String flywavePath(String name) {
        return prefixPath(REVISION_PATH_FLYWAVE_HEAD, name);
    }

    @NotNull
    public static String masterPath(String name) {
        return prefixPath(REVISION_PATH_MASTER_HEAD, name);
    }

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
        if (name == null) {
            return prefix + REVISION_PATH_REVIFILE_TAIL;
        }

        StringBuilder sb = new StringBuilder(100);
        sb.append(prefix);
        for (String pt : name.split("[/\\\\]+")) {
            pt = pt.trim();
            if (!pt.isEmpty()) {
                sb.append(pt).append("/");
            }
        }

        // sql 文件
        final int dot = sb.length() - REVISION_PATH_REVIFILE_EXTN.length() - 1;
        if (dot > 0) {
            final int lst = sb.length() - 1;
            final String en = sb.substring(dot, lst);
            if (en.equalsIgnoreCase(REVISION_PATH_REVIFILE_EXTN)) {
                return sb.substring(0, lst);
            }
        }

        // 目录
        sb.append(REVISION_PATH_REVIFILE_TAIL);
        return sb.toString();
    }

    @NotNull
    public static String commentInfo(String... path) {
        Pattern tknRegex = Pattern.compile("[/\\\\]wings-flywave[/\\\\]([^:]*[/\\\\])([-_0-9]{8,}[uv][0-9]{2,})([^/]*\\.sql)$", Pattern.CASE_INSENSITIVE);

        LinkedHashSet<String> info = new LinkedHashSet<>();
        for (String s : path) {
            Matcher m = tknRegex.matcher(s);
            if (m.find()) {
                BuilderHelper.W sb = BuilderHelper.w();
                sb.append(m.group(1));
                sb.append(formatRevi(m.group(2)));
                sb.append(m.group(3));
                info.add(sb.toString());
            }
            else {
                info.add(s);
            }
        }
        info.removeIf(String::isBlank);

        return String.join(", ", info);
    }

    public static String formatRevi(String revi) {
        StringBuilder sb = new StringBuilder(revi.length());
        int cnt = 0;
        for (int i = 0, len = revi.length(); i < len; i++) {
            char c = revi.charAt(i);
            if (c == 'u' || c == 'U' || c == 'v' || c == 'V') {
                sb.append("_");
                cnt = 0;
            }
            else if (c >= '0' && c <= '9') {
                if (cnt > 0 && cnt % 4 == 0) {
                    sb.append("-");
                }
                cnt++;
                sb.append(c);
            }
        }
        final int lst = sb.length() - 1;
        if (sb.charAt(lst) == '-') {
            return sb.substring(0, lst);
        }
        else {
            return sb.toString();
        }
    }

    @NotNull
    public static SortedMap<Long, RevisionSql> scanMaster() {
        return scan(REVISION_PATH_MASTER);
    }

    @NotNull
    public static SortedMap<Long, RevisionSql> scanMaster(String... name) {
        TreeMap<Long, RevisionSql> result = new TreeMap<>();
        for (String n : name) {
            scan(result, masterPath(n));
        }
        return result;
    }

    @NotNull
    public static SortedMap<Long, RevisionSql> scanBranch(String... name) {
        TreeMap<Long, RevisionSql> result = new TreeMap<>();
        for (String n : name) {
            scan(result, branchPath(n));
        }
        return result;
    }

    /**
     * @param path FlywaveRevisionRegister
     * @return 按版本号升序排列的TreeMap
     * @see PathMatchingResourcePatternResolver
     */
    @NotNull
    public static SortedMap<Long, RevisionSql> scan(@NotNull RevisionRegister... path) {
        TreeMap<Long, RevisionSql> result = new TreeMap<>();
        for (RevisionRegister p : path) {
            scan(result, p.classpath());
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
    public static SortedMap<Long, RevisionSql> scan(@NotNull String... path) {
        return scan(Arrays.asList(path));
    }

    @NotNull
    public static SortedMap<Long, RevisionSql> scan(@NotNull Collection<String> path) {
        TreeMap<Long, RevisionSql> result = new TreeMap<>();
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
    public static void scan(SortedMap<Long, RevisionSql> result, String path) {
        String file = null;
        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources(path);
            log.info("[FlywaveRevisionScanner]🐝 scanned " + resources.length + " resources in path=" + path);
            Pattern reviRegex = Pattern.compile("([-_0-9]{8,})([uv])([0-9]{2,})[^/]*\\.sql$", Pattern.CASE_INSENSITIVE);
            Charset utf8 = StandardCharsets.UTF_8;

            final HashSet<Long> newRevi = new HashSet<>();
            final HashSet<Long> rplRevi = new HashSet<>();
            for (Resource res : resources) {
                file = res.getURL().getPath();
                Matcher m = reviRegex.matcher(file);
                if (!m.find()) {
                    log.info("[FlywaveRevisionScanner]🐝 skip unsupported resource=" + file);
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
                final Long revi = Long.valueOf(sb.toString());
                newRevi.add(revi);

                RevisionSql d = result.computeIfAbsent(revi, RevisionSql::new);

                String text = StreamUtils.copyToString(res.getInputStream(), utf8);

                if (undo) {
                    final String ou = d.getUndoPath();
                    if (EmptySugar.asEmptyValue(ou)) {
                        log.info("[FlywaveRevisionScanner]🐝 scan " + revi + " undo↓ resource=" + file);
                    }
                    else {
                        rplRevi.add(revi);
                        log.warn("[FlywaveRevisionScanner]🐝 replace " + revi + " undo↓ new=" + file + ", old=" + ou);
                    }
                    d.setUndoPath(file);
                    d.setUndoText(text);
                }
                else {
                    final String ou = d.getUptoPath();
                    if (EmptySugar.asEmptyValue(ou)) {
                        log.info("[FlywaveRevisionScanner]🐝 scan " + revi + " upto↑ resource=" + file);
                    }
                    else {
                        rplRevi.add(revi);
                        log.warn("[FlywaveRevisionScanner]🐝 replace " + revi + " upto↑ new=" + file + ", old=" + ou);
                    }
                    d.setUptoPath(file);
                    d.setUptoText(text);
                }
            }
            log.info("[FlywaveRevisionScanner]🐝 scanned revisions new=" + newRevi.size() + ", replace=" + rplRevi.size());
        }
        catch (Exception e) {
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
    public static String undo(SortedMap<Long, RevisionSql> sqls) {
        BuilderHelper.W sb = BuilderHelper.w();
        if (sqls != null) {
            sb.join(true, "\n", sqls.values(), RevisionSql::getUndoText);
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
    public static String upto(SortedMap<Long, RevisionSql> sqls) {
        BuilderHelper.W sb = BuilderHelper.w();
        if (sqls != null) {
            sb.join(true, "\n", sqls.values(), RevisionSql::getUptoText);
        }
        return sb.toString();
    }

    /**
     * 可应用版本过滤器和重命名
     *
     * @return 构造器
     * @see Helper
     */
    public static Helper helper() {
        return new Helper();
    }

    /**
     * 执行以下步骤
     * ①scan全路径
     * ②replace版本
     * ③include过滤器
     * ④exclude过滤器
     * ⑤modifier调整
     */
    public static class Helper {
        private final LinkedHashMap<Predicate<Long>, String> includes = new LinkedHashMap<>();
        private final LinkedHashMap<Predicate<Long>, String> excludes = new LinkedHashMap<>();
        private final HashMap<Long, Long> replaces = new HashMap<>();
        private final LinkedHashMap<BiConsumer<Long, RevisionSql>, String> modifier = new LinkedHashMap<>();
        private final LinkedHashSet<String> paths = new LinkedHashSet<>();

        public Helper path(RevisionRegister... path) {
            for (RevisionRegister s : path) {
                paths.add(s.classpath());
            }
            return this;
        }

        public Helper path(String... path) {
            Collections.addAll(paths, path);
            return this;
        }

        public Helper flywave(String... path) {
            for (String s : path) {
                paths.add(FlywaveRevisionScanner.flywavePath(s));
            }
            return this;
        }

        public Helper master() {
            paths.add(REVISION_PATH_MASTER);
            return this;
        }

        public Helper master(String... path) {
            for (String s : path) {
                paths.add(FlywaveRevisionScanner.masterPath(s));
            }
            return this;
        }

        public Helper branch(String... path) {
            for (String s : path) {
                paths.add(FlywaveRevisionScanner.branchPath(s));
            }
            return this;
        }

        public Helper feature(String... path) {
            for (String s : path) {
                paths.add(FlywaveRevisionScanner.featurePath(s));
            }
            return this;
        }

        public Helper somefix(String... path) {
            for (String s : path) {
                paths.add(FlywaveRevisionScanner.somefixPath(s));
            }
            return this;
        }

        public Helper support(String... path) {
            for (String s : path) {
                paths.add(FlywaveRevisionScanner.supportPath(s));
            }
            return this;
        }

        /**
         * 把from版替换为to版，新版不存在时创建。
         *
         * @param from 旧版本
         * @param to   新版本
         * @return builder
         */
        public Helper replace(long from, long to) {
            return replace(from, to, false);
        }

        /**
         * 把from版替换为to版，新版不存在时创建。同时使用fn处理from和to版
         *
         * @param from 旧版本
         * @param to   新版本
         * @param sql  同时replace undo和upto脚本中的版本号
         * @return builder
         */
        public Helper replace(long from, long to, boolean sql) {
            if (sql) {
                final Pattern op = Pattern.compile("\\b" + from + "\\b");
                final String ns = String.valueOf(to);
                return replace(from, to, it -> op.matcher(it).replaceAll(ns));
            }
            else {
                return replace(from, to, null);
            }
        }

        /**
         * 把from版替换为to版，新版不存在时创建。同时使用fn处理from和to版
         *
         * @param from 旧版本
         * @param to   新版本
         * @param mod  同时replace undo和upto脚本中的方法
         * @return builder
         */
        public Helper replace(long from, long to, Function<String, String> mod) {
            if (from < 0 || to < 0) throw new IllegalArgumentException("revi must >0");
            replaces.put(from, to);
            if (mod != null) {
                modify("replace " + from + " to " + to + " with sql", to, it -> {
                    it.setUptoText(mod.apply(it.getUptoText()));
                    it.setUndoText(mod.apply(it.getUndoText()));
                    log.info("[FlywaveRevisionScanner]🐝 replace revi from=" + from + " to=" + to + " with sql text");
                });
            }
            return this;
        }

        /**
         * 调整RevisionSql内容
         *
         * @param revi 声明版本
         * @param str  查找字符串
         * @param rpl  替换字符串
         * @return builder
         * @see #modify(String, BiConsumer)
         */
        public Helper modify(long revi, String str, String rpl) {
            return modify("replace " + str + " to " + rpl + " at revi=" + revi, revi, it -> {
                it.setUptoText(it.getUptoText().replace(str, rpl));
                it.setUndoText(it.getUndoText().replace(str, rpl));
            });
        }

        /**
         * 调整RevisionSql内容
         *
         * @param revi 声明版本
         * @param mod  调整函数 Consumer(实际SQL)
         * @return builder
         * @see #modify(String, BiConsumer)
         */
        public Helper modify(long revi, Consumer<RevisionSql> mod) {
            return modify("", revi, mod);
        }

        /**
         * 调整RevisionSql内容
         *
         * @param info 用途说明
         * @param revi 声明版本
         * @param mod  调整函数 Consumer(实际SQL)
         * @return builder
         * @see #modify(String, BiConsumer)
         */
        public Helper modify(String info, long revi, Consumer<RevisionSql> mod) {
            modifier.put((r, s) -> {if (r == revi) mod.accept(s);}, info);
            return this;
        }

        /**
         * 调整RevisionSql内容
         *
         * @param mod 调整函数 BiConsumer(声明版本, 实际SQL)
         * @return builder
         */
        public Helper modify(BiConsumer<Long, RevisionSql> mod) {
            return modify("", mod);
        }

        /**
         * 调整RevisionSql内容
         *
         * @param info 用途说明
         * @param mod  调整函数 BiConsumer(声明版本, 实际SQL)
         * @return builder
         */
        public Helper modify(String info, BiConsumer<Long, RevisionSql> mod) {
            modifier.put(mod, info);
            return this;
        }


        public Helper include(RevisionRegister revi) {
            return include(revi.description(), revi.revision());
        }

        public Helper include(long... revi) {
            return include("", revi);
        }

        public Helper include(String info, long... revi) {
            final HashSet<Long> rvs = new HashSet<>();
            for (Long l : revi) {
                rvs.add(l);
            }
            return include(info, rvs::contains);
        }

        public Helper include(Predicate<Long> inc) {
            return include("", inc);
        }

        public Helper include(String info, Predicate<Long> inc) {
            includes.put(inc, info);
            return this;
        }


        public Helper exclude(RevisionRegister revi) {
            return exclude(revi.description(), revi.revision());
        }

        public Helper exclude(long... revi) {
            return exclude("", revi);
        }

        public Helper exclude(String info, long... revi) {
            final HashSet<Long> rvs = new HashSet<>();
            for (Long l : revi) {
                rvs.add(l);
            }
            return exclude(info, rvs::contains);
        }

        public Helper exclude(Predicate<Long> exc) {
            return exclude("", exc);
        }

        public Helper exclude(String info, Predicate<Long> exc) {
            excludes.put(exc, info);
            return this;
        }

        public SortedMap<Long, RevisionSql> scan() {

            TreeMap<Long, RevisionSql> result = new TreeMap<>();
            // scan
            for (String p : paths) {
                FlywaveRevisionScanner.scan(result, p);
            }

            if (result.isEmpty()) return result;

            // replace
            for (Map.Entry<Long, Long> ent : replaces.entrySet()) {
                final Long ov = ent.getKey();
                final Long nv = ent.getValue();
                if (ov.equals(nv)) continue;

                final RevisionSql old = result.remove(ov);
                if (old == null) {
                    throw new IllegalStateException("failed to replace not-exist from=" + ov + " to=" + nv);
                }

                final RevisionSql tor = result.put(nv, old);
                if (tor != null) {
                    log.info("[FlywaveRevisionScanner]🐝 replace revi from=" + ov + " to=" + nv + ", exist=" + tor);
                }
            }

            // include
            if (!includes.isEmpty()) {
                result.entrySet().removeIf(it -> {
                    for (Map.Entry<Predicate<Long>, String> ent : includes.entrySet()) {
                        if (ent.getKey().test(it.getKey())) {
                            final String info = ent.getValue();
                            if (info != null && !info.isEmpty()) {
                                log.info("[FlywaveRevisionScanner]🐝 include " + it.getKey() + " by " + info);
                            }
                            else {
                                log.info("[FlywaveRevisionScanner]🐝 include " + it.getKey());
                            }
                            return false;
                        }
                    }
                    log.info("[FlywaveRevisionScanner]🐝 remove " + it.getKey() + " by include filter unmatched");
                    return true;
                });
            }

            // exclude
            if (!excludes.isEmpty()) {
                result.entrySet().removeIf(it -> {
                    for (Map.Entry<Predicate<Long>, String> ent : excludes.entrySet()) {
                        if (ent.getKey().test(it.getKey())) {
                            final String info = ent.getValue();
                            if (info == null || info.isEmpty()) {
                                log.info("[FlywaveRevisionScanner]🐝 remove " + it.getKey() + " by exclude filter matched");
                            }
                            else {
                                log.info("[FlywaveRevisionScanner]🐝 remove " + it.getKey() + " by " + info);
                            }
                            return true;
                        }
                    }
                    return false;
                });
            }

            // modifier
            for (Map.Entry<BiConsumer<Long, RevisionSql>, String> mod : modifier.entrySet()) {
                log.info("[FlywaveRevisionScanner]🐝 modify RevisionSql by " + mod.getValue());
                final BiConsumer<Long, RevisionSql> fn = mod.getKey();
                for (Map.Entry<Long, RevisionSql> ent : result.entrySet()) {
                    fn.accept(ent.getKey(), ent.getValue());
                }
            }

            return result;
        }
    }
}
