package pro.fessional.wings.testing.faceless.database;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.springframework.jdbc.core.JdbcTemplate;
import pro.fessional.mirana.data.Diff;
import pro.fessional.mirana.io.InputStreams;
import pro.fessional.wings.faceless.database.DataSourceContext;
import pro.fessional.wings.faceless.database.helper.JdbcTemplateHelper;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * @author trydofor
 * @since 2020-05-22
 */
@Slf4j
public class TestingDatabaseHelper {

    private final DataSourceContext dataSourceContext;

    private final boolean hasH2;

    public TestingDatabaseHelper(DataSourceContext context) {
        this.dataSourceContext = context;
        boolean h2 = false;
        for (String s : context.getBackends().keySet()) {
            if (s.contains(":h2:") || s.contains(":H2:")) {
                h2 = true;
                break;
            }
        }
        hasH2 = h2;
    }

    public boolean hasH2() {
        return hasH2;
    }

    public DataSourceContext getDataSourceContext() {
        return dataSourceContext;
    }

    /**
     * DROP DATABASE IF EXISTS wings;
     * CREATE DATABASE `wings` DEFAULT CHARACTER SET utf8mb4;
     */
    public void cleanTable() {
        for (Map.Entry<String, DataSource> en : dataSourceContext.getBackends().entrySet()) {
            cleanTable(en.getValue(), en.getKey());
        }
    }

    public void cleanTable(DataSource dataSource, String info) {
        testcaseNotice("clean database " + info);
        JdbcTemplate tmpl = new JdbcTemplate(dataSource);
        tmpl.query("SHOW TABLES", rs -> {
            String tbl = JdbcTemplateHelper.safeName(rs.getString(1));
            testcaseNotice("DROP TABLE " + tbl);
            //noinspection SqlSourceToSinkFlow
            tmpl.execute("DROP TABLE " + tbl);
        });
    }

    public enum Type {
        Table("SHOW TABLES"),
        Trigger("SELECT TRIGGER_NAME FROM INFORMATION_SCHEMA.TRIGGERS WHERE EVENT_OBJECT_SCHEMA = SCHEMA()"),
        ;
        private final String sql;

        Type(String sql) {
            this.sql = sql;
        }
    }

    public void assertSame(Type type, String... str) {
        List<String> bSet = lowerCase(str);
        AtomicBoolean good = new AtomicBoolean(true);
        fetchAllColumn1(type.sql).forEach((k, aSet) -> {
            Diff.S<String> diff = Diff.of(aSet, bSet);
            if (!diff.bNotA.isEmpty()) {
                testcaseNotice(k + " less in db " + type + ":" + String.join(",", diff.bNotA));
                good.set(false);
            }
            if (!diff.aNotB.isEmpty()) {
                testcaseNotice(k + " more in db " + type + ":" + String.join(",", diff.aNotB));
                good.set(false);
            }
        });

        Assertions.assertTrue(good.get(), type.name() + " difference, check the logs.");
    }

    public void assertHas(Type type, String... str) {
        List<String> bSet = lowerCase(str);
        AtomicBoolean good = new AtomicBoolean(true);
        fetchAllColumn1(type.sql).forEach((k, aSet) -> {
            Diff.S<String> diff = Diff.of(aSet, bSet);
            if (!diff.bNotA.isEmpty()) {
                testcaseNotice(k + " less in db " + type + ":" + String.join(",", diff.bNotA));
                good.set(false);
            }
        });

        Assertions.assertTrue(good.get(), type.name() + " difference, check the logs.");
    }

    public void assertNot(Type type, String... str) {
        List<String> bSet = lowerCase(str);
        AtomicBoolean good = new AtomicBoolean(true);
        fetchAllColumn1(type.sql).forEach((k, aSet) -> {
            Diff.S<String> diff = Diff.of(aSet, bSet);
            if (diff.bNotA.size() != bSet.size()) {
                testcaseNotice(k + " cant in db " + type + ":" + String.join(",", diff.bNotA));
                good.set(false);
            }
        });

        Assertions.assertTrue(good.get(), type.name() + " difference, check the logs.");
    }

    private List<String> lowerCase(String... str) {
        return Arrays.stream(str).map(String::toLowerCase).collect(Collectors.toList());
    }

    private Map<String, Set<String>> fetchAllColumn1(String sql) {
        Map<String, Set<String>> result = new LinkedHashMap<>();
        for (Map.Entry<String, DataSource> en : dataSourceContext.getBackends().entrySet()) {
            List<String> col = new JdbcTemplate(en.getValue())
                    .query(sql, (rs, ignored) -> rs.getString(1).toLowerCase());
            result.put(en.getKey(), new LinkedHashSet<>(col));
        }
        return result;
    }

    public static void testcaseNotice(String... mes) {
        for (String s : mes) {
            log.info(">>=>🦁🦁🦁 " + s + " 🦁🦁🦁<=<<");
        }
    }

    public static void breakpointDebug(String... mes) {
        Arrays.stream(mes).forEach(s -> log.debug(">>=>🐶🐶🐶 " + s + " 🐶🐶🐶<=<<"));
    }

    public static void execWingsSql(JdbcTemplate jdbcTemplate, String path) {
        String sqls = InputStreams.readText(TestingDatabaseHelper.class.getResourceAsStream("/wings-flywave/" + path));
        for (String sql : sqls.split(
                ";+[ \\t]*[\\r\\n]+"
                + "|"
                + ";+[ \\t]*--[^\\r\\n]+[\\r\\n]+"
                + "|"
                + ";+[ \\t]*/\\*[^\\r\\n]+\\*/[ \\t]*[\\r\\n]+"
        )) {
            String s = sql.trim();
            if (!s.isEmpty()) {
                jdbcTemplate.execute(s);
            }
        }
    }
}
