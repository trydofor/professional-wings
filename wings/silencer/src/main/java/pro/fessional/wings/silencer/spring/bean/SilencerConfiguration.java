package pro.fessional.wings.silencer.spring.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import pro.fessional.wings.silencer.message.MessageSourceHelper;
import pro.fessional.wings.silencer.runner.ApplicationInspectRunner;
import pro.fessional.wings.silencer.runner.ApplicationRunnerOrdered;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;

import java.util.Map;

/**
 * @author trydofor
 * @since 2019-06-24
 */
@Configuration(proxyBeanMethods = false)
@ConditionalWingsEnabled
public class SilencerConfiguration {

    private static final Log log = LogFactory.getLog(SilencerConfiguration.class);

    /**
     * @link <a href="https://docs.spring.io/spring-boot/docs/3.0.3/reference/htmlsingle/#features.internationalization">Internationalization</a>
     * @see org.springframework.boot.autoconfigure.context.MessageSourceAutoConfiguration
     */

    @Bean
    @ConditionalWingsEnabled
    public MessageSourceHelper messageSourceHelper(MessageSource messageSource) {
        var bean = new MessageSourceHelper(messageSource) {};
        if (MessageSourceHelper.hasCombine) {
            log.info("Silencer spring-auto MessageSourceHelper parent to CombinableMessageSource");
        }
        else {
            log.info("Silencer spring-auto MessageSourceHelper skip CombinableMessageSource");
        }
        return bean;
    }

    /**
     * applicationRunner are executed before commandLineRunner in SpringApplication#callRunners
     *
     * @link <a href="https://docs.spring.io/spring-boot/docs/3.0.3/reference/htmlsingle/#features.spring-application.application-events-and-listeners">Application Events and Listeners</a>
     */
    @Bean
    @ConditionalWingsEnabled
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
