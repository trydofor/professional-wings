package pro.fessional.wings.faceless.database;

import lombok.Setter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author trydofor
 * @since 2021-01-16
 */
@SpringBootTest
class DataSourceContextTest {

    @Setter(onMethod_ = {@Autowired})
    private DataSourceContext sourceContext;

    @Test
    void test() {
        assertNotNull(sourceContext);
        final DataSource primary = sourceContext.getCurrent();
        assertNotNull(primary);
        final Map<String, DataSource> plains = sourceContext.getBackends();
        assertEquals(1, plains.size());
        assertTrue(plains.containsValue(primary));
    }
}
