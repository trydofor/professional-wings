package pro.fessional.wings.slardar.spring.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import pro.fessional.wings.slardar.servlet.resolver.WingsLocaleResolver;
import pro.fessional.wings.slardar.spring.prop.SlardarEnabledProp;
import pro.fessional.wings.slardar.spring.prop.SlardarLocaleProp;
import pro.fessional.wings.spring.consts.OrderedSlardarConst;

import static org.springframework.web.servlet.DispatcherServlet.LOCALE_RESOLVER_BEAN_NAME;

/**
 * @author trydofor
 * @since 2019-06-29
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = SlardarEnabledProp.Key$locale, havingValue = "true")
@AutoConfigureOrder(OrderedSlardarConst.LocaleConfiguration)
public class SlardarLocaleConfiguration {

    private final Log log = LogFactory.getLog(SlardarLocaleConfiguration.class);

    @Bean(LOCALE_RESOLVER_BEAN_NAME)
    @ConditionalOnClass(LocaleResolver.class)
    public WingsLocaleResolver wingsLocaleResolver(SlardarLocaleProp conf) {
        log.info("SlardarWebmvc spring-bean wingsLocaleResolver");
        final WingsLocaleResolver resolver = new WingsLocaleResolver();
        resolver.addLocaleCookie(conf.getLocaleCookie());
        resolver.addLocaleHeader(conf.getLocaleHeader());
        resolver.addLocaleParam(conf.getLocaleParam());
        resolver.addZoneidCookie(conf.getZoneidCookie());
        resolver.addZoneidHeader(conf.getZoneidHeader());
        resolver.addZoneidParam(conf.getZoneidParam());
        return resolver;
    }
}
