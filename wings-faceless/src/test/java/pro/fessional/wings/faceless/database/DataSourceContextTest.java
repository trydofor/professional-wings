package pro.fessional.wings.faceless.database;

import lombok.Setter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author trydofor
 * @since 2021-01-16
 */
@SpringBootTest(properties = {"debug = true"})
class DataSourceContextTest {

    @Setter(onMethod_ = {@Autowired})
    private DataSourceContext sourceContext;

    @Test
    void test() {
        assertNotNull(sourceContext);
        final DataSource primary = sourceContext.getPrimary();
        assertNotNull(primary);
        final DataSource sharding = sourceContext.getSharding();
        assertNull(sharding);
        final Map<String, DataSource> plains = sourceContext.getPlains();
        assertEquals(1, plains.size());
        assertTrue(plains.containsValue(primary));
    }
}
