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
import pro.fessional.wings.warlock.security.justauth.JustAuthLoginPageCombo;
import pro.fessional.wings.warlock.security.justauth.JustAuthRequestBuilder;
import pro.fessional.wings.warlock.security.justauth.JustAuthStateCaffeine;
import pro.fessional.wings.warlock.spring.prop.WarlockEnabledProp;
import pro.fessional.wings.warlock.spring.prop.WarlockJustAuthProp;
import pro.fessional.wings.warlock.spring.prop.WarlockSecurityProp;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.HashMap;
import java.util.Map;


/**
 * @author trydofor
 * @since 2019-12-01
 */
@Configuration
@ConditionalOnProperty(name = WarlockEnabledProp.Key$justAuth, havingValue = "true")
@RequiredArgsConstructor
public class WarlockJustAuthConfiguration {

    private final static Log logger = LogFactory.getLog(WarlockJustAuthConfiguration.class);


    @Bean
    @ConditionalOnMissingBean
    public JustAuthRequestBuilder justAuthRequestBuilder(WarlockJustAuthProp justConf,
                                                         WarlockSecurityProp secrConf,
                                                         AuthStateCache cache) {
        logger.info("Wings conf justAuthRequestFactory");
        JustAuthRequestBuilder factory = new JustAuthRequestBuilder();
        final Map<String, WarlockJustAuthProp.Http> hcs = justConf.getHttpConf();
        final Map<String, Enum<?>> emp = secrConf.mapAuthTypeEnum();

        final Map<Enum<?>, AuthConfig> map = new HashMap<>();
        for (Map.Entry<String, AuthConfig> en : justConf.getAuthType().entrySet()) {
            final String k = en.getKey();
            Enum<?> em = emp.get(k);
            if (em == null) throw new IllegalArgumentException("failed to map auth-type" + k);

            AuthConfig ac = en.getValue();
            WarlockJustAuthProp.Http hc = hcs.get(k);
            if (hc != null) {
                final Proxy.Type ht = Proxy.Type.valueOf(hc.getProxyType());
                final Proxy proxy = new Proxy(ht, new InetSocketAddress(hc.getProxyHost(), hc.getProxyPort()));
                ac.setHttpConfig(HttpConfig
                        .builder()
                        .timeout(hc.getTimeout())
                        .proxy(proxy)
                        .build());
            }
            logger.info("Wings conf justAuthRequestFactory auth-type " + k);
            map.put(em, ac);
        }

        factory.setAuthConfigMap(map);
        factory.setAuthStateCache(cache);
        return factory;
    }

    @Bean
    @ConditionalOnMissingBean
    public AuthStateCache authStateCache(WarlockJustAuthProp config) {
        logger.info("Wings conf authStateCache");
        return new JustAuthStateCaffeine(config.getCacheSize(), config.getCacheLive());
    }

    @Bean
    @ConditionalOnProperty(name = WarlockEnabledProp.Key$justAuthLoginPageCombo, havingValue = "true")
    public JustAuthLoginPageCombo justAuthLoginPageCombo(JustAuthRequestBuilder justAuthRequestBuilder) {
        final JustAuthLoginPageCombo handler = new JustAuthLoginPageCombo();
        handler.setJustAuthRequestBuilder(justAuthRequestBuilder);
        return handler;
    }
}
