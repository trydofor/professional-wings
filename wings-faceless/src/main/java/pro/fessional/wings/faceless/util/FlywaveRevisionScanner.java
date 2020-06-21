package pro.fessional.wings.faceless.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.StreamUtils;
import pro.fessional.mirana.text.BuilderHelper;
import pro.fessional.wings.faceless.flywave.SchemaRevisionManager;

import javax.validation.constraints.NotNull;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashSet;
import java.util.SortedMap;
import java.util.TreeMap;
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
    public static final String REVISION_PATH_BRANCH_TAIL = "**/*.sql";
    public static final String REVISION_PATH_BRANCH_FULL = REVISION_PATH_BRANCH_HEAD + REVISION_PATH_BRANCH_TAIL;
    public static final String REVISION_PATH_BRANCH_3RD_ENU18N = branchPath("features/enum-i18n");
    public static final String REVISION_PATH_BRANCH_FIX_V2_2_7 = branchPath("hotfixes/v2.2.7-fix");

    public static final long REVISION_1ST_SCHEMA = 2019_0512_01L;
    public static final long REVISION_2ND_IDLOGS = 2019_0520_01L;
    public static final long REVISION_3RD_ENU18N = 2019_0521_01L;

    public static String branchPath(String name) {
        if (name == null || name.isEmpty()) return REVISION_PATH_BRANCH_FULL;
        int p1 = 0;
        while (name.startsWith("/", p1)) {
            p1++;
        }
        int p2 = name.length();
        while (name.startsWith("/", p2 - 1)) {
            p2--;
        }
        StringBuilder sb = new StringBuilder(100);
        sb.append(REVISION_PATH_BRANCH_HEAD);
        if (p1 < p2) {
            name = name.substring(p1, p2);
        } else {
            return REVISION_PATH_BRANCH_FULL;
        }
        String trim = name.trim();
        sb.append(trim);
        if (trim.length() > 0) {
            sb.append("/");
        }
        sb.append(REVISION_PATH_BRANCH_TAIL);
        return sb.toString();
    }

    public static String commentInfo(String... path) {
        Pattern tknRegex = Pattern.compile("[/\\\\]wings-flywave[/\\\\]([^:]*[/\\\\])[-_0-9]{8,}[uv][0-9]{2,}([^/]*\\.sql)$", Pattern.CASE_INSENSITIVE);

        LinkedHashSet<String> info = new LinkedHashSet<>();
        for (String s : path) {
            Matcher m = tknRegex.matcher(s);
            if (m.find()) {
                StringBuilder sb = new StringBuilder();
                BuilderHelper.appendNotNull(sb, m.group(1));
                sb.append("*");
                BuilderHelper.appendNotNull(sb, m.group(2));
                info.add(sb.toString());
            } else {
                info.add(s);
            }
        }

        return String.join(", ", info);
    }

    public static SortedMap<Long, SchemaRevisionManager.RevisionSql> scanMaster() {
        return scan(REVISION_PATH_MASTER);
    }

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

                SchemaRevisionManager.RevisionSql d = result.computeIfAbsent(revi, key -> {
                    SchemaRevisionManager.RevisionSql sql = new SchemaRevisionManager.RevisionSql();
                    sql.setRevision(revi);
                    return sql;
                });

                String text = StreamUtils.copyToString(res.getInputStream(), utf8);

                if (undo) {
                    logger.info("[FlywaveRevisionScanner]🐝 scan " + revi + " undo↓ resource=" + file);
                    d.setUndoPath(file);
                    d.setUndoText(text);
                } else {
                    logger.info("[FlywaveRevisionScanner]🐝 scan " + revi + " upto↑ resource=" + file);
                    d.setUptoPath(file);
                    d.setUptoText(text);
                }
            }
        } catch (Exception e) {
            throw new IllegalStateException("failed to scan path = " + path + ", file=" + file, e);
        }
    }
}
