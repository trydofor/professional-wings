package pro.fessional.wings.slardar.spring.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;
import pro.fessional.wings.silencer.support.PropHelper;
import pro.fessional.wings.slardar.event.tweak.TweakEventListener;
import pro.fessional.wings.slardar.httprest.okhttp.OkHttpTweakLogInterceptor;
import pro.fessional.wings.slardar.spring.prop.SlardarOkhttpProp;

import java.util.Collection;

/**
 * @author trydofor
 * @since 2019-12-03
 */
@Configuration(proxyBeanMethods = false)
@ConditionalWingsEnabled
public class SlardarTweakConfiguration {

    private static final Log log = LogFactory.getLog(SlardarTweakConfiguration.class);

    @Bean
    @ConditionalWingsEnabled
    public OkHttpTweakLogInterceptor okhttpTweakLogInterceptor(SlardarOkhttpProp prop) {
        log.info("Slardar spring-bean okhttpTweakLogInterceptor");
        Collection<String> nop = PropHelper.onlyValid(prop.getInterceptNop()).values();
        return new OkHttpTweakLogInterceptor(nop);
    }

    @Bean
    @ConditionalWingsEnabled
    public TweakEventListener tweakEventListener() {
        log.info("Slardar spring-bean tweakEventListener");
        return new TweakEventListener();
    }
}
