package pro.fessional.wings.slardar.spring.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.i18n.TimeZoneAwareLocaleContext;
import pro.fessional.wings.slardar.context.SecurityContextUtil;
import pro.fessional.wings.slardar.context.TerminalContext;
import pro.fessional.wings.slardar.context.TerminalInterceptor;
import pro.fessional.wings.slardar.security.WingsUserDetails;
import pro.fessional.wings.slardar.servlet.resolver.WingsLocaleResolver;
import pro.fessional.wings.slardar.servlet.resolver.WingsRemoteResolver;
import pro.fessional.wings.slardar.spring.prop.SlardarEnabledProp;
import pro.fessional.wings.slardar.spring.prop.SlardarTerminalProp;

import java.util.ArrayList;
import java.util.Map;

/**
 * @author trydofor
 * @since 2019-06-29
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = SlardarEnabledProp.Key$terminal, havingValue = "true")
public class SlardarTerminalConfiguration {

    private final Log log = LogFactory.getLog(SlardarTerminalConfiguration.class);

    @Bean
    @ConditionalOnBean({WingsRemoteResolver.class})
    public TerminalInterceptor.TerminalBuilder remoteTerminalBuilder(WingsRemoteResolver resolver) {
        log.info("SlardarWebmvc spring-bean remoteTerminalBuilder");
        return (builder, request) -> builder
                .terminalAddr(resolver.resolveRemoteIp(request))
                .terminalAgent(resolver.resolveAgentInfo(request));
    }

    @Bean
    @ConditionalOnBean({WingsRemoteResolver.class})
    public TerminalInterceptor.TerminalBuilder localeTerminalBuilder(WingsLocaleResolver resolver) {
        log.info("SlardarWebmvc spring-bean localeTerminalBuilder");
        return (builder, request) -> {
            final WingsUserDetails details = SecurityContextUtil.getUserDetails(false);
            if (details == null) {
                TimeZoneAwareLocaleContext locale = resolver.resolveI18nContext(request);
                builder.localeIfAbsent(locale.getLocale())
                       .timeZoneIfAbsent(locale.getTimeZone())
                       .guest();
            }
            else {
                builder.locale(details.getLocale())
                       .timeZone(details.getZoneId())
                       .user(details.getUserId());
            }
        };
    }

    @Bean
    @ConditionalOnMissingBean
    public TerminalInterceptor terminalInterceptor(SlardarTerminalProp prop, ObjectProvider<TerminalInterceptor.TerminalBuilder> builders) {
        log.info("SlardarWebmvc spring-bean terminalInterceptor");
        TerminalContext.initActive(true);
        final TerminalInterceptor bean = new TerminalInterceptor();
        builders.orderedStream().forEach(bean::addTerminalBuilder);
        //
        final Map<String, String> ex = prop.getExcludePatterns();
        if (!ex.isEmpty()) {
            final ArrayList<String> vs = new ArrayList<>(ex.values());
            log.info("SlardarWebmvc spring-conf terminalInterceptor ExcludePatterns=" + vs);
            bean.setExcludePatterns(vs);
        }

        final Map<String, String> ic = prop.getIncludePatterns();
        if(!ic.isEmpty()){
            final ArrayList<String> vs = new ArrayList<>(ic.values());
            log.info("SlardarWebmvc spring-conf terminalInterceptor IncludePatterns=" + vs);
            bean.setExcludePatterns(vs);
        }

        return bean;
    }
}
