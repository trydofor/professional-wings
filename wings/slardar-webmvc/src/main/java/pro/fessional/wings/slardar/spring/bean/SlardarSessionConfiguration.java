package pro.fessional.wings.slardar.spring.bean;

import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.session.DefaultCookieSerializerCustomizer;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.boot.web.servlet.server.Session.Cookie;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.web.http.CookieHttpSessionIdResolver;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;
import org.springframework.session.web.http.HeaderHttpSessionIdResolver;
import org.springframework.session.web.http.HttpSessionIdResolver;
import org.springframework.util.StringUtils;
import pro.fessional.mirana.best.AssertArgs;
import pro.fessional.wings.slardar.session.WingsSessionIdResolver;
import pro.fessional.wings.slardar.spring.prop.SlardarEnabledProp;
import pro.fessional.wings.slardar.spring.prop.SlardarSessionProp;
import pro.fessional.wings.spring.consts.OrderedSlardarConst;

import java.util.ArrayList;
import java.util.List;

/**
 * Configure the IMap for spring session via session-hazelcast.xml, mainly index and serial.
 * <a href="https://docs.spring.io/spring-boot/docs/3.0.3/reference/htmlsingle/#web.spring-session">Spring Session</a>
 * <a href="https://docs.spring.io/spring-session/reference/spring-security.html">spring-security</a>
 * <a href="https://docs.hazelcast.com/tutorials/spring-session-hazelcast">spring-session-hazelcast</a>
 *
 * @author trydofor
 * @since 2019-06-26
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = SlardarEnabledProp.Key$session, havingValue = "true")
@EnableConfigurationProperties(ServerProperties.class)
@RequiredArgsConstructor
@AutoConfigureOrder(OrderedSlardarConst.SessionConfiguration)
public class SlardarSessionConfiguration {

    private static final Log log = LogFactory.getLog(SlardarSessionConfiguration.class);

    private final SlardarSessionProp slardarSessionProp;

    @Bean
    public DefaultCookieSerializerCustomizer slardarCookieSerializerCustomizer() {
        log.info("SlardarWebmvc spring-bean slardarCookieSerializerCustomizer");
        return it -> {
            final boolean base64 = slardarSessionProp.isCookieBase64();
            log.info("SlardarWebmvc conf Session Cookie Base64=" + base64);
            it.setUseBase64Encoding(base64);
            final String jvmRoute = slardarSessionProp.getCookieRoute();
            if (StringUtils.hasText(jvmRoute)) {
                log.info("SlardarWebmvc conf Session Cookie jvmRoute=" + jvmRoute);
                it.setJvmRoute(jvmRoute);
            }
        };
    }

    @Bean
    public HttpSessionIdResolver httpSessionIdResolver(
            ObjectProvider<ServerProperties> serverProperties,
            ObjectProvider<CookieSerializer> cookieSerializer,
            ObjectProvider<DefaultCookieSerializerCustomizer> cookieSerializerCustomizers) {
        log.info("SlardarWebmvc spring-bean httpSessionIdResolver");

        final List<HttpSessionIdResolver> resolvers = new ArrayList<>();
        if (StringUtils.hasText(slardarSessionProp.getCookieName())) {
            final ServerProperties server = serverProperties.getIfAvailable();
            AssertArgs.notNull(server, "need `server.servlet.session.*` config");
            Cookie cookie = server.getServlet().getSession().getCookie();
            final String propName = slardarSessionProp.getCookieName();
            final String servName = cookie.getName();
            if (propName.equals(servName)) {
                log.info("SlardarWebmvc conf cookieHttpSessionIdResolver by server.servlet.session.cookie.name=" + propName);
            }
            else {
                log.warn("SlardarWebmvc conf cookieHttpSessionIdResolver by cookie.name=" + propName + ", but server.servlet.session.cookie.name =" + servName);
            }

            CookieSerializer serializer = cookieSerializer.getIfAvailable();
            if (serializer == null) {
                log.info("SlardarWebmvc conf httpSessionIdResolver CookieSerializer by default");
                DefaultCookieSerializer defaultCookieSerializer = new DefaultCookieSerializer();
                PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
                map.from(propName).to(defaultCookieSerializer::setCookieName);
                map.from(cookie::getDomain).to(defaultCookieSerializer::setDomainName);
                map.from(cookie::getPath).to(defaultCookieSerializer::setCookiePath);
                map.from(cookie::getHttpOnly).to(defaultCookieSerializer::setUseHttpOnlyCookie);
                map.from(cookie::getSecure).to(defaultCookieSerializer::setUseSecureCookie);
                map.from(cookie::getMaxAge).to((maxAge) -> defaultCookieSerializer.setCookieMaxAge((int) maxAge.getSeconds()));
                cookieSerializerCustomizers.orderedStream().forEach((customizer) -> customizer.customize(defaultCookieSerializer));
                serializer = defaultCookieSerializer;
            }
            final CookieHttpSessionIdResolver cookieHttpSessionIdResolver = new CookieHttpSessionIdResolver();
            cookieHttpSessionIdResolver.setCookieSerializer(serializer);
            resolvers.add(cookieHttpSessionIdResolver);
        }

        final String headerName = slardarSessionProp.getHeaderName();
        if (StringUtils.hasText(headerName)) {
            final HeaderHttpSessionIdResolver headerHttpSessionIdResolver = new HeaderHttpSessionIdResolver(headerName);
            log.info("SlardarWebmvc conf headerHttpSessionIdResolver by header.name=" + headerName);
            resolvers.add(headerHttpSessionIdResolver);
        }

        return new WingsSessionIdResolver(resolvers);
    }
}
