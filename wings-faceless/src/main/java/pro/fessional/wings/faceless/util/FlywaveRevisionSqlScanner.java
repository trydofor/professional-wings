package pro.fessional.wings.faceless.util;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.StreamUtils;
import pro.fessional.wings.faceless.flywave.SchemaRevisionManager;

import javax.validation.constraints.NotNull;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author trydofor
 * @since 2019-06-14
 */
public class FlywaveRevisionSqlScanner {

    private FlywaveRevisionSqlScanner() {
    }

    /**
     * 把指定或默认位置的*.sql文件都加载进来，并按文件名的字母顺序排序。
     * String path = "classpath*:/wings-flywave/revision/"; // 全部类路径
     * String path = "classpath:/wings-flywave/revision/";  // 当前类路径
     * String path = "file:src/main/resources/wings-flywave/revision/"; // 具体文件
     *
     * @param path 按Spring的格式写，classpath*:,classpath:等，默认[REVISIONSQL_PATH]
     * @see PathMatchingResourcePatternResolver
     */

    public static SortedMap<Long, SchemaRevisionManager.RevisionSql> scan(@NotNull String path) {

        TreeMap<Long, SchemaRevisionManager.RevisionSql> result = new TreeMap<>();
        String file = null;
        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources(path);
            Pattern reviRegex = Pattern.compile("(\\d{8,})([uv])(\\d{2,})[^/]*\\.sql$", Pattern.CASE_INSENSITIVE);
            Charset utf8 = StandardCharsets.UTF_8;

            for (Resource res : resources) {
                file = res.getURL().getPath();
                Matcher m = reviRegex.matcher(file);
                if (!m.find()) {
                    continue;
                }
                boolean undo = m.group(2).equalsIgnoreCase("u");
                long revi = Long.parseLong(m.group(1) + m.group(3));

                SchemaRevisionManager.RevisionSql d = result.computeIfAbsent(revi, key -> {
                    SchemaRevisionManager.RevisionSql sql = new SchemaRevisionManager.RevisionSql();
                    sql.setRevision(revi);
                    return sql;
                });

                String text = StreamUtils.copyToString(res.getInputStream(), utf8);

                if (undo) {
                    d.setUndoPath(file);
                    d.setUndoText(text);
                } else {
                    d.setUptoPath(file);
                    d.setUptoText(text);
                }
            }
        } catch (Exception e) {
            throw new IllegalStateException("failed to scan path = " + path + ", file=" + file, e);
        }

        return result;
    }
}
