package pro.fessional.wings.warlock.spring.bean;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerExceptionResolver;
import pro.fessional.wings.warlock.errorhandle.DefaultExceptionResolver;
import pro.fessional.wings.warlock.spring.prop.WarlockApiAuthProp;
import pro.fessional.wings.warlock.spring.prop.WarlockEnabledProp;
import pro.fessional.wings.warlock.spring.prop.WarlockErrorProp;
import pro.fessional.wings.warlock.spring.prop.WarlockUrlmapProp;


/**
 * @author trydofor
 * @since 2019-12-01
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({WarlockApiAuthProp.class, WarlockUrlmapProp.class, WarlockErrorProp.class})
public class WarlockExceptionConfiguration {

    public static final String defaultExceptionResolver = "defaultExceptionResolver";

    private final static Log log = LogFactory.getLog(WarlockExceptionConfiguration.class);

    @Bean(name = defaultExceptionResolver)
    @ConditionalOnMissingBean(name = defaultExceptionResolver)
    @ConditionalOnProperty(name = WarlockEnabledProp.Key$defaultExceptionHandler, havingValue = "true")
    public HandlerExceptionResolver defaultExceptionResolver(WarlockErrorProp prop, MessageSource messageSource, ObjectMapper objectMapper) {
        log.info("WarlockShadow spring-bean " + defaultExceptionResolver);
        return new DefaultExceptionResolver(prop.getDefaultException(), messageSource, objectMapper);
    }
}
