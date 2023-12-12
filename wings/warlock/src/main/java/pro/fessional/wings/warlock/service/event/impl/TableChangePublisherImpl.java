package pro.fessional.wings.warlock.service.event.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationEventPublisher;
import pro.fessional.wings.warlock.event.cache.TableChangeEvent;
import pro.fessional.wings.warlock.service.event.TableChangePublisher;

/**
 * @author trydofor
 * @since 2021-06-10
 */
@RequiredArgsConstructor
@Slf4j
public class TableChangePublisherImpl implements TableChangePublisher {

    private static final String EVENT_SRC = TableChangePublisherImpl.class.getName();

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void publish(@NotNull TableChangeEvent event) {
        if (event.hasSource(EVENT_SRC)) {
            log.debug("skip published event, table={}, change={}", event.getTable(), event.getChange());
        }
        else {
            log.debug("publish event={}", event);
            event.addSource(EVENT_SRC);
            eventPublisher.publishEvent(event);
        }
    }
}
