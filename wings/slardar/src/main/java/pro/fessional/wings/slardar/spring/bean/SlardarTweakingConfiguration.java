package pro.fessional.wings.slardar.spring.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.fessional.wings.slardar.event.TweakEventListener;
import pro.fessional.wings.slardar.httprest.okhttp.OkHttpTweakLogInterceptor;
import pro.fessional.wings.slardar.spring.prop.SlardarEnabledProp;

/**
 * @author trydofor
 * @since 2019-12-03
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = SlardarEnabledProp.Key$tweaking, havingValue = "true")
public class SlardarTweakingConfiguration {

    private static final Log log = LogFactory.getLog(SlardarTweakingConfiguration.class);

    @Bean
    public TweakEventListener tweakEventListener() {
        log.info("Slardar spring-bean tweakEventListener");
        return new TweakEventListener();
    }

    @Bean
    public OkHttpTweakLogInterceptor okHttpTweakLogInterceptor() {
        log.info("Slardar spring-bean okHttpTweakLogInterceptor");
        return new OkHttpTweakLogInterceptor();
    }
}
