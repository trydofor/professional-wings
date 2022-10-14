package pro.fessional.wings.warlock.spring.bean;

import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.util.StringUtils;
import pro.fessional.wings.warlock.spring.prop.WarlockEnabledProp;
import pro.fessional.wings.warlock.spring.prop.WarlockSecurityProp;

import java.util.Map;


/**
 * @author trydofor
 * @since 2019-12-01
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = WarlockEnabledProp.Key$securityAuto, havingValue = "true")
@RequiredArgsConstructor
public class WarlockSecurityAutoConfiguration extends WebSecurityConfigurerAdapter {

    private final static Log log = LogFactory.getLog(WarlockSecurityAutoConfiguration.class);

    private final WarlockSecurityProp warlockSecurityProp;
    private final Map<String, HttpSecurityCustomizer> configures;

    /**
     * The URL paths provided by the framework are
     * /oauth/authorize (the authorization endpoint),
     * /oauth/token (the token endpoint),
     * /oauth/confirm_access (user posts approval for grants here),
     * /oauth/error (used to render errors in the authorization server),
     * /oauth/check_token (used by Resource Servers to decode access tokens), and
     * /oauth/token_key (exposes public key for token verification if using JWT tokens).
     * <p>
     * Note: if your Authorization Server is also a Resource Server then
     * there is another security filter chain with lower priority controlling the API resources.
     * Fo those requests to be protected by access tokens you need their paths
     * not to be matched by the ones in the main user-facing filter chain,
     * so be sure to include a request matcher that picks out
     * only non-API resources in the WebSecurityConfigurer above.
     */
    @Override
    public void configure(HttpSecurity http) throws Exception {
        log.info("WarlockShadow conf HttpSecurity");
        for (Map.Entry<String, HttpSecurityCustomizer> en : configures.entrySet()) {
            log.info("WarlockShadow conf HttpSecurity, bean=" + en.getKey());
            en.getValue().customize(http);
        }

        //
        final String anyRequest = warlockSecurityProp.getAnyRequest();
        if (StringUtils.hasText(anyRequest)) {
            log.info("WarlockShadow conf HttpSecurity, anyRequest=" + anyRequest);
            String str = anyRequest.trim();
            if ("permitAll".equalsIgnoreCase(str)) {
                http.authorizeRequests().anyRequest().permitAll();
            }
            else if ("authenticated".equalsIgnoreCase(str)) {
                http.authorizeRequests().anyRequest().authenticated();
            }
            else if ("anonymous".equalsIgnoreCase(str)) {
                http.authorizeRequests().anyRequest().anonymous();
            }
            else if ("fullyAuthenticated".equalsIgnoreCase(str)) {
                http.authorizeRequests().anyRequest().fullyAuthenticated();
            }
            else {
                http.authorizeRequests().anyRequest().hasAnyAuthority(str.split("[, \t\r\n]+"));
            }
        }
    }

    public interface HttpSecurityCustomizer {
        void customize(HttpSecurity http) throws Exception;
    }
}
