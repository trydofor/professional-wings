package pro.fessional.wings.slardar.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import pro.fessional.mirana.pain.DebugException;
import pro.fessional.wings.slardar.event.TestApplicationEvent;
import pro.fessional.wings.slardar.event.TestEvent;

/**
 * @author trydofor
 * @since 2021-06-09
 */
@Service
@Slf4j
public class TestEventListener {

    @EventListener
    public void syncListen(TestEvent event) {
        log.info("sync-listen:{}:{}", Thread.currentThread().getName(), event.getMessage());
        throw new DebugException("sync-listen");
    }

    @Async
    @EventListener
    public void asyncListen(TestEvent event) {
        log.info("async-listen:{}:{}", Thread.currentThread().getName(), event.getMessage());
        throw new DebugException("async-listen");
    }

    @EventListener
    public void multicastListen(TestApplicationEvent event) {
        log.info("multicast-listen:{}:{}", Thread.currentThread().getName(), event.getSource());
        throw new DebugException("multicast-listen");
    }

}
