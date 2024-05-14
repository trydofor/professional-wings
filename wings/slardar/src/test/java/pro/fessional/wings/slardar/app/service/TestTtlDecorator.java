package pro.fessional.wings.slardar.app.service;

import org.springframework.core.task.TaskDecorator;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author trydofor
 * @since 2024-05-14
 */
@Service
public class TestTtlDecorator implements TaskDecorator {

    public static final AtomicInteger Count = new AtomicInteger(0);

    @Override
    public Runnable decorate(Runnable runnable) {
        Count.incrementAndGet();
        return runnable;
    }
}
