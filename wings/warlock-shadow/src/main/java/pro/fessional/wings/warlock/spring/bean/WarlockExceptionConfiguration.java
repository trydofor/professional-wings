package pro.fessional.wings.warlock.spring.bean;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerExceptionResolver;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;
import pro.fessional.wings.warlock.errorhandle.DefaultExceptionResolver;
import pro.fessional.wings.warlock.spring.prop.WarlockErrorProp;


/**
 * @author trydofor
 * @since 2019-12-01
 */
@Configuration(proxyBeanMethods = false)
@ConditionalWingsEnabled
public class WarlockExceptionConfiguration {


    private final static Log log = LogFactory.getLog(WarlockExceptionConfiguration.class);

    @Bean
    @ConditionalWingsEnabled
    public HandlerExceptionResolver defaultExceptionResolver(WarlockErrorProp prop, MessageSource messageSource, ObjectMapper objectMapper) {
        log.info("WarlockShadow spring-bean defaultExceptionResolver");
        return new DefaultExceptionResolver(prop.getDefaultException(), messageSource, objectMapper);
    }
}
