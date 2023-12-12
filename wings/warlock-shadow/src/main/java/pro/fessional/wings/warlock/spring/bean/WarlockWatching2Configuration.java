package pro.fessional.wings.warlock.spring.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;
import pro.fessional.wings.slardar.webmvc.SlowResponseInterceptor;
import pro.fessional.wings.warlock.spring.prop.WarlockWatchingProp;


/**
 * @author trydofor
 * @since 2019-12-01
 */
@Configuration(proxyBeanMethods = false)
@ConditionalWingsEnabled
public class WarlockWatching2Configuration {

    private final static Log log = LogFactory.getLog(WarlockWatching2Configuration.class);

    @Bean
    @ConditionalWingsEnabled
    @ConditionalOnExpression("${" + WarlockWatchingProp.Key$controllerThreshold + ":-1} >=0")
    public SlowResponseInterceptor slowResponseInterceptor(WarlockWatchingProp prop) {
        final long ms = prop.getControllerThreshold();
        log.info("Warlock spring-bean slowResponseInterceptor, threshold=" + ms);
        SlowResponseInterceptor bean = new SlowResponseInterceptor();
        bean.setThresholdMillis(ms);
        return bean;
    }
}
