package pro.fessional.wings.silencer.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import pro.fessional.wings.silencer.app.conf.TestMergingProp;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * #01,  244ms, (spring.factories): ApplicationStartingEvent
 * #02, 2209ms, (spring.factories): ApplicationEnvironmentPreparedEvent
 * #03, 2758ms, (spring.factories): ApplicationContextInitializedEvent
 * #04, 2809ms, (spring.factories): ApplicationPreparedEvent
 * #05, 3365ms, @PostConstruct: TestSpringOrderService
 * #06, 3365ms, @Override: InitializingBean TestSpringOrderService
 * #07, 3381ms, (constructor): can inject para, autoconf=true
 * #08, 3384ms, @Autowired: testAutowired1 can inject para, autoconf=true
 * #09, 3384ms, @PostConstruct: postConstruct1
 * #10, 3384ms, @PostConstruct: postConstruct2
 * #11, 3385ms, @Override: InitializingBean TestSpringOrderConfiguration
 * #12, 3385ms, @Bean: testBean1 can inject para, autoconf=true
 * #13, 3386ms, @Bean: testBean2 can inject para, autoconf=true
 * #14, 3657ms, (spring.factories): ContextRefreshedEvent
 * #15, 3662ms, (spring.factories): ApplicationStartedEvent
 * #16, 3664ms, @EventListener: ApplicationStartedEvent
 * #17, 3665ms, (spring.factories): AvailabilityChangeEvent
 * #18, 3669ms, CommandLineRunner: CommandLineRunner1
 * #19, 3673ms, CommandLineRunner: CommandLineRunner2
 * #20, 3674ms, (spring.factories): ApplicationReadyEvent
 * #21, 3675ms, @EventListener: ApplicationReadyEvent
 * #22, 3676ms, (spring.factories): AvailabilityChangeEvent
 * #23, 3678ms, Jvm ShutdownHook
 * #24, 3679ms, (spring.factories): ContextClosedEvent
 * #25, 3679ms, @EventListener: ContextClosedEvent
 * #26, 3682ms, @PreDestroy: TestSpringOrderConfiguration
 * #27, 3682ms, @Override: DisposableBean TestSpringOrderConfiguration
 * #28, 3682ms, @PreDestroy: TestSpringOrderService
 * #29, 3682ms, @Override: DisposableBean TestSpringOrderService
 *
 * @author trydofor
 * @since 2019-07-20
 */
@SpringBootApplication
@EnableConfigurationProperties(TestMergingProp.class)
public class TestSilencerCurseApplication {

    private static final AtomicInteger seq = new AtomicInteger(0);
    private static final long start = System.currentTimeMillis();

    public static void log(String msg) {
        System.err.printf(">>>>> #%02d, %4dms, %s\n", seq.incrementAndGet(), (System.currentTimeMillis() - start), msg);
    }

    public interface InnerFace {
    }

    @Bean
    public InnerFace innerFace() {
        return new InnerFace() {};
    }

    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> log("Jvm ShutdownHook")));
        SpringApplication.run(TestSilencerCurseApplication.class, args);
    }
}
