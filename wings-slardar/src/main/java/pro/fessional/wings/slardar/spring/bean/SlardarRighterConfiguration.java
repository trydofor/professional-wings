package pro.fessional.wings.slardar.spring.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.fessional.wings.slardar.context.RighterInterceptor;
import pro.fessional.wings.slardar.spring.prop.SlardarEnabledProp;
import pro.fessional.wings.slardar.spring.prop.SlardarRighterProp;

/**
 * @author trydofor
 * @since 2019-06-29
 */
@Configuration
@ConditionalOnProperty(name = SlardarEnabledProp.Key$righter, havingValue = "true")
public class SlardarRighterConfiguration {

    private final static Log logger = LogFactory.getLog(SlardarRighterConfiguration.class);

    @Bean
    public RighterInterceptor righterInterceptor(SlardarRighterProp slardarRighterProp) {
        logger.info("Wings conf righterInterceptor");
        return new RighterInterceptor(slardarRighterProp);
    }
}
