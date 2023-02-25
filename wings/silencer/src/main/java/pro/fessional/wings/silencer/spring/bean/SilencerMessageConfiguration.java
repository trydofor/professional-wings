package pro.fessional.wings.silencer.spring.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.HierarchicalMessageSource;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import pro.fessional.wings.silencer.message.CombinableMessageSource;
import pro.fessional.wings.silencer.spring.prop.SilencerEnabledProp;

/**
 * @author trydofor
 * @link <a href="https://docs.spring.io/spring-boot/docs/3.0.3/reference/htmlsingle/#features.internationalization">Internationalization</a>
 * @see org.springframework.boot.autoconfigure.context.MessageSourceAutoConfiguration
 * @since 2019-06-24
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(MessageSource.class)
@ConditionalOnProperty(name = SilencerEnabledProp.Key$message, havingValue = "true")
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
public class SilencerMessageConfiguration {

    private static final Log log = LogFactory.getLog(SilencerMessageConfiguration.class);

    @Bean
    public CombinableMessageSource combinableMessageSource(MessageSource messageSource) {
        log.info("Silencer spring-bean combinableMessageSource");
        CombinableMessageSource combinable = new CombinableMessageSource();
        if (messageSource instanceof HierarchicalMessageSource hierarchy) {
            MessageSource parent = hierarchy.getParentMessageSource();
            if (parent != null) {
                log.info("Silencer set parent for CombinableMessageSource");
                combinable.setParentMessageSource(parent);
            }
            log.info("Silencer change messageSource's parent to CombinableMessageSource");
            hierarchy.setParentMessageSource(combinable);
        }
        else {
            log.info("Silencer skip non HierarchicalMessageSource for CombinableMessageSource");
        }

        return combinable;
    }
}
