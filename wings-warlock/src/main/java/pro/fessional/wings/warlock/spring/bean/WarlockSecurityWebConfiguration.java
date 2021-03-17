package pro.fessional.wings.warlock.spring.bean;

import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import pro.fessional.mirana.data.Null;
import pro.fessional.wings.slardar.security.WingsAuthDetailsSource;
import pro.fessional.wings.slardar.spring.help.SecurityConfigHelper;
import pro.fessional.wings.warlock.spring.prop.WarlockEnabledProp;
import pro.fessional.wings.warlock.spring.prop.WarlockSecurityProp;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;


/**
 * @author trydofor
 * @since 2019-12-01
 */
@Configuration
@ConditionalOnProperty(name = WarlockEnabledProp.Key$securityConf, havingValue = "true")
@RequiredArgsConstructor
public class WarlockSecurityWebConfiguration extends WebSecurityConfigurerAdapter {

    private final static Log logger = LogFactory.getLog(WarlockSecurityWebConfiguration.class);

    private final SessionRegistry sessionRegistry;
    private final WarlockSecurityProp securityProp;
    private final AuthenticationSuccessHandler authenticationSuccessHandler;
    private final AuthenticationFailureHandler authenticationFailureHandler;
    private final LogoutSuccessHandler logoutSuccessHandler;
    private final WingsAuthDetailsSource<?> wingsAuthDetailsSource;

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
        logger.info("Wings conf HttpSecurity");
        http.apply(SecurityConfigHelper.http())
            .httpPermit(conf -> conf
                    .permitCorsAll()
                    .permitTest()
            )
            .bindLogin(conf -> conf
                    .loginPage(securityProp.getLoginPage()) // 无权限时返回的页面，
                    .loginProcessingUrl(securityProp.getLoginUrl()) // filter处理，不需要controller
                    .usernameParameter(securityProp.getUsernamePara())
                    .passwordParameter(securityProp.getPasswordPara())
                    .successHandler(authenticationSuccessHandler)
                    .failureHandler(authenticationFailureHandler)
                    .authenticationDetailsSource(wingsAuthDetailsSource)
                    .bindAuthTypeToEnums(securityProp.mapAuthTypeEnum())
            )
            .and()
            .authorizeRequests(conf ->
                    {
                        for (Map.Entry<String, List<String>> en : securityProp.getAuthority().entrySet()) {
                            final List<String> paths = en.getValue();
                            if (paths.isEmpty()) continue;
                            LinkedHashSet<String> uniq = new LinkedHashSet<>(paths);
                            logger.info("Wings conf HttpSecurity. bind authority=" + en.getKey()
                                    + String.join("\n, ", uniq));
                            conf.antMatchers(uniq.toArray(Null.StrArr)).hasAuthority(en.getKey());
                        }

                        conf.antMatchers(securityProp.getAuthenticated().toArray(Null.StrArr))
                            .authenticated()
                            .antMatchers(securityProp.getPermitAll().toArray(Null.StrArr))
                            .permitAll();

                        logger.info("Wings conf HttpSecurity. bind Authenticated"
                                + String.join("\n, ", securityProp.getAuthenticated()));
                        logger.info("Wings conf HttpSecurity. bind PermitAll="
                                + String.join("\n, ", securityProp.getPermitAll()));

                    }
            )
            .logout(conf -> conf
                    .logoutUrl(securityProp.getLogoutUrl())
                    .clearAuthentication(true)
                    .invalidateHttpSession(true)
                    .logoutSuccessHandler(logoutSuccessHandler)
            )
            .sessionManagement(conf -> conf
                    .maximumSessions(1)
                    .sessionRegistry(sessionRegistry)
            )
            .requestCache().disable()
            .csrf().disable();
    }
}
