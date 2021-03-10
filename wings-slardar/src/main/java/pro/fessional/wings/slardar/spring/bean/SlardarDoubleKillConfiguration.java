package pro.fessional.wings.slardar.spring.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ModelAndView;
import pro.fessional.wings.slardar.concur.DoubleKillAround;
import pro.fessional.wings.slardar.concur.DoubleKillExceptionResolver;
import pro.fessional.wings.slardar.servlet.response.view.PlainTextView;
import pro.fessional.wings.slardar.spring.prop.SlardarEnabledProp;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * @author trydofor
 * @since 2019-12-03
 */
@Configuration
@ConditionalOnProperty(name = SlardarEnabledProp.Key$doubleKill, havingValue = "true")
public class SlardarDoubleKillConfiguration {

    private static final Log logger = LogFactory.getLog(SlardarDoubleKillConfiguration.class);

    @Bean
    public DoubleKillAround doubleKillAround() {
        logger.info("Wings conf doubleKillAround");
        return new DoubleKillAround();
    }

    @Bean
    @ConditionalOnMissingBean
    public DoubleKillExceptionResolver doubleKillExceptionResolver() {
        logger.info("Wings conf doubleKillAround");
        ModelAndView mav = new ModelAndView();
        final String json = "{\"success\":false,\"message\":\"Request Too Busy, Take A Coffee\"}";
        PlainTextView pv = new PlainTextView(APPLICATION_JSON_VALUE, json);
        mav.setView(pv);
        return new DoubleKillExceptionResolver(mav);
    }
}
