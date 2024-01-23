package pro.fessional.wings.silencer.app.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.SpringApplicationEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import pro.fessional.wings.silencer.spring.boot.WingsEnabledCondition;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author trydofor
 * @since 2023-11-17
 */
@Conditional(WingsEnabledCondition.class)
@Configuration(proxyBeanMethods = false)
public class TestEnabledDogConfiguration {

    private static final Log log = LogFactory.getLog(TestEnabledDogConfiguration.class);
    public static final ConcurrentHashMap<Object, Boolean> autowire = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<Object, Integer> listener = new ConcurrentHashMap<>();

    public TestEnabledDogConfiguration() {
        log.info(">>> init " + this);
        autowire.put(this, false);
        listener.put(this, 0);
    }

    @Bean
    @Conditional(WingsEnabledCondition.class)
    public DogBean dogBean() {
        return new DogBean();
    }

    @EventListener
    @Conditional(WingsEnabledCondition.class) // no effect
    public void listener(SpringApplicationEvent ignore) {
        log.info(">>> listener " + this);
        listener.compute(this, (k, v) -> v == null ? 1 : v + 1);
    }

    @Autowired
    @Conditional(WingsEnabledCondition.class) // no effect
    public void autowire(ApplicationContext ignore) {
        log.info(">>> autowire " + this);
        autowire.put(this, true);
    }

    @Conditional(WingsEnabledCondition.class)
    @Configuration(proxyBeanMethods = false)
    public static class InnerDogConfiguration {

        public InnerDogConfiguration() {
            log.info(">>> init " + this);
            autowire.put(this, false);
            listener.put(this, 0);
        }

        @EventListener
        @Conditional(WingsEnabledCondition.class) // no effect
        public void listener(SpringApplicationEvent ignore) {
            log.info(">>> listener " + this);
            listener.compute(this, (k, v) -> v == null ? 1 : v + 1);
        }

        @Autowired
        @Conditional(WingsEnabledCondition.class) // no effect
        public void autowire(ApplicationContext ignore) {
            log.info(">>> autowire " + this);
            autowire.put(this, true);
        }

        @Bean
        @Conditional(WingsEnabledCondition.class)
        public InnerDogBean innerDogBean() {
            return new InnerDogBean();
        }
    }

    public static class DogBean {
    }

    public static class InnerDogBean {
    }
}
