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

/**
 * @author trydofor
 * @link https://docs.spring.io/spring-boot/docs/2.4.0/reference/htmlsingle/#boot-features-internationalization
 * @see org.springframework.boot.autoconfigure.context.MessageSourceAutoConfiguration
 * @since 2019-06-24
 */
@Configuration
@ConditionalOnClass(MessageSource.class)
@ConditionalOnProperty(name = "spring.wings.message.enabled", havingValue = "true")
public class WingsMessageConfiguration {

    private static final Log logger = LogFactory.getLog(WingsMessageConfiguration.class);

    @Bean
    public CombinableMessageSource combinableMessageSource(MessageSource messageSource) {
        CombinableMessageSource combinable = new CombinableMessageSource();
        if (messageSource instanceof HierarchicalMessageSource) {
            HierarchicalMessageSource hierarchy = (HierarchicalMessageSource) messageSource;
            MessageSource parent = hierarchy.getParentMessageSource();
            if (parent != null) {
                logger.info("set parent for CombinableMessageSource");
                combinable.setParentMessageSource(parent);
            }
            logger.info("change messageSource to CombinableMessageSource");
            hierarchy.setParentMessageSource(combinable);
        } else {
            logger.info("skip non HierarchicalMessageSource for CombinableMessageSource");
        }

        return combinable;
    }
}
