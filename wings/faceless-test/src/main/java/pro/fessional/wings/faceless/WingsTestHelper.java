package pro.fessional.wings.faceless;

import lombok.Setter;
import org.junit.jupiter.api.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import pro.fessional.mirana.data.Diff;
import pro.fessional.wings.faceless.database.DataSourceContext;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * @author trydofor
 * @since 2020-05-22
 */
@Component
public class WingsTestHelper {

    public static final long REVISION_TEST_V1 = 2019_0601_01L;
    public static final long REVISION_TEST_V2 = 2019_0601_02L;

    private static final Logger log = LoggerFactory.getLogger(WingsTestHelper.class);

    @Setter(onMethod_ = {@Autowired})
    private DataSourceContext dataSourceContext;

    private final HashMap<DataSource, Boolean> isH2Map = new HashMap<>();

    public boolean isH2() {
        for (DataSource ds : dataSourceContext.getBackends().values()) {
            Boolean h2 = isH2Map.computeIfAbsent(ds, dataSource -> {
                String s = dataSourceContext.cacheJdbcUrl(dataSource);
                return s.contains(":h2:") || s.contains(":H2:");
            });
            if (h2) return true;
        }
        return false;
    }

    public void cleanTable() {
        /*
         DROP DATABASE IF EXISTS wings;
         CREATE DATABASE `wings` DEFAULT CHARACTER SET utf8mb4;
         */
        dataSourceContext.getBackends().forEach((k, v) -> {
            testcaseNotice("clean database " + k);
            JdbcTemplate tmpl = new JdbcTemplate(v);
            tmpl.query("SHOW TABLES", rs -> {
                String tbl = rs.getString(1);
                testcaseNotice("drop table " + tbl);
                tmpl.execute("DROP TABLE `" + tbl + "`");
            });
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
        return dataSourceContext
                .getBackends().entrySet().stream()
                .collect(
                        Collectors.toMap(
                                Map.Entry::getKey,
                                e -> new HashSet<>(new JdbcTemplate(e.getValue())
                                        .query(sql, (rs, ignored) -> rs.getString(1).toLowerCase())
                                )
                        )
                );

    }

    public static void testcaseNotice(String... mes) {
        for (String s : mes) {
            log.info(">>=>🦁🦁🦁 " + s + " 🦁🦁🦁<=<<");
        }
    }

    public static void breakpointDebug(String... mes) {
        Arrays.stream(mes).forEach(s -> log.debug(">>=>🐶🐶🐶 " + s + " 🐶🐶🐶<=<<"));
    }
}
