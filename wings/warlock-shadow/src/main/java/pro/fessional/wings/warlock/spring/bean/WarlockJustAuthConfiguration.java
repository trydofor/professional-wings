package pro.fessional.wings.warlock.spring.bean;

import com.xkcoding.http.config.HttpConfig;
import me.zhyd.oauth.config.AuthConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import pro.fessional.mirana.flow.FlowEnum;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;
import pro.fessional.wings.slardar.security.WingsAuthHelper;
import pro.fessional.wings.slardar.servlet.resolver.WingsRemoteResolver;
import pro.fessional.wings.warlock.security.justauth.AuthStateBuilder;
import pro.fessional.wings.warlock.security.justauth.JustAuthRequestManager;
import pro.fessional.wings.warlock.security.justauth.JustAuthRequestManager.SuccessHandler;
import pro.fessional.wings.warlock.security.justauth.JustAuthStateCache;
import pro.fessional.wings.warlock.spring.prop.WarlockJustAuthProp;
import pro.fessional.wings.warlock.spring.prop.WarlockSecurityProp;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.HashMap;
import java.util.Map;

import static java.net.Proxy.Type.DIRECT;

/**
 * @author trydofor
 * @since 2019-12-01
 */
@Configuration(proxyBeanMethods = false)
@ConditionalWingsEnabled
public class WarlockJustAuthConfiguration {

    private final static Log log = LogFactory.getLog(WarlockJustAuthConfiguration.class);

    @Bean
    @ConditionalWingsEnabled
    public JustAuthStateCache justAuthStateCache(WarlockJustAuthProp justAuthProp) {
        log.info("WarlockShadow spring-bean justAuthStateCache");
        return new JustAuthStateCache(justAuthProp.getCacheSize(), justAuthProp.getCacheLive());
    }

    @Bean
    @ConditionalWingsEnabled
    public SuccessHandler justAuthRequestSuccessHandler(
        AuthStateBuilder authStateBuilder, WingsRemoteResolver remoteResolver) {
        log.info("WarlockShadow spring-bean justAuthRequestSuccessHandler");
        return (authType, request, authUser, detail) -> {
            final Map<String, String> meta = detail.getMetaData();
            meta.put(WingsAuthHelper.AuthType, authType.name());
            meta.put(WingsAuthHelper.AuthZone, authStateBuilder.parseAuthZone(request));
            meta.put(WingsAuthHelper.AuthAddr, remoteResolver.resolveRemoteIp(request));
            meta.put(WingsAuthHelper.AuthAgent, remoteResolver.resolveAgentInfo(request));
            return FlowEnum.Default;
        };
    }

    @Bean
    @ConditionalWingsEnabled
    public JustAuthRequestManager justAuthRequestManager(WarlockJustAuthProp justAuthProp, WarlockSecurityProp securityProp) {
        log.info("WarlockShadow spring-bean justAuthRequestManager");
        final Map<String, WarlockJustAuthProp.Http> hcs = justAuthProp.getHttpConf();
        final Map<String, Enum<?>> emp = securityProp.mapAuthTypeEnum();

        final Map<Enum<?>, AuthConfig> map = new HashMap<>();
        for (Map.Entry<String, AuthConfig> en : justAuthProp.getAuthType().entrySet()) {
            final String k = en.getKey();
            Enum<?> em = emp.get(k);
            if (em == null) throw new IllegalArgumentException("failed to map auth-type" + k);

            AuthConfig ac = en.getValue();
            WarlockJustAuthProp.Http hc = hcs.get(k);
            if (hc == null || !StringUtils.hasText(hc.getProxyHost()) || DIRECT.name().equalsIgnoreCase(hc.getProxyType())) {
                log.info("WarlockShadow conf JustAuthConfigBuilder auth-type " + k);
            }
            else {
                final Proxy.Type ht = Proxy.Type.valueOf(hc.getProxyType());
                final Proxy proxy = new Proxy(ht, new InetSocketAddress(hc.getProxyHost(), hc.getProxyPort()));
                ac.setHttpConfig(HttpConfig
                    .builder()
                    .timeout(hc.getTimeout() * 1000)
                    .proxy(proxy)
                    .build());
                log.info("WarlockShadow conf JustAuthConfigBuilder auth-type " + k + ", proxy=" + hc.getProxyType());
            }

            map.put(em, ac);
        }

        JustAuthRequestManager bean = new JustAuthRequestManager();
        bean.setSafeRedirectHost(justAuthProp.getSafeHost());
        bean.setAuthConfigMap(map);
        return bean;
    }
}
