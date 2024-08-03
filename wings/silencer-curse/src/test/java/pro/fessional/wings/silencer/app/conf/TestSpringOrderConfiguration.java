package pro.fessional.wings.silencer.app.conf;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.event.SpringApplicationEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import pro.fessional.wings.silencer.app.TestSilencerCurseApplication;
import pro.fessional.wings.silencer.other.CollectionInjectTest;
import pro.fessional.wings.silencer.spring.prop.SilencerEnabledProp;

/**
 * @author trydofor
 * @since 2022-11-02
 */
@Configuration(proxyBeanMethods = false)
public class TestSpringOrderConfiguration implements InitializingBean, DisposableBean {

    public TestSpringOrderConfiguration(SilencerEnabledProp prop) {
        TestSilencerCurseApplication.log("(constructor): can inject para, autoconf=" + prop.isAutoconf());
    }

    @Bean
    public CollectionInjectTest.Dto dto2() {
        return new CollectionInjectTest.Dto(2);
    }

    @Bean
    public CommandLineRunner testBean1(SilencerEnabledProp prop) {
        TestSilencerCurseApplication.log("@Bean: testBean1 can inject para, autoconf=" + prop.isAutoconf());
        return ignored -> TestSilencerCurseApplication.log("CommandLineRunner: CommandLineRunner1 ");
    }

    @PostConstruct
    public void postConstruct1() {
        TestSilencerCurseApplication.log("@PostConstruct: postConstruct1");
    }

    @Autowired
    public void testAutowired1(SilencerEnabledProp prop) {
        TestSilencerCurseApplication.log("@Autowired: testAutowired1 can inject para, autoconf=" + prop.isAutoconf());
    }

    @PostConstruct
    public void postConstruct2() {
        TestSilencerCurseApplication.log("@PostConstruct: postConstruct2");
    }

    @Bean
    public CommandLineRunner testBean2(SilencerEnabledProp prop) {
        TestSilencerCurseApplication.log("@Bean: testBean2 can inject para, autoconf=" + prop.isAutoconf());
        return ignored -> TestSilencerCurseApplication.log("CommandLineRunner: CommandLineRunner2");
    }

    @EventListener
    public void testApplicationReadyEvent(SpringApplicationEvent event) {
        TestSilencerCurseApplication.log("@EventListener: " + event.getClass().getSimpleName());
    }

    @Override
    public void afterPropertiesSet() {
        TestSilencerCurseApplication.log("@Override: InitializingBean TestSpringOrderConfiguration");
    }

    @PreDestroy
    public void preDestroy() {
        TestSilencerCurseApplication.log("@PreDestroy: TestSpringOrderConfiguration");
    }

    @Override
    public void destroy() {
        TestSilencerCurseApplication.log("@Override: DisposableBean TestSpringOrderConfiguration");
    }
}
