package pro.fessional.wings.slardar.spring.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;
import pro.fessional.wings.slardar.servlet.resolver.WingsLocaleResolver;
import pro.fessional.wings.slardar.spring.prop.SlardarLocaleProp;

import static org.springframework.web.servlet.DispatcherServlet.LOCALE_RESOLVER_BEAN_NAME;

/**
 * @author trydofor
 * @see WebMvcAutoConfiguration.EnableWebMvcConfiguration
 * @since 2019-06-29
 */
@Configuration(proxyBeanMethods = false)
@ConditionalWingsEnabled
@ConditionalOnClass(LocaleResolver.class)
public class SlardarLocaleConfiguration {

    private final Log log = LogFactory.getLog(SlardarLocaleConfiguration.class);

    @Bean(LOCALE_RESOLVER_BEAN_NAME)
    @ConditionalWingsEnabled
    public WingsLocaleResolver localeResolver(SlardarLocaleProp conf) {
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
