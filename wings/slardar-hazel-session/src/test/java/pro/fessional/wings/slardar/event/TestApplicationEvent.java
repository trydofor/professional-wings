package pro.fessional.wings.slardar.event;

import org.springframework.context.ApplicationEvent;

/**
 * @author trydofor
 * @since 2021-06-09
 */

public class TestApplicationEvent extends ApplicationEvent {
    public TestApplicationEvent(Object source) {
        super(source);
    }
}
