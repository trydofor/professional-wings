package pro.fessional.wings.slardar.spring.bean;

import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.ModelAndView;
import pro.fessional.wings.slardar.concur.impl.DebounceInterceptor;
import pro.fessional.wings.slardar.servlet.response.view.PlainTextView;
import pro.fessional.wings.slardar.spring.prop.SlardarDebounceProp;
import pro.fessional.wings.slardar.spring.prop.SlardarEnabledProp;

/**
 * @author trydofor
 * @since 2019-12-03
 */
@Configuration(proxyBeanMethods = false)
@RequiredArgsConstructor
@ConditionalOnProperty(name = SlardarEnabledProp.Key$debounce, havingValue = "true")
public class SlardarDebounceConfiguration {

    private static final Log log = LogFactory.getLog(SlardarDebounceConfiguration.class);

    @Bean
    @ConditionalOnMissingBean(DebounceInterceptor.class)
    public DebounceInterceptor debounceInterceptor(SlardarDebounceProp debounceProp) {
        final long cap = debounceProp.getCapacity();
        final int max = debounceProp.getMaxWait();
        log.info("SlardarWebmvc spring-bean debounceInterceptor, capacity=" + cap + ", max-wait=" + max);
        final ModelAndView mav = new ModelAndView();
        PlainTextView pv = new PlainTextView(debounceProp.getContentType(), debounceProp.getResponseBody());
        mav.setStatus(HttpStatus.valueOf(debounceProp.getHttpStatus()));
        mav.setView(pv);
        return new DebounceInterceptor(cap, max, mav);
    }
}
