package pro.fessional.wings.warlock.caching;

import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.Test;
import pro.fessional.wings.warlock.event.cache.TableChangeEvent;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/*
 * @author trydofor
 * @since 2024-01-15
 */
class CacheEventHelperTest {

    @Test
    @TmsLink("C14078")
    void receiveTable() {
        List<String> tables = List.of("table1", "table2", "TABLE3");
        //
        TableChangeEvent event = new TableChangeEvent();
        event.setTable("table0");
        assertNull(CacheEventHelper.receiveTable(null, tables));
        assertNull(CacheEventHelper.receiveTable(event, tables));
        event.setTable("TABLE1");
        assertEquals("TABLE1", CacheEventHelper.receiveTable(event, tables));
        event.setTable("table3");
        assertEquals("table3", CacheEventHelper.receiveTable(event, tables));

        event.setChange(TableChangeEvent.DELETE | TableChangeEvent.UPDATE);
        event.setTable("table0");
        assertNull(CacheEventHelper.receiveTable(null, tables, TableChangeEvent.INSERT));
        assertNull(CacheEventHelper.receiveTable(event, tables, TableChangeEvent.INSERT));
        event.setTable("table1");
        assertEquals("table1", CacheEventHelper.receiveTable(event, tables, TableChangeEvent.DELETE));
    }
}