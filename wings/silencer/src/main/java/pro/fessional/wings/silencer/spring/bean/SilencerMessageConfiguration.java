package pro.fessional.wings.silencer.spring.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.HierarchicalMessageSource;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.fessional.wings.silencer.message.CombinableMessageSource;
import pro.fessional.wings.silencer.spring.prop.SilencerEnabledProp;

/**
 * @author trydofor
 * @link https://docs.spring.io/spring-boot/docs/2.6.6/reference/htmlsingle/#boot-features-internationalization
 * @see org.springframework.boot.autoconfigure.context.MessageSourceAutoConfiguration
 * @since 2019-06-24
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(MessageSource.class)
@ConditionalOnProperty(name = SilencerEnabledProp.Key$message, havingValue = "true")
public class SilencerMessageConfiguration {

    private static final Log log = LogFactory.getLog(SilencerMessageConfiguration.class);

    @Bean
    public CombinableMessageSource combinableMessageSource(MessageSource messageSource) {
        CombinableMessageSource combinable = new CombinableMessageSource();
        if (messageSource instanceof HierarchicalMessageSource) {
            HierarchicalMessageSource hierarchy = (HierarchicalMessageSource) messageSource;
            MessageSource parent = hierarchy.getParentMessageSource();
            if (parent != null) {
                log.info("set parent for CombinableMessageSource");
                combinable.setParentMessageSource(parent);
            }
            log.info("change messageSource to CombinableMessageSource");
            hierarchy.setParentMessageSource(combinable);
        } else {
            log.info("skip non HierarchicalMessageSource for CombinableMessageSource");
        }

        return combinable;
    }
}
