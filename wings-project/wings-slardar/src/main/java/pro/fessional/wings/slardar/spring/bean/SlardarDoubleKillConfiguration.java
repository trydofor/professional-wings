package pro.fessional.wings.slardar.spring.bean;

import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.HandlerExceptionResolver;
import pro.fessional.wings.slardar.concur.impl.DoubleKillAround;
import pro.fessional.wings.slardar.concur.impl.DoubleKillExceptionResolver;
import pro.fessional.wings.slardar.spring.prop.SlardarDoubleKillProp;
import pro.fessional.wings.slardar.spring.prop.SlardarEnabledProp;

/**
 * @author trydofor
 * @since 2019-12-03
 */
@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(name = SlardarEnabledProp.Key$doubleKill, havingValue = "true")
public class SlardarDoubleKillConfiguration {

    private static final Log logger = LogFactory.getLog(SlardarDoubleKillConfiguration.class);
    private final SlardarDoubleKillProp doubleKillProp;

    @Bean
    public DoubleKillAround doubleKillAround() {
        logger.info("Wings conf doubleKillAround");
        return new DoubleKillAround();
    }

    @Bean
    @ConditionalOnMissingBean(name = "doubleKillExceptionResolver")
    public HandlerExceptionResolver doubleKillExceptionResolver() {
        logger.info("Wings conf doubleKillExceptionResolver");
        final DoubleKillExceptionResolver bean = new DoubleKillExceptionResolver(
                doubleKillProp.getHttpStatus(),
                doubleKillProp.getContentType(),
                doubleKillProp.getResponseBody());
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE + 1000);
        return bean;
    }
}
