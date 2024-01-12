package pro.fessional.wings.testing.database;

import org.junit.jupiter.api.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import pro.fessional.mirana.data.Diff;
import pro.fessional.mirana.io.InputStreams;

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
public class WingsTestHelper {

    public static final long REVISION_TEST_V1 = 2019_0601_01L;
    public static final long REVISION_TEST_V2 = 2019_0601_02L;

    private static final Logger log = LoggerFactory.getLogger(WingsTestHelper.class);

    private final DataSource current;
    private final Map<String, DataSource> backends;

    private final boolean hasH2;

    public WingsTestHelper(DataSource current, Map<String, DataSource> backends) {
        this.current = current;
        this.backends = backends;
        boolean h2 = false;
        for (String s : backends.keySet()) {
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

    public void cleanTable() {
        // load table early for shardingsphere caching
        testcaseNotice("show tables of current");
        if (current != null) {
            new JdbcTemplate(current).execute("SHOW TABLES");
        }

        /*
         DROP DATABASE IF EXISTS wings;
         CREATE DATABASE `wings` DEFAULT CHARACTER SET utf8mb4;
         */
        for (Map.Entry<String, DataSource> en : backends.entrySet()) {
            testcaseNotice("clean database " + en.getKey());
            JdbcTemplate tmpl = new JdbcTemplate(en.getValue());
            tmpl.query("SHOW TABLES", rs -> {
                String tbl = rs.getString(1);
                testcaseNotice("drop table " + tbl);
                tmpl.execute("DROP TABLE `" + tbl + "`");
            });
        }
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
        for (Map.Entry<String, DataSource> en : backends.entrySet()) {
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
        String sqls = InputStreams.readText(WingsTestHelper.class.getResourceAsStream("/wings-flywave/" + path));
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
