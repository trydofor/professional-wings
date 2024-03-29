package pro.fessional.wings.faceless.database;

import io.qameta.allure.TmsLink;
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
    @TmsLink("C12004")
    void dbctxBackend() {
        assertNotNull(sourceContext);
        final DataSource primary = sourceContext.getCurrent();
        assertNotNull(primary);
        final Map<String, DataSource> backends = sourceContext.getBackends();
        assertEquals(1, backends.size());
        assertTrue(backends.containsValue(primary));
    }
}
