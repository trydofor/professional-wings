package pro.fessional.wings.warlock.spring.bean;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.util.StringUtils;
import pro.fessional.mirana.data.Null;
import pro.fessional.wings.silencer.spring.help.CommonPropHelper;
import pro.fessional.wings.slardar.security.WingsAuthDetailsSource;
import pro.fessional.wings.slardar.servlet.response.ResponseHelper;
import pro.fessional.wings.slardar.spring.help.SecurityConfigHelper;
import pro.fessional.wings.spring.consts.OrderedWarlockConst;
import pro.fessional.wings.warlock.spring.conf.HttpSecurityCustomizer;
import pro.fessional.wings.warlock.spring.prop.WarlockEnabledProp;
import pro.fessional.wings.warlock.spring.prop.WarlockSecurityProp;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;


/**
 * @author trydofor
 * @since 2019-12-01
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = WarlockEnabledProp.Key$securityAuto, havingValue = "true")
@RequiredArgsConstructor
@AutoConfigureOrder(OrderedWarlockConst.SecurityConfConfiguration)
public class WarlockSecurityConfConfiguration {

    private final static Log log = LogFactory.getLog(WarlockSecurityConfConfiguration.class);

    private final SessionRegistry sessionRegistry;
    private final WarlockSecurityProp securityProp;

    @Bean
    @ConditionalOnProperty(name = WarlockEnabledProp.Key$securityWebAutos, havingValue = "true")
    public WebSecurityCustomizer warlockWebCustomizer(ObjectProvider<HttpFirewall> httpFirewall, ObjectProvider<MvcRequestMatcher.Builder> mvcMatcher) {
        log.info("WarlockShadow spring-bean warlockWebCustomizer");
        MvcRequestMatcher.Builder mvc = mvcMatcher.getIfAvailable();
        return web -> {
            if (securityProp.isWebDebug()) {
                log.info("WarlockShadow conf WebSecurity, WebDebug=true");
                web.debug(true);
            }

            // You are asking Spring Security to ignore Ant . This is not recommended
            // https://github.com/spring-projects/spring-security/issues/10938
            final Map<String, String> webIgnore = securityProp.getWebIgnore();
            if (!webIgnore.isEmpty()) {
                final Set<String> ignores = CommonPropHelper.onlyValue(webIgnore.values());
                log.info("WarlockShadow conf WebSecurity, ignoring=" + String.join("\n,", ignores));
                web.ignoring().requestMatchers(SecurityConfigHelper.requestMatchers(mvc, ignores));
            }

            final HttpFirewall firewall = httpFirewall.getIfAvailable();
            if (firewall != null) {
                log.info("WarlockShadow conf WebSecurity, httpFirewall=" + firewall.getClass());
                web.httpFirewall(firewall);
            }
        };
    }

    @Bean
    @ConditionalOnProperty(name = WarlockEnabledProp.Key$securityHttpBind, havingValue = "true")
    @Order(OrderedWarlockConst.SecurityBindHttp)
    public HttpSecurityCustomizer warlockSecurityBindHttpConfigure(
            ObjectProvider<AuthenticationSuccessHandler> authenticationSuccessHandler,
            ObjectProvider<AuthenticationFailureHandler> authenticationFailureHandler,
            ObjectProvider<WingsAuthDetailsSource<?>> wingsAuthDetailsSource,
            ObjectProvider<LogoutSuccessHandler> logoutSuccessHandler,
            ObjectProvider<AccessDeniedHandler> accessDeniedHandler
    ) {
        log.info("WarlockShadow spring-bean warlockSecurityBindHttpConfigure");
        return http -> {
            final AuthenticationSuccessHandler authOkHandler = authenticationSuccessHandler.getIfAvailable();
            final AuthenticationFailureHandler authNgHandler = authenticationFailureHandler.getIfAvailable();
            final WingsAuthDetailsSource<?> authDetailSource = wingsAuthDetailsSource.getIfAvailable();
            final LogoutSuccessHandler logoutOkHandler = logoutSuccessHandler.getIfAvailable();
            log.info("WarlockShadow conf HttpSecurity, authenticationDetailsSource=" + (authDetailSource == null ? "null" : authDetailSource.getClass()));

            http.apply(SecurityConfigHelper.http())
                .bindLogin(conf -> {
                            conf.loginPage(securityProp.getLoginPage()) // init authenticationEntryPoint, 401 page
                                // init filter.RequestMatcher
                                .loginProcessingUrl(securityProp.getLoginProcUrl(), securityProp.getLoginProcMethod()) // by filter,no controller
                                .loginForward(securityProp.isLoginForward()) // forward or redirect
                                .usernameParameter(securityProp.getUsernamePara())
                                .passwordParameter(securityProp.getPasswordPara())
                                .authenticationDetailsSource(authDetailSource)
                                .bindAuthTypeDefault(securityProp.mapAuthTypeDefault())
                                .bindAuthTypeToEnums(securityProp.mapAuthTypeEnum());

                            if (authOkHandler != null) {
                                log.info("WarlockShadow conf HttpSecurity, successHandler=" + authOkHandler.getClass());
                                conf.successHandler(authOkHandler);
                            }
                            if (authNgHandler != null) {
                                log.info("WarlockShadow conf HttpSecurity, failureHandler=" + authNgHandler.getClass());
                                conf.failureHandler(authNgHandler);
                            }
                        }
                )
                .and()
                .logout(conf -> {
                            conf.logoutUrl(securityProp.getLogoutUrl())
                                .clearAuthentication(true)
                                .invalidateHttpSession(true);

                            if (logoutOkHandler != null) {
                                log.info("WarlockShadow conf HttpSecurity, logoutSuccessHandler=" + logoutOkHandler.getClass());
                                conf.logoutSuccessHandler(logoutOkHandler);
                            }
                        }
                )
                .sessionManagement(conf -> conf
                        .maximumSessions(securityProp.getSessionMaximum())
                        .sessionRegistry(sessionRegistry)
                        .expiredSessionStrategy(event -> {
                            HttpServletResponse response = event.getResponse();
                            ResponseHelper.writeBodyUtf8(response, securityProp.getSessionExpiredBody());
                        })
                );

            final AccessDeniedHandler deniedHandler = accessDeniedHandler.getIfAvailable();
            if(deniedHandler != null){
                log.info("WarlockShadow conf exceptionHandling, accessDeniedHandler=" + deniedHandler.getClass());
                http.exceptionHandling().accessDeniedHandler(deniedHandler);
            }
        };
    }

    @Bean
    @ConditionalOnProperty(name = WarlockEnabledProp.Key$securityHttpAuth, havingValue = "true")
    @Order(OrderedWarlockConst.SecurityAuthHttp)
    public HttpSecurityCustomizer warlockSecurityAuthHttpConfigure(ObjectProvider<MvcRequestMatcher.Builder> mvcMatcher) {
        log.info("WarlockShadow spring-bean warlockSecurityAuthHttpConfigure");
        MvcRequestMatcher.Builder mvc = mvcMatcher.getIfAvailable();
        return http -> {
            val conf = http.authorizeHttpRequests();
            // 1 PermitAll
            final Set<String> permed = CommonPropHelper.onlyValue(securityProp.getPermitAll().values());
            if (!permed.isEmpty()) {
                log.info("WarlockShadow conf HttpSecurity, bind PermitAll=" + String.join("\n,", permed));
                conf.requestMatchers(SecurityConfigHelper.requestMatchers(mvc, permed)).permitAll();
            }

            // 2 Authenticated
            final Set<String> authed = CommonPropHelper.onlyValue(securityProp.getAuthenticated().values());
            if (!authed.isEmpty()) {
                log.info("WarlockShadow conf HttpSecurity, bind Authenticated=" + String.join("\n,", authed));
                conf.requestMatchers(SecurityConfigHelper.requestMatchers(mvc, authed)).authenticated();
            }

            // 3 Authority
            if (!securityProp.getAuthority().isEmpty()) {
                // group
                final TreeMap<String, Set<String>> urlPerm = new TreeMap<>();
                for (Map.Entry<String, Set<String>> en : securityProp.getAuthority().entrySet()) {
                    final String perm = en.getKey();
                    for (String url : en.getValue()) {
                        if (CommonPropHelper.hasValue(url)) {
                            final Set<String> st = urlPerm.computeIfAbsent(url, k -> new HashSet<>());
                            st.add(perm);
                        }
                    }
                }
                // desc
                for (Map.Entry<String, Set<String>> en : urlPerm.descendingMap().entrySet()) {
                    final String url = en.getKey();
                    final Set<String> pms = CommonPropHelper.onlyValue(en.getValue());
                    log.info("WarlockShadow conf HttpSecurity, bind url=" + url + ", any-permit=[" + String.join(",", pms) + "]");
                    conf.requestMatchers(SecurityConfigHelper.requestMatchers(mvc, url)).hasAnyAuthority(pms.toArray(Null.StrArr));
                }
            }
        };
    }

    @Bean
    @ConditionalOnProperty(name = WarlockEnabledProp.Key$securityHttpBase, havingValue = "true")
    @Order(OrderedWarlockConst.SecurityHttpBase)
    public HttpSecurityCustomizer warlockSecurityHttpBaseConfigure() {
        log.info("WarlockShadow spring-bean warlockSecurityHttpBaseConfigure");
        return HttpSecurity::httpBasic;
    }

    @Bean
    @ConditionalOnProperty(name = WarlockEnabledProp.Key$securityHttpAuto, havingValue = "true")
    @Order(OrderedWarlockConst.SecurityAutoHttp)
    public HttpSecurityCustomizer warlockSecurityAutoHttpConfigure(
            ObjectProvider<CsrfTokenRepository> csrf,
            ObjectProvider<RequestCache> cache) {
        log.info("WarlockShadow spring-bean warlockSecurityAutoHttpConfigure");
        return http -> {
            // cors
            http.cors().configurationSource(SecurityConfigHelper.corsPermitAll());

            // cache
            final RequestCache rc = cache.getIfAvailable();
            if (rc == null) {
                http.requestCache().disable();
                log.info("WarlockShadow conf HttpSecurity, requestCache disable");
            }
            else {
                http.requestCache().requestCache(rc);
                log.info("WarlockShadow conf HttpSecurity, requestCache " + rc.getClass().getName());
            }

            // csrf
            final CsrfTokenRepository ct = csrf.getIfAvailable();
            if (ct == null) {
                http.csrf().disable();
                log.info("WarlockShadow conf HttpSecurity, csrf disable");
            }
            else {
                http.csrf().csrfTokenRepository(ct);
                log.info("WarlockShadow conf HttpSecurity, csrf " + ct.getClass().getName());
            }
        };
    }

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
    @Bean
    @ConditionalOnProperty(name = WarlockEnabledProp.Key$securityHttpChain, havingValue = "true")
    @Order(OrderedWarlockConst.SecurityFilterChain)
    public SecurityFilterChain securityFilterChain(HttpSecurity http, Map<String, HttpSecurityCustomizer> configures) throws Exception {
        log.info("WarlockShadow conf securityFilterChain, begin");
        for (Map.Entry<String, HttpSecurityCustomizer> en : configures.entrySet()) {
            log.info("WarlockShadow conf securityFilterChain, bean=" + en.getKey());
            en.getValue().customize(http);
        }

        final String anyRequest = securityProp.getAnyRequest();
        if (StringUtils.hasText(anyRequest)) {
            log.info("WarlockShadow conf securityFilterChain, anyRequest=" + anyRequest);
            String str = anyRequest.trim();
            if ("permitAll".equalsIgnoreCase(str)) {
                http.authorizeHttpRequests().anyRequest().permitAll();
            }
            else if ("authenticated".equalsIgnoreCase(str)) {
                http.authorizeHttpRequests().anyRequest().authenticated();
            }
            else if ("anonymous".equalsIgnoreCase(str)) {
                http.authorizeHttpRequests().anyRequest().anonymous();
            }
            else if ("fullyAuthenticated".equalsIgnoreCase(str)) {
                http.authorizeHttpRequests().anyRequest().fullyAuthenticated();
            }
            else {
                http.authorizeHttpRequests().anyRequest().hasAnyAuthority(str.split("[, \t\r\n]+"));
            }
        }
        log.info("WarlockShadow conf securityFilterChain, done");
        return http.build();
    }
}
