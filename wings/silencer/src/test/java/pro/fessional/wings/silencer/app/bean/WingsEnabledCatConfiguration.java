package pro.fessional.wings.silencer.app.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.SpringApplicationEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author trydofor
 * @since 2023-11-17
 */
@ConditionalWingsEnabled
@Configuration(proxyBeanMethods = false)
public class WingsEnabledCatConfiguration {

    private static final Log log = LogFactory.getLog(WingsEnabledCatConfiguration.class);
    public static final ConcurrentHashMap<Object, Boolean> autowire = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<Object, Integer> listener = new ConcurrentHashMap<>();

    public WingsEnabledCatConfiguration() {
        log.info(">>> init " + this);
        autowire.put(this, false);
        listener.put(this, 0);
    }

    @Bean
    @ConditionalWingsEnabled(key = "catBean")
    public CatBean catBean() {
        return new CatBean();
    }

    @EventListener
    @ConditionalWingsEnabled // no effect
    public void listener(SpringApplicationEvent ignore) {
        log.info(">>> listener " + this);
        listener.compute(this, (k, v) -> v == null ? 1 : v + 1);
    }

    @Autowired
    @ConditionalWingsEnabled // no effect
    public void autowire(ApplicationContext ignore) {
        log.info(">>> autowire " + this);
        autowire.put(this, true);
    }

    @Bean
    @ConditionalWingsEnabled(and = InnerCatConfiguration.class)
    public AndBean andBean() {
        return new AndBean();
    }

    @Bean
    @ConditionalWingsEnabled(not = InnerCatConfiguration.class)
    public NotBean notBean() {
        return new NotBean();
    }

    @Bean
    @ConditionalWingsEnabled(abs = "wings.cat.key-bean")
    public KeyBean keyBean() {
        return new KeyBean();
    }


    @ConditionalWingsEnabled
    @Configuration(proxyBeanMethods = false)
    public static class InnerCatConfiguration {

        public InnerCatConfiguration() {
            log.info(">>> init " + this);
            autowire.put(this, false);
            listener.put(this, 0);
        }

        @EventListener
        @ConditionalWingsEnabled // no effect
        public void listener(SpringApplicationEvent ignore) {
            log.info(">>> listener " + this);
            listener.compute(this, (k, v) -> v == null ? 1 : v + 1);
        }

        @Autowired
        @ConditionalWingsEnabled // no effect
        public void autowire(ApplicationContext ignore) {
            log.info(">>> autowire " + this);
            autowire.put(this, true);
        }

        @Bean
        @ConditionalWingsEnabled
        public InnerCatBean innerCatBean() {
            return new InnerCatBean();
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalWingsEnabled
    public static class ComponentScan {
        public ComponentScan() {
            log.info("Silencer spring-scan Component");
        }
    }

    @Configuration(proxyBeanMethods = false)
    public static class InnerCatConfigDefault {
    }

    public static class CatBean {
    }

    public static class InnerCatBean {
    }

    public static class AndBean {
    }

    public static class NotBean {
    }

    public static class KeyBean {
    }
}
