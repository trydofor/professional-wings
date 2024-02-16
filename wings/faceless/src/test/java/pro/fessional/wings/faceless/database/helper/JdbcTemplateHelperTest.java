package pro.fessional.wings.faceless.database.helper;

import io.qameta.allure.TmsLink;
import lombok.Setter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pro.fessional.wings.faceless.database.DataSourceContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author trydofor
 * @since 2024-02-15
 */
@SpringBootTest
class JdbcTemplateHelperTest {

    @Setter(onMethod_ = {@Autowired})
    private DataSourceContext sourceContext;

    @Test
    @TmsLink("12148")
    void safeTable() {
        // init the context
        assertNotNull(sourceContext);

        assertTrue(JdbcTemplateHelper.isSafeTable("sys_light_sequence"));
        assertTrue(JdbcTemplateHelper.isSafeTable("SYS_LIGHT_SEQUENCE"));
        assertTrue(JdbcTemplateHelper.isSafeTable("`SYS_LIGHT_SEQUENCE`"));
        assertFalse(JdbcTemplateHelper.isSafeTable("SYS_LIGHT_SEQUENCE`"));

        assertEquals("`sys_light_sequence`", JdbcTemplateHelper.safeTable("sys_light_sequence"));
        assertEquals("`SYS_LIGHT_SEQUENCE`", JdbcTemplateHelper.safeTable("SYS_LIGHT_SEQUENCE"));
        assertEquals("`SYS_LIGHT_SEQUENCE`", JdbcTemplateHelper.safeTable("`SYS_LIGHT_SEQUENCE`"));
    }
}