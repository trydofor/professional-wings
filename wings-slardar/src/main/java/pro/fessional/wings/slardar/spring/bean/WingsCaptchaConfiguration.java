package pro.fessional.wings.slardar.spring.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.fessional.wings.slardar.servlet.WingsCaptchaFilter;
import pro.fessional.wings.slardar.servlet.WingsFilterOrder;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author trydofor
 * @since 2019-07-09
 */
@Configuration
@ConditionalOnProperty(name = "spring.wings.slardar.captcha.enabled", havingValue = "true")
public class WingsCaptchaConfiguration {

    private final Log logger = LogFactory.getLog(WingsCaptchaConfiguration.class);

    @Bean
    @ConfigurationProperties("wings.slardar.captcha")
    public WingsCaptchaFilter.Config wingsCaptchaFilterConfig() {
        return new WingsCaptchaFilter.Config();
    }

    @Bean
    public WingsCaptchaFilter wingsCaptchaFilter(WingsCaptchaFilter.Config config, ObjectProvider<WingsCaptchaFilter.CaptchaTrigger> triggers) {
        logger.info("Wings conf Captcha filter");
        List<WingsCaptchaFilter.CaptchaTrigger> trgs = triggers.orderedStream().collect(Collectors.toList());
        WingsCaptchaFilter filter = new WingsCaptchaFilter(config, trgs);
        filter.setOrder(WingsFilterOrder.CAPTCHA);
        return filter;
    }

}
