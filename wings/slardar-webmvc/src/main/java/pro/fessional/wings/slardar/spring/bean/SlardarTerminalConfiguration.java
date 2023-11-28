package pro.fessional.wings.slardar.spring.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;
import pro.fessional.wings.slardar.constants.SlardarServletConst;
import pro.fessional.wings.slardar.context.SecurityContextUtil;
import pro.fessional.wings.slardar.context.TerminalInterceptor;
import pro.fessional.wings.slardar.context.TerminalSecurityAttribute;
import pro.fessional.wings.slardar.security.WingsUserDetails;
import pro.fessional.wings.slardar.servlet.resolver.WingsLocaleResolver;
import pro.fessional.wings.slardar.servlet.resolver.WingsRemoteResolver;
import pro.fessional.wings.slardar.spring.prop.SlardarEnabledProp;
import pro.fessional.wings.slardar.spring.prop.SlardarTerminalProp;

import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

import static pro.fessional.wings.slardar.context.TerminalAttribute.TerminalAddr;
import static pro.fessional.wings.slardar.context.TerminalAttribute.TerminalAgent;

/**
 * @author trydofor
 * @since 2019-06-29
 */
@Configuration(proxyBeanMethods = false)
@ConditionalWingsEnabled(abs = SlardarEnabledProp.Key$terminal)
public class SlardarTerminalConfiguration {

    private final Log log = LogFactory.getLog(SlardarTerminalConfiguration.class);

    @Bean
    @ConditionalWingsEnabled
    public TerminalInterceptor.TerminalBuilder remoteTerminalBuilder(WingsRemoteResolver resolver) {
        log.info("SlardarWebmvc spring-bean remoteTerminalBuilder");
        return (builder, request) -> builder
                .terminal(TerminalAddr, resolver.resolveRemoteIp(request))
                .terminal(TerminalAgent, resolver.resolveAgentInfo(request));
    }

    @Bean
    @ConditionalWingsEnabled
    public TerminalInterceptor.TerminalBuilder securityTerminalBuilder(WingsLocaleResolver resolver) {
        log.info("SlardarWebmvc spring-bean securityTerminalBuilder");
        return (builder, request) -> {
            final Authentication authn = SecurityContextUtil.getAuthentication(false);
            final WingsUserDetails details = SecurityContextUtil.getUserDetails(authn);
            if (details == null) {
                final Long userId = (Long) request.getAttribute(SlardarServletConst.AttrUserId);
                final var locale = resolver.resolveI18nContext(request, userId);
                builder.locale(locale.getLocale())
                       .timeZone(locale.getTimeZone())
                       .userOrGuest(userId);
            }
            else {
                builder.locale(details.getLocale())
                       .timeZone(details.getZoneId())
                       .user(details.getUserId())
                       .authType(details.getAuthType())
                       .username(details.getUsername())
                       .authPerm(details.getAuthorities().stream()
                                        .map(GrantedAuthority::getAuthority)
                                        .collect(Collectors.toSet()))
                       .terminal(TerminalSecurityAttribute.UserDetails, details)
                       .terminal(TerminalSecurityAttribute.AuthDetails, SecurityContextUtil.getAuthDetails(authn));
            }
        };
    }

    @Bean
    @ConditionalWingsEnabled
    public TerminalInterceptor terminalInterceptor(SlardarTerminalProp prop, ObjectProvider<TerminalInterceptor.TerminalBuilder> builders, ObjectProvider<TerminalInterceptor.TerminalLogger> loggers) {
        log.info("SlardarWebmvc spring-bean terminalInterceptor");

        final TerminalInterceptor bean = new TerminalInterceptor();
        builders.orderedStream().forEach(bean::addTerminalBuilder);
        loggers.orderedStream().forEach(bean::addTerminalLogger);

        //
        final Map<String, String> ex = prop.getExcludePatterns();
        if (!ex.isEmpty()) {
            final ArrayList<String> vs = new ArrayList<>(ex.values());
            log.info("SlardarWebmvc spring-conf terminalInterceptor ExcludePatterns=" + vs);
            bean.setExcludePatterns(vs);
        }

        final Map<String, String> ic = prop.getIncludePatterns();
        if (!ic.isEmpty()) {
            final ArrayList<String> vs = new ArrayList<>(ic.values());
            log.info("SlardarWebmvc spring-conf terminalInterceptor IncludePatterns=" + vs);
            bean.setExcludePatterns(vs);
        }

        return bean;
    }
}
