package pro.fessional.wings.warlock.spring.bean;

import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
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
import pro.fessional.wings.slardar.spring.conf.WingsHttpPermitConfigurer;
import pro.fessional.wings.slardar.spring.help.SecurityConfigHelper;
import pro.fessional.wings.warlock.spring.bean.WarlockSecurityWebConfiguration.HttpSecurityConfigure;
import pro.fessional.wings.warlock.spring.prop.WarlockEnabledProp;
import pro.fessional.wings.warlock.spring.prop.WarlockSecurityProp;

import javax.servlet.http.HttpServletResponse;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;


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


    public static final int OrderWarlockBind = 1000;
    public static final int OrderWarlockAuth = 2000;
    public static final int OrderWarlockBase = 3000;
    public static final int OrderWarlockAuto = 4000;

    @Bean
    @ConditionalOnProperty(name = WarlockEnabledProp.Key$securityHttpBind, havingValue = "true")
    @Order(OrderWarlockBind)
    public HttpSecurityConfigure warlockBindHttpSecurityConfigure() {
        return http ->
                http.apply(SecurityConfigHelper.http())
                    .bindLogin(conf -> conf
                            .loginForward(securityProp.isLoginForward()) // 无权限时返回的页面，
                            .loginProcessingUrl(securityProp.getLoginProcUrl(), securityProp.getLoginProcMethod()) // filter处理，不需要controller
                            .loginPage(securityProp.getLoginPage()) // 无权限时返回的页面，
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
    @Order(OrderWarlockAuth)
    public HttpSecurityConfigure warlockAuthHttpSecurityConfigure() {
        return http -> http.authorizeRequests(conf ->
                {
                    for (Map.Entry<String, List<String>> en : securityProp.getAuthority().entrySet()) {
                        final List<String> paths = en.getValue();
                        if (paths.isEmpty()) continue;
                        LinkedHashSet<String> uniq = new LinkedHashSet<>(paths);
                        logger.info("Wings conf HttpSecurity, bind authority=[" + en.getKey() + "] "
                                    + String.join("\n,", uniq));
                        conf.antMatchers(uniq.toArray(Null.StrArr)).hasAuthority(en.getKey());
                    }

                    final Set<String> authed = new TreeSet<>(securityProp.getAuthenticated().values());
                    authed.removeIf(it -> it == null || it.isEmpty());

                    final Set<String> permed = new TreeSet<>(securityProp.getPermitAll().values());
                    permed.removeIf(it -> it == null || it.isEmpty());

                    conf.antMatchers(authed.toArray(Null.StrArr))
                        .authenticated()
                        .antMatchers(permed.toArray(Null.StrArr))
                        .permitAll();

                    logger.info("Wings conf HttpSecurity, bind Authenticated="
                                + String.join("\n,", authed));
                    logger.info("Wings conf HttpSecurity, bind PermitAll="
                                + String.join("\n,", permed));
                }
        );
    }


    @Bean
    @ConditionalOnProperty(name = WarlockEnabledProp.Key$securityHttpBase, havingValue = "true")
    @Order(OrderWarlockBase)
    public HttpSecurityConfigure warlockBaseHttpSecurityConfigure() {
        return HttpSecurity::httpBasic;
    }

    @Bean
    @ConditionalOnProperty(name = WarlockEnabledProp.Key$securityHttpAuto, havingValue = "true")
    @Order(OrderWarlockAuto)
    public HttpSecurityConfigure warlockAutoHttpSecurityConfigure(
            ObjectProvider<CsrfTokenRepository> csrf,
            ObjectProvider<RequestCache> cache) {
        return http -> {
            http.cors().configurationSource(WingsHttpPermitConfigurer.corsPermitAll());

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
