package pro.fessional.wings.slardar.spring.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.fessional.wings.slardar.servlet.WingsCaptchaFilter;
import pro.fessional.wings.slardar.servlet.WingsFilterOrder;

/**
 * @author trydofor
 * @since 2019-07-09
 */
@Configuration
@ConditionalOnProperty(prefix = "spring.wings.slardar.captcha", name = "enabled", havingValue = "true")
public class WingsCaptchaConfiguration {

    private final Log logger = LogFactory.getLog(WingsCaptchaConfiguration.class);

    @Bean
    @ConfigurationProperties("wings.slardar.captcha")
    public WingsCaptchaFilter.Config wingsCaptchaFilterConfig() {
        return new WingsCaptchaFilter.Config();
    }

    @Bean
    public WingsCaptchaFilter wingsCaptchaFilter(WingsCaptchaFilter.Config config) {
        logger.info("Wings conf Captcha filter");
        WingsCaptchaFilter filter = new WingsCaptchaFilter(config);
        filter.setOrder(WingsFilterOrder.CAPTCHA);
        return filter;
    }

}
