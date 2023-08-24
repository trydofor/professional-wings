package pro.fessional.wings.silencer.spring.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.core.Ordered;
import pro.fessional.wings.silencer.runner.ApplicationInspectRunner;
import pro.fessional.wings.silencer.runner.ApplicationReadyEventRunner;
import pro.fessional.wings.silencer.runner.ApplicationRunnerOrdered;
import pro.fessional.wings.silencer.runner.ApplicationStartedEventRunner;

import java.util.Map;

/**
 * applicationRunner are executed before commandLineRunner in SpringApplication#callRunners
 *
 * @author trydofor
 * @link <a href="https://docs.spring.io/spring-boot/docs/3.0.3/reference/htmlsingle/#features.spring-application.application-events-and-listeners">Application Events and Listeners</a>
 * @since 2023-02-06
 */
@Configuration(proxyBeanMethods = false)
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
public class SilencerRunnerConfiguration {

    private static final Log log = LogFactory.getLog(SilencerRunnerConfiguration.class);

    @EventListener
    public void applicationStartedEventRunner(ApplicationStartedEvent startedEvent) {
        final ConfigurableApplicationContext context = startedEvent.getApplicationContext();
        final Map<String, ApplicationStartedEventRunner> beans = context.getBeansOfType(ApplicationStartedEventRunner.class);
        if (beans.isEmpty()) {
            log.info("===>>> Silencer applicationStartedEventRunner empty");
            return;
        }

        log.info("===>>> Silencer applicationStartedEventRunner size=" + beans.size());
        final ApplicationArguments args = context.getBean(ApplicationArguments.class);
        for (Map.Entry<String, ApplicationStartedEventRunner> en : beans.entrySet()) {
            log.info(">>> started=" + en.getKey());
            try {
                en.getValue().run(args);
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        log.info("===<<< Silencer applicationStartedEventRunner");
    }

    @EventListener
    public void applicationReadyEventRunner(ApplicationReadyEvent startedEvent) {
        final ConfigurableApplicationContext context = startedEvent.getApplicationContext();
        final Map<String, ApplicationReadyEventRunner> beans = context.getBeansOfType(ApplicationReadyEventRunner.class);
        if (beans.isEmpty()) {
            log.info("===>>> Silencer applicationReadyEventRunner empty");
            return;
        }

        log.info("===>>> Silencer applicationReadyEventRunner size=" + beans.size());
        final ApplicationArguments args = context.getBean(ApplicationArguments.class);
        for (Map.Entry<String, ApplicationReadyEventRunner> en : beans.entrySet()) {
            log.info(">>> ready=" + en.getKey());
            try {
                en.getValue().run(args);
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        log.info("===<<< Silencer applicationReadyEventRunner");
    }

    @Bean
    public ApplicationRunnerOrdered applicationInspectRunner(ApplicationContext context) {
        log.info("Silencer spring-runs applicationInspectRunner");
        return new ApplicationRunnerOrdered(Ordered.LOWEST_PRECEDENCE, args -> {
            final Map<String, ApplicationInspectRunner> beans = context.getBeansOfType(ApplicationInspectRunner.class);
            if (beans.isEmpty()) {
                log.info("===>>> Silencer applicationInspectRunner empty");
                return;
            }
            log.info("===>>> Silencer applicationInspectRunner size=" + beans.size());
            for (Map.Entry<String, ApplicationInspectRunner> en : beans.entrySet()) {
                final String name = en.getKey();
                log.info(">>> inspect=" + name);
                try {
                    en.getValue().run(args);
                }
                catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            log.info("===<<< Silencer applicationInspectRunner");
        });
    }
}
