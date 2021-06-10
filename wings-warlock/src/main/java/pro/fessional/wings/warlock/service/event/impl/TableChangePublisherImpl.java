package pro.fessional.wings.warlock.service.event.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import pro.fessional.wings.warlock.event.cache.TableChangeEvent;
import pro.fessional.wings.warlock.service.event.TableChangePublisher;

import java.util.Collection;

/**
 * @author trydofor
 * @since 2021-06-10
 */
@RequiredArgsConstructor
public class TableChangePublisherImpl implements TableChangePublisher {

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void publish(Object source, String table, Collection<Object> record) {
        eventPublisher.publishEvent(new TableChangeEvent(source, table, record));
    }
}
