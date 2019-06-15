package pro.fessional.wings.oracle.util;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.StreamUtils;
import pro.fessional.wings.oracle.flywave.SchemaVersionManger;

import javax.validation.constraints.NotNull;
import java.nio.charset.Charset;
import java.util.SortedMap;
import java.util.TreeMap;
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
     *
     * @param path 按Spring的格式写，classpath*:,classpath:等，默认[revisionSqlPath]
     * @see PathMatchingResourcePatternResolver
     */

    public static SortedMap<Long, SchemaVersionManger.RevisionSql> scan(@NotNull String path) {

        TreeMap<Long, SchemaVersionManger.RevisionSql> result = new TreeMap<>();
        String file = null;
        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources(path);
            Pattern notDigit = Pattern.compile("\\D");
            Charset utf8 = Charset.forName("UTF8");

            for (Resource res : resources) {
                file = res.getURL().getPath();
                if (!file.toLowerCase().endsWith(".sql")) {
                    continue;
                }

                String name = file.substring(file.lastIndexOf('/') + 1);
                Long revi = Long.parseLong(notDigit.matcher(name).replaceAll(""));

                SchemaVersionManger.RevisionSql d = result.computeIfAbsent(revi, key -> {
                    SchemaVersionManger.RevisionSql sql = new SchemaVersionManger.RevisionSql();
                    sql.setRevision(revi);
                    return sql;
                });

                String text = StreamUtils.copyToString(res.getInputStream(), utf8);

                if (name.contains("u") || name.contains("U")) {
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
