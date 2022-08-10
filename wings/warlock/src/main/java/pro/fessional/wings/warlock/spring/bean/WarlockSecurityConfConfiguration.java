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
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.savedrequest.RequestCache;
import pro.fessional.mirana.data.Null;
import pro.fessional.wings.slardar.security.WingsAuthDetailsSource;
import pro.fessional.wings.slardar.servlet.response.ResponseHelper;
import pro.fessional.wings.slardar.spring.conf.WingsHttpPermitConfigurer;
import pro.fessional.wings.slardar.spring.help.SecurityConfigHelper;
import pro.fessional.wings.warlock.spring.bean.WarlockSecurityAutoConfiguration.HttpSecurityCustomizer;
import pro.fessional.wings.warlock.spring.prop.WarlockEnabledProp;
import pro.fessional.wings.warlock.spring.prop.WarlockSecurityProp;

import javax.servlet.http.HttpServletResponse;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import static pro.fessional.wings.silencer.spring.help.CommonPropHelper.validValue;


/**
 * @author trydofor
 * @since 2019-12-01
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = WarlockEnabledProp.Key$securityAuto, havingValue = "true")
@RequiredArgsConstructor
public class WarlockSecurityConfConfiguration {

    private final static Log logger = LogFactory.getLog(WarlockSecurityConfConfiguration.class);

    private final SessionRegistry sessionRegistry;
    private final WarlockSecurityProp securityProp;

    public static final int OrderWarlockBase = 1000;
    public static final int OrderWarlockBind = 2000;
    public static final int OrderWarlockAuth = 3000;
    public static final int OrderWarlockAuto = 4000;

    @Bean
    @ConditionalOnProperty(name = WarlockEnabledProp.Key$securityWebAutos, havingValue = "true")
    public WebSecurityCustomizer warlockWebCustomizer(ObjectProvider<HttpFirewall> httpFirewall) {
        logger.info("Wings conf warlockWebCustomizer");
        return web -> {
            if (securityProp.isWebDebug()) {
                logger.info("Wings conf WebSecurity, WebDebug=true");
                web.debug(true);
            }
            final Map<String, String> webIgnore = securityProp.getWebIgnore();
            if (!webIgnore.isEmpty()) {
                final Set<String> ignores = validValue(webIgnore.values());
                logger.info("Wings conf WebSecurity, ignoring=" + String.join("\n,", ignores));
                web.ignoring().antMatchers(ignores.toArray(Null.StrArr));
            }
            final HttpFirewall firewall = httpFirewall.getIfAvailable();
            if (firewall != null) {
                logger.info("Wings conf WebSecurity, httpFirewall=" + firewall.getClass());
                web.httpFirewall(firewall);
            }
        };
    }

    @Bean
    @ConditionalOnProperty(name = WarlockEnabledProp.Key$securityHttpBind, havingValue = "true")
    @Order(OrderWarlockBind)
    public HttpSecurityCustomizer warlockBindHttpSecurityConfigure(
            ObjectProvider<AuthenticationSuccessHandler> authenticationSuccessHandler,
            ObjectProvider<AuthenticationFailureHandler> authenticationFailureHandler,
            ObjectProvider<WingsAuthDetailsSource<?>> wingsAuthDetailsSource,
            ObjectProvider<LogoutSuccessHandler> logoutSuccessHandler
    ) {
        return http -> {
            final AuthenticationSuccessHandler authOkHandler = authenticationSuccessHandler.getIfAvailable();
            final AuthenticationFailureHandler authNgHandler = authenticationFailureHandler.getIfAvailable();
            final WingsAuthDetailsSource<?> authDetailSource = wingsAuthDetailsSource.getIfAvailable();
            final LogoutSuccessHandler logoutOkHandler = logoutSuccessHandler.getIfAvailable();
            logger.info("Wings conf HttpSecurity, successHandler=" + (authOkHandler == null ? "null" : authOkHandler.getClass()));
            logger.info("Wings conf HttpSecurity, failureHandler=" + (authNgHandler == null ? "null" : authNgHandler.getClass()));
            logger.info("Wings conf HttpSecurity, authenticationDetailsSource=" + (authDetailSource == null ? "null" : authDetailSource.getClass()));
            logger.info("Wings conf HttpSecurity, logoutSuccessHandler=" + (logoutOkHandler == null ? "null" : logoutOkHandler.getClass()));

            http.apply(SecurityConfigHelper.http())
                .bindLogin(conf -> conf
                        // 初始authenticationEntryPoint
                        .loginPage(securityProp.getLoginPage()) // 无权限时返回的页面。
                        // 初始filter.RequestMatcher
                        .loginProcessingUrl(securityProp.getLoginProcUrl(), securityProp.getLoginProcMethod()) // filter处理，不需要controller
                        .loginForward(securityProp.isLoginForward()) // 无权限时返回的页面，
                        .usernameParameter(securityProp.getUsernamePara())
                        .passwordParameter(securityProp.getPasswordPara())
                        .successHandler(authOkHandler)
                        .failureHandler(authNgHandler)
                        .authenticationDetailsSource(authDetailSource)
                        .bindAuthTypeDefault(securityProp.mapAuthTypeDefault())
                        .bindAuthTypeToEnums(securityProp.mapAuthTypeEnum())
                )
                .and()
                .logout(conf -> conf
                        .logoutUrl(securityProp.getLogoutUrl())
                        .clearAuthentication(true)
                        .invalidateHttpSession(true)
                        .logoutSuccessHandler(logoutOkHandler)

                )
                .sessionManagement(conf -> conf
                        .maximumSessions(securityProp.getSessionMaximum())
                        .sessionRegistry(sessionRegistry)
                        .expiredSessionStrategy(event -> {
                            HttpServletResponse response = event.getResponse();
                            ResponseHelper.writeBodyUtf8(response, securityProp.getSessionExpiredBody());
                        })
                );
        };
    }

    @Bean
    @ConditionalOnProperty(name = WarlockEnabledProp.Key$securityHttpAuth, havingValue = "true")
    @Order(OrderWarlockAuth)
    public HttpSecurityCustomizer warlockAuthHttpSecurityConfigure() {
        return http -> http.authorizeRequests(conf -> {
            // 1
            final Set<String> permed = validValue(securityProp.getPermitAll().values());
            logger.info("Wings conf HttpSecurity, bind PermitAll=" + String.join("\n,", permed));
            conf.antMatchers(permed.toArray(Null.StrArr)).permitAll();

            // 2
            final Set<String> authed = validValue(securityProp.getAuthenticated().values());
            logger.info("Wings conf HttpSecurity, bind Authenticated=" + String.join("\n,", authed));
            conf.antMatchers(authed.toArray(Null.StrArr)).authenticated();

            // 3.group
            final TreeMap<String, Set<String>> urlPerm = new TreeMap<>();
            for (Map.Entry<String, Set<String>> en : securityProp.getAuthority().entrySet()) {
                final String perm = en.getKey();
                for (String url : en.getValue()) {
                    if (validValue(url)) {
                        final Set<String> st = urlPerm.computeIfAbsent(url, k -> new HashSet<>());
                        st.add(perm);
                    }
                }
            }
            // 3.desc
            for (Map.Entry<String, Set<String>> en : urlPerm.descendingMap().entrySet()) {
                final String url = en.getKey();
                final Set<String> pms = validValue(en.getValue());
                logger.info("Wings conf HttpSecurity, bind url=" + url + ", any-permit=[" + String.join(",", pms) + "]");
                conf.antMatchers(url).hasAnyAuthority(pms.toArray(Null.StrArr));
            }
        });
    }


    @Bean
    @ConditionalOnProperty(name = WarlockEnabledProp.Key$securityHttpBase, havingValue = "true")
    @Order(OrderWarlockBase)
    public HttpSecurityCustomizer warlockBaseHttpSecurityConfigure() {
        return HttpSecurity::httpBasic;
    }

    @Bean
    @ConditionalOnProperty(name = WarlockEnabledProp.Key$securityHttpAuto, havingValue = "true")
    @Order(OrderWarlockAuto)
    public HttpSecurityCustomizer warlockAutoHttpSecurityConfigure(
            ObjectProvider<CsrfTokenRepository> csrf,
            ObjectProvider<RequestCache> cache) {
        return http -> {
            // cors
            http.cors().configurationSource(WingsHttpPermitConfigurer.corsPermitAll());

            // cache
            final RequestCache rc = cache.getIfAvailable();
            if (rc == null) {
                http.requestCache().disable();
                logger.info("Wings conf HttpSecurity, requestCache disable");
            }
            else {
                http.requestCache().requestCache(rc);
                logger.info("Wings conf HttpSecurity, requestCache " + rc.getClass().getName());
            }

            // csrf
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
