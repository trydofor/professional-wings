package pro.fessional.wings.warlock.service.event;

import io.qameta.allure.TmsLink;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pro.fessional.mirana.time.Sleep;
import pro.fessional.wings.faceless.database.WingsTableCudHandler;
import pro.fessional.wings.warlock.app.service.TestTableChangeEvent;
import pro.fessional.wings.warlock.event.cache.TableChangeEvent;
import pro.fessional.wings.warlock.service.event.impl.WingsTableCudHandlerImpl;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * @author trydofor
 * @since 2024-01-15
 */
@SpringBootTest
@Slf4j
class TableChangePublisherTest {

    @Setter(onMethod_ = {@Autowired})
    protected WingsTableCudHandlerImpl wingsTableCudHandler;
    @Setter(onMethod_ = {@Autowired})
    protected TestTableChangeEvent testTableChangeEvent;

    @Test
    @TmsLink("C14080")
    void publishInsert() {
        final AtomicReference<TableChangeEvent> event = new AtomicReference<>();
        testTableChangeEvent.setConsumer(event::set);
        wingsTableCudHandler.register((source, cud, table) -> "auto_table".equals(table));
        wingsTableCudHandler.handle(TableChangePublisherTest.class, WingsTableCudHandler.Cud.Create, "auto_table");
        Sleep.ignoreInterrupt(1000);
        assertNull(event.get());
        wingsTableCudHandler.handle(TableChangePublisherTest.class, WingsTableCudHandler.Cud.Create, "test_table");
        Sleep.ignoreInterrupt(1000);
        TableChangeEvent ev2 = event.get();
        assertEquals("test_table", ev2.getTable());
    }
}