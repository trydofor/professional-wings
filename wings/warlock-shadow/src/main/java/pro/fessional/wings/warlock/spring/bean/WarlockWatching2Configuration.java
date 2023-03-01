package pro.fessional.wings.warlock.spring.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.fessional.wings.spring.consts.OrderedWarlockConst;
import pro.fessional.wings.slardar.webmvc.SlowResponseInterceptor;
import pro.fessional.wings.warlock.spring.prop.WarlockEnabledProp;
import pro.fessional.wings.warlock.spring.prop.WarlockWatchingProp;


/**
 * @author trydofor
 * @since 2019-12-01
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = WarlockEnabledProp.Key$watching, havingValue = "true")
@AutoConfigureOrder(OrderedWarlockConst.Watching2Configuration)
public class WarlockWatching2Configuration {

    private final static Log log = LogFactory.getLog(WarlockWatching2Configuration.class);

    @Bean
    @ConditionalOnMissingBean(SlowResponseInterceptor.class)
    @ConditionalOnExpression("${" + WarlockWatchingProp.Key$controllerThreshold + ":-1} >=0")
    public SlowResponseInterceptor slowResponseInterceptor(WarlockWatchingProp prop) {
        final long ms = prop.getControllerThreshold();
        log.info("Warlock spring-bean slowResponseInterceptor, threshold=" + ms);
        SlowResponseInterceptor bean = new SlowResponseInterceptor();
        bean.setThresholdMillis(ms);
        return bean;
    }
}
