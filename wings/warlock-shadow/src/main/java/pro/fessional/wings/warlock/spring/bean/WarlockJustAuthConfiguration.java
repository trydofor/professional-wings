package pro.fessional.wings.warlock.spring.bean;

import com.xkcoding.http.config.HttpConfig;
import lombok.RequiredArgsConstructor;
import me.zhyd.oauth.cache.AuthStateCache;
import me.zhyd.oauth.config.AuthConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import pro.fessional.wings.warlock.security.justauth.AuthConfigWrapper;
import pro.fessional.wings.warlock.security.justauth.AuthStateBuilder;
import pro.fessional.wings.warlock.security.justauth.JustAuthRequestBuilder;
import pro.fessional.wings.warlock.security.justauth.JustAuthStateCaffeine;
import pro.fessional.wings.warlock.spring.prop.WarlockEnabledProp;
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
@ConditionalOnProperty(name = WarlockEnabledProp.Key$justAuth, havingValue = "true")
@RequiredArgsConstructor
public class WarlockJustAuthConfiguration {

    private final static Log log = LogFactory.getLog(WarlockJustAuthConfiguration.class);

    private final WarlockJustAuthProp justAuthProp;
    private final WarlockSecurityProp securityProp;

    @Bean
    @ConditionalOnMissingBean(AuthStateCache.class)
    public AuthStateCache authStateCache() {
        log.info("Wings conf authStateCache");
        return new JustAuthStateCaffeine(justAuthProp.getCacheSize(), justAuthProp.getCacheLive());
    }

    @Bean
    @ConditionalOnMissingBean(JustAuthRequestBuilder.class)
    public JustAuthRequestBuilder justAuthRequestBuilder(AuthStateCache cache, AuthStateBuilder builder) {
        log.info("Wings conf justAuthRequestFactory");
        JustAuthRequestBuilder bean = new JustAuthRequestBuilder();
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
                log.info("Wings conf justAuthRequestFactory auth-type " + k);
            }
            else {
                final Proxy.Type ht = Proxy.Type.valueOf(hc.getProxyType());
                final Proxy proxy = new Proxy(ht, new InetSocketAddress(hc.getProxyHost(), hc.getProxyPort()));
                ac.setHttpConfig(HttpConfig
                        .builder()
                        .timeout(hc.getTimeout() * 1000)
                        .proxy(proxy)
                        .build());
                log.info("Wings conf justAuthRequestFactory auth-type " + k + ", proxy=" + hc.getProxyType());
            }

            // 处理动态redirect-uri
            map.put(em, AuthConfigWrapper.tryWrap(ac, justAuthProp.getSafeHost()));
        }

        bean.setAuthConfigMap(map);
        bean.setAuthStateCache(cache);
        bean.setAuthStateBuilder(builder);
        return bean;
    }
}
