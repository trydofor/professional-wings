package pro.fessional.wings.silencer.app.service.impl;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import pro.fessional.wings.silencer.app.TestSilencerCurseApplication;

/**
 * @author trydofor
 * @since 2024-08-02
 */
@Service
public class TestSpringOrderService  implements InitializingBean, DisposableBean {

    @EventListener
    public void testApplicationReadyEvent(ContextClosedEvent event) {
        TestSilencerCurseApplication.log("@EventListener: " + event.getClass().getSimpleName());
    }

    @PostConstruct
    public void PostConstruct() {
        TestSilencerCurseApplication.log("@PostConstruct: TestSpringOrderService");
    }

    @Override
    public void afterPropertiesSet() {
        TestSilencerCurseApplication.log("@Override: InitializingBean TestSpringOrderService");
    }

    @PreDestroy
    public void preDestroy() {
        TestSilencerCurseApplication.log("@PreDestroy: TestSpringOrderService");
    }

    @Override
    public void destroy() {
        TestSilencerCurseApplication.log("@Override: DisposableBean TestSpringOrderService");
    }
}
