package pro.fessional.wings.silencer.spring.bean;

import jakarta.annotation.PostConstruct;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.event.SpringApplicationEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import pro.fessional.wings.silencer.other.CollectionInjectTest;
import pro.fessional.wings.silencer.spring.prop.SilencerEnabledProp;

/**
 * ApplicationEnvironmentPreparedEvent(spring.factories)
 * ApplicationContextInitializedEvent(spring.factories)
 * ApplicationPreparedEvent(spring.factories)
 * constructor
 * testAutowired1
 * postConstruct1
 * postConstruct2
 * afterPropertiesSet
 * testBean1
 * testBean2
 * ContextRefreshedEvent(spring.factories)
 * ApplicationStartedEvent
 * CommandLineRunner1
 * CommandLineRunner2
 * ApplicationReadyEvent
 * ContextClosedEvent(spring.factories)
 *
 * @author trydofor
 * @since 2022-11-02
 */
@Configuration(proxyBeanMethods = false)
public class SpringOrderConfiguration implements InitializingBean {
    private static final Log log = LogFactory.getLog(SpringOrderConfiguration.class);

    public SpringOrderConfiguration(SilencerEnabledProp prop) {
        log.info(">>>>> constructor 可自动注入参数 AutoLog=" + prop.isAutoLog());
    }


    @Bean
    public CollectionInjectTest.Dto dto2() {
        return new CollectionInjectTest.Dto(2);
    }

    @Bean
    public CommandLineRunner testBean1(SilencerEnabledProp prop) {
        log.info(">>>>> testBean1 可自动注入参数 AutoLog=" + prop.isAutoLog());
        return ignored -> log.info(">>>>> CommandLineRunner1 " + prop.isAutoLog());
    }

    @PostConstruct
    public void postConstruct1() {
        log.info(">>>>> postConstruct1 不可注入参数");
    }

    @Autowired
    public void testAutowired1(SilencerEnabledProp prop) {
        log.info(">>>>> testAutowired1 可自动注入参数 AutoLog=" + prop.isAutoLog());
    }

    @PostConstruct
    public void postConstruct2() {
        log.info(">>>>> postConstruct2");
    }

    @Bean
    public CommandLineRunner testBean2(SilencerEnabledProp prop) {
        log.info(">>>>> testBean2 AutoLog=" + prop.isAutoLog());
        return ignored -> log.info(">>>>> CommandLineRunner2 AutoLog=" + prop.isAutoLog());
    }

    @EventListener
    public void testApplicationReadyEvent(SpringApplicationEvent event) {
        log.info(">>>>> " + event.getClass().getSimpleName() + " timestamp=" + event.getTimestamp());
    }

    @Override
    public void afterPropertiesSet() {
        log.info(">>>>> afterPropertiesSet");
    }
}
