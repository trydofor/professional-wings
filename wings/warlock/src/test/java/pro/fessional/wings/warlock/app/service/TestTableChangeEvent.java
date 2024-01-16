package pro.fessional.wings.warlock.app.service;

import lombok.Setter;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import pro.fessional.wings.warlock.event.cache.TableChangeEvent;

import java.util.function.Consumer;

/**
 * @author trydofor
 * @since 2024-01-15
 */
@Component
public class TestTableChangeEvent {

    @Setter
    private Consumer<TableChangeEvent> consumer;

    @EventListener
    public void handle(TableChangeEvent event) {
        if (consumer != null) {
            consumer.accept(event);
        }
    }
}
