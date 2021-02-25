package pro.fessional.wings.warlock.spring.bean;

import com.xkcoding.http.config.HttpConfig;
import lombok.Setter;
import me.zhyd.oauth.cache.AuthStateCache;
import me.zhyd.oauth.config.AuthConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
public class WarlockJustAuthConfiguration {

    private final static Log logger = LogFactory.getLog(WarlockJustAuthConfiguration.class);

    @Setter(onMethod = @__({@Autowired}))
    private WarlockJustAuthProp justAuthProp;

    @Setter(onMethod = @__({@Autowired}))
    private WarlockSecurityProp securityProp;

    @Bean
    @ConditionalOnMissingBean
    public JustAuthRequestBuilder justAuthRequestBuilder(AuthStateCache cache) {
        logger.info("Wings conf justAuthRequestFactory");
        JustAuthRequestBuilder factory = new JustAuthRequestBuilder();
        final Map<String, WarlockJustAuthProp.Http> hcs = justAuthProp.getHttpConf();
        final Map<String, Enum<?>> emp = securityProp.mapAuthTypeEnum();

        final Map<Enum<?>, AuthConfig> map = new HashMap<>();
        for (Map.Entry<String, AuthConfig> en : justAuthProp.getAuthType().entrySet()) {
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
    public AuthStateCache authStateCache() {
        logger.info("Wings conf authStateCache");
        return new JustAuthStateCaffeine(justAuthProp.getCacheSize(), justAuthProp.getCacheLive());
    }
}
