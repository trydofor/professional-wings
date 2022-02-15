package pro.fessional.wings.warlock.spring.bean;

import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.savedrequest.RequestCache;
import pro.fessional.mirana.data.Null;
import pro.fessional.wings.slardar.security.WingsAuthDetailsSource;
import pro.fessional.wings.slardar.servlet.response.ResponseHelper;
import pro.fessional.wings.slardar.spring.help.SecurityConfigHelper;
import pro.fessional.wings.warlock.spring.bean.WarlockSecurityWebConfiguration.HttpSecurityConfigure;
import pro.fessional.wings.warlock.spring.prop.WarlockEnabledProp;
import pro.fessional.wings.warlock.spring.prop.WarlockSecurityProp;

import javax.servlet.http.HttpServletResponse;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * @author trydofor
 * @since 2019-12-01
 */
@Configuration
@ConditionalOnProperty(name = WarlockEnabledProp.Key$securityHttp, havingValue = "true")
@RequiredArgsConstructor
public class WarlockSecurityHttpConfiguration {

    private final static Log logger = LogFactory.getLog(WarlockSecurityHttpConfiguration.class);

    private final SessionRegistry sessionRegistry;
    private final WarlockSecurityProp securityProp;
    private final AuthenticationSuccessHandler authenticationSuccessHandler;
    private final AuthenticationFailureHandler authenticationFailureHandler;
    private final LogoutSuccessHandler logoutSuccessHandler;
    private final WingsAuthDetailsSource<?> wingsAuthDetailsSource;

    @Bean
    @ConditionalOnProperty(name = WarlockEnabledProp.Key$securityHttpBind, havingValue = "true")
    public HttpSecurityConfigure warlockBindHttpSecurityConfigure() {
        return http ->
                http.apply(SecurityConfigHelper.http())
                    .httpPermit(conf -> conf
                            .permitCorsAll()
                            .permitTest()
                    )
                    .bindLogin(conf -> conf
                            .loginPage(securityProp.getLoginPage()) // 无权限时返回的页面，
                            .loginForward(securityProp.isLoginForward()) // 无权限时返回的页面，
                            .loginProcessingUrl(securityProp.getLoginUrl()) // filter处理，不需要controller
                            .usernameParameter(securityProp.getUsernamePara())
                            .passwordParameter(securityProp.getPasswordPara())
                            .successHandler(authenticationSuccessHandler)
                            .failureHandler(authenticationFailureHandler)
                            .authenticationDetailsSource(wingsAuthDetailsSource)
                            .bindAuthTypeToEnums(securityProp.mapAuthTypeEnum())
                    )
                    .and()
                    .logout(conf -> conf
                            .logoutUrl(securityProp.getLogoutUrl())
                            .clearAuthentication(true)
                            .invalidateHttpSession(true)
                            .logoutSuccessHandler(logoutSuccessHandler)
                    )
                    .sessionManagement(conf -> conf
                            .maximumSessions(securityProp.getSessionMaximum())
                            .sessionRegistry(sessionRegistry)
                            .expiredSessionStrategy(event -> {
                                HttpServletResponse response = event.getResponse();
                                ResponseHelper.writeBodyUtf8(response, securityProp.getSessionExpiredBody());
                            })
                    );
    }

    @Bean
    @ConditionalOnProperty(name = WarlockEnabledProp.Key$securityHttpAuth, havingValue = "true")
    public HttpSecurityConfigure warlockAuthHttpSecurityConfigure() {
        return http -> http.authorizeRequests(conf ->
                {
                    for (Map.Entry<String, List<String>> en : securityProp.getAuthority().entrySet()) {
                        final List<String> paths = en.getValue();
                        if (paths.isEmpty()) continue;
                        LinkedHashSet<String> uniq = new LinkedHashSet<>(paths);
                        logger.info("Wings conf HttpSecurity, bind authority=[" + en.getKey() + "] "
                                    + String.join("\n, ", uniq));
                        conf.antMatchers(uniq.toArray(Null.StrArr)).hasAuthority(en.getKey());
                    }

                    final Set<String> authed = new HashSet<>(securityProp.getAuthenticated().values());
                    authed.removeIf(it -> it == null || it.isEmpty());

                    final Set<String> permed = new HashSet<>(securityProp.getPermitAll().values());
                    permed.removeIf(it -> it == null || it.isEmpty());

                    conf.antMatchers(authed.toArray(Null.StrArr))
                        .authenticated()
                        .antMatchers(permed.toArray(Null.StrArr))
                        .permitAll();

                    logger.info("Wings conf HttpSecurity, bind Authenticated="
                                + String.join("\n, ", authed));
                    logger.info("Wings conf HttpSecurity, bind PermitAll="
                                + String.join("\n, ", permed));

                }
        );
    }


    @Bean
    @ConditionalOnProperty(name = WarlockEnabledProp.Key$securityHttpBase, havingValue = "true")
    public HttpSecurityConfigure warlockBaseHttpSecurityConfigure() {
        return HttpSecurity::httpBasic;
    }

    @Bean
    @ConditionalOnProperty(name = WarlockEnabledProp.Key$securityHttpAuto, havingValue = "true")
    public HttpSecurityConfigure warlockAutoHttpSecurityConfigure(
            ObjectProvider<CsrfTokenRepository> csrf,
            ObjectProvider<RequestCache> cache) {
        return http -> {
            final RequestCache rc = cache.getIfAvailable();
            if (rc == null) {
                http.requestCache().disable();
                logger.info("Wings conf HttpSecurity, requestCache disable");
            }
            else {
                http.requestCache().requestCache(rc);
                logger.info("Wings conf HttpSecurity, requestCache " + rc.getClass().getName());
            }

            final CsrfTokenRepository ct = csrf.getIfAvailable();
            if (ct == null) {
                http.csrf().disable();
                logger.info("Wings conf HttpSecurity, csrf disable");
            }
            else {
                http.csrf().csrfTokenRepository(ct);
                logger.info("Wings conf HttpSecurity, csrf " + ct.getClass().getName());
            }
        };
    }
}
