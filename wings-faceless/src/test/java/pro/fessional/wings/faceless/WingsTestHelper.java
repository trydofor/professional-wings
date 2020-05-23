package pro.fessional.wings.faceless;

import com.google.common.collect.Sets;
import lombok.Setter;
import org.apache.logging.log4j.util.Strings;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import pro.fessional.mirana.data.Diff;
import pro.fessional.wings.faceless.flywave.FlywaveDataSources;
import pro.fessional.wings.faceless.flywave.SchemaRevisionManager;
import pro.fessional.wings.faceless.util.FlywaveRevisionSqlScanner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * @author trydofor
 * @since 2020-05-22
 */
@Component
public class WingsTestHelper {

    private Logger logger = LoggerFactory.getLogger(WingsTestHelper.class);

    @Setter(onMethod = @__({@Autowired}))
    private SchemaRevisionManager schemaRevisionManager;
    @Setter(onMethod = @__({@Autowired}))
    private FlywaveDataSources flywaveDataSources;

    public void cleanAndInit() {
        flywaveDataSources.plains().forEach((k, v) -> {
            note("clean database " + k);
            JdbcTemplate tmpl = new JdbcTemplate(v);
            tmpl.query("SHOW TABLES", rs -> {
                String tbl = rs.getString(1);
                note("drop table " + tbl);
                tmpl.execute("DROP TABLE " + tbl);
            });
        });

        SortedMap<Long, SchemaRevisionManager.RevisionSql> sqls = FlywaveRevisionSqlScanner.scan(SchemaRevisionManager.REVISIONSQL_PATH);
        schemaRevisionManager.checkAndInitSql(sqls, 0, true);
    }

    public enum Type {
        Table("SHOW TABLES"),
        Trigger("SELECT TRIGGER_NAME FROM INFORMATION_SCHEMA.TRIGGERS WHERE EVENT_OBJECT_SCHEMA = database()"),
        ;
        private final String sql;

        Type(String sql) {
            this.sql = sql;
        }
    }

    public void assertSame(Type type, String... str) {
        List<String> bSet = Arrays.asList(str);
        AtomicBoolean good = new AtomicBoolean(true);
        fetchAllColumn1(type.sql).forEach((k, aSet) -> {
            Diff.S<String> diff = Diff.of(aSet, bSet);
            if (!diff.bNotA.isEmpty()) {
                note(k + " 数据库少：" + type + ":" + Strings.join(diff.bNotA, ','));
                good.set(false);
            }
            if (!diff.aNotB.isEmpty()) {
                note(k + " 数据库多：" + type + ":" + Strings.join(diff.aNotB, ','));
                good.set(false);
            }
        });

        Assert.assertTrue(type.name() + "不一致，查看日志，", good.get());
    }

    public void assertHas(Type type, String... str) {
        List<String> bSet = Arrays.asList(str);
        AtomicBoolean good = new AtomicBoolean(true);
        fetchAllColumn1(type.sql).forEach((k, aSet) -> {
            Diff.S<String> diff = Diff.of(aSet, bSet);
            if (!diff.bNotA.isEmpty()) {
                note(k + " 数据库少：" + type + ":" + Strings.join(diff.bNotA, ','));
                good.set(false);
            }
        });

        Assert.assertTrue(type.name() + "不一致，查看日志，", good.get());
    }

    public void assertNot(Type type, String... str) {
        List<String> bSet = Arrays.asList(str);
        AtomicBoolean good = new AtomicBoolean(true);
        fetchAllColumn1(type.sql).forEach((k, aSet) -> {
            Diff.S<String> diff = Diff.of(aSet, bSet);
            if (diff.bNotA.size() != bSet.size()) {
                note(k + " 数据库不能有：" + type + ": " + Strings.join(diff.bNotA, ','));
                good.set(false);
            }
        });

        Assert.assertTrue(type.name() + "不一致，查看日志，", good.get());
    }

    public Map<String, Set<String>> fetchAllColumn1(String sql) {
        return flywaveDataSources
                .plains().entrySet().stream()
                .collect(
                        Collectors.toMap(
                                Map.Entry::getKey,
                                e -> new HashSet<>(new JdbcTemplate(e.getValue())
                                        .query(sql, (rs, i) -> rs.getString(1))
                                )
                        )
                );

    }

    public void note(String... mes) {
        for (String s : mes) {
            logger.warn(">>=>🦁🦁🦁 " + s + " 🦁🦁🦁<=<<");
        }
    }
}
