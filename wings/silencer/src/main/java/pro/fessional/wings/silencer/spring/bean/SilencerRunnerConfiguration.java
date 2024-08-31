package pro.fessional.wings.silencer.spring.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import pro.fessional.wings.silencer.runner.ApplicationReadyEventRunner;
import pro.fessional.wings.silencer.runner.ApplicationStartedEventRunner;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;

import java.util.Map;

/**
 * applicationRunner are executed before commandLineRunner in SpringApplication#callRunners
 *
 * @author trydofor
 * @link <a href="https://docs.spring.io/spring-boot/docs/3.0.3/reference/htmlsingle/#features.spring-application.application-events-and-listeners">Application Events and Listeners</a>
 * @since 2023-02-06
 */
@Configuration(proxyBeanMethods = false)
@ConditionalWingsEnabled
public class SilencerRunnerConfiguration {

    private static final Log log = LogFactory.getLog(SilencerRunnerConfiguration.class);

    @Component
    @ConditionalWingsEnabled
    public static class ReadyEvent implements ApplicationListener<ApplicationReadyEvent> {
        @Override
        public void onApplicationEvent(@NotNull ApplicationReadyEvent event) {
            final ConfigurableApplicationContext context = event.getApplicationContext();
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
    }

    @Component
    @ConditionalWingsEnabled
    public static class StartedEvent implements ApplicationListener<ApplicationStartedEvent> {
        @Override
        public void onApplicationEvent(@NotNull ApplicationStartedEvent event) {
            final ConfigurableApplicationContext context = event.getApplicationContext();
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
    }
}
