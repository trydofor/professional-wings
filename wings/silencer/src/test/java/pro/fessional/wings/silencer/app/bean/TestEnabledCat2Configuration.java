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
public class TestEnabledCat2Configuration {

    private static final Log log = LogFactory.getLog(TestEnabledCat2Configuration.class);
    public static final ConcurrentHashMap<Object, Boolean> autowire = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<Object, Integer> listener = new ConcurrentHashMap<>();

    public TestEnabledCat2Configuration() {
        log.info(">>> init " + this);
        autowire.put(this, false);
        listener.put(this, 0);
    }

    @Bean
    @ConditionalWingsEnabled(key = "cat2Bean")
    public Cat2Bean cat2Bean() {
        return new Cat2Bean();
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
    @ConditionalWingsEnabled(and = InnerCat2Configuration.class)
    public And2Bean and2Bean() {
        return new And2Bean();
    }

    @Bean
    @ConditionalWingsEnabled(not = InnerCat2Configuration.class)
    public Not2Bean not2Bean() {
        return new Not2Bean();
    }

    @Bean
    @ConditionalWingsEnabled(abs = "wings.cat.key-bean")
    public Key2Bean key2Bean() {
        return new Key2Bean();
    }


    @ConditionalWingsEnabled
    @Configuration(proxyBeanMethods = false)
    public static class InnerCat2Configuration {

        public InnerCat2Configuration() {
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
        public InnerCat2Bean innerCat2Bean() {
            return new InnerCat2Bean();
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalWingsEnabled
    public static class Component2Scan {
        public Component2Scan() {
            log.info("Silencer spring-scan Component");
        }
    }

    @Configuration(proxyBeanMethods = false)
    public static class InnerCat2ConfigDefault {
    }

    public static class Cat2Bean {
    }

    public static class InnerCat2Bean {
    }

    public static class And2Bean {
    }

    public static class Not2Bean {
    }

    public static class Key2Bean {
    }
}
