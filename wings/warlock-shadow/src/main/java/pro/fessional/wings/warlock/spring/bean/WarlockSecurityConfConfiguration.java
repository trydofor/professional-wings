package pro.fessional.wings.warlock.spring.bean;

import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.RequestCacheConfigurer;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.StringUtils;
import pro.fessional.mirana.data.Null;
import pro.fessional.wings.silencer.runner.ApplicationRunnerOrdered;
import pro.fessional.wings.silencer.spring.WingsOrdered;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;
import pro.fessional.wings.silencer.spring.help.CommonPropHelper;
import pro.fessional.wings.slardar.security.WingsAuthDetailsSource;
import pro.fessional.wings.slardar.servlet.request.FakeHttpServletRequest;
import pro.fessional.wings.slardar.servlet.response.ResponseHelper;
import pro.fessional.wings.slardar.spring.conf.WingsBindLoginConfigurer;
import pro.fessional.wings.slardar.spring.help.SecurityConfigHelper;
import pro.fessional.wings.slardar.spring.help.SecurityConfigHelper.MatcherHelper;
import pro.fessional.wings.warlock.spring.conf.HttpSecurityCustomizer;
import pro.fessional.wings.warlock.spring.prop.WarlockEnabledProp;
import pro.fessional.wings.warlock.spring.prop.WarlockSecurityProp;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicReference;


/**
 * @author trydofor
 * @since 2019-12-01
 */
@Configuration(proxyBeanMethods = false)
@ConditionalWingsEnabled
public class WarlockSecurityConfConfiguration {

    private final static Log log = LogFactory.getLog(WarlockSecurityConfConfiguration.class);

    @Bean
    @ConditionalWingsEnabled(abs = WarlockEnabledProp.Key$secWebAuto)
    public WebSecurityCustomizer warlockWebCustomizer(WarlockSecurityProp securityProp, ObjectProvider<HttpFirewall> httpFirewall) {
        log.info("WarlockShadow spring-bean warlockWebCustomizer");
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
                web.ignoring().requestMatchers(ignores.toArray(String[]::new));
            }

            final HttpFirewall firewall = httpFirewall.getIfAvailable();
            if (firewall != null) {
                log.info("WarlockShadow conf WebSecurity, httpFirewall=" + firewall.getClass());
                web.httpFirewall(firewall);
            }
        };
    }

    @Bean
    @ConditionalWingsEnabled(abs = WarlockEnabledProp.Key$secHttpBind)
    @Order(WingsOrdered.Lv4Application + 200)
    public HttpSecurityCustomizer warlockSecurityBindHttpConfigure(
            WarlockSecurityProp securityProp,
            SessionRegistry sessionRegistry,
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

            http.with(new WingsBindLoginConfigurer(), conf -> {
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
                )
                .anonymous(conf -> {
                    if (!securityProp.isAnonymous()) {
                        log.info("WarlockShadow conf HttpSecurity, disable anonymous");
                        conf.disable();
                    }
                });

            final AccessDeniedHandler deniedHandler = accessDeniedHandler.getIfAvailable();
            if (deniedHandler != null) {
                log.info("WarlockShadow conf exceptionHandling, accessDeniedHandler=" + deniedHandler.getClass());
                http.exceptionHandling(c -> c.accessDeniedHandler(deniedHandler));
            }
        };
    }

    @Bean
    @ConditionalWingsEnabled(abs = WarlockEnabledProp.Key$secHttpAuth)
    @Order(WingsOrdered.Lv4Application + 300)
    public HttpSecurityCustomizer warlockSecurityAuthHttpConfigure(WarlockSecurityProp securityProp) {
        log.info("WarlockShadow spring-bean warlockSecurityAuthHttpConfigure");

        return http -> http.authorizeHttpRequests(conf -> {
            // 1 PermitAll
            final Set<String> permed = CommonPropHelper.onlyValue(securityProp.getPermitAll().values());
            if (!permed.isEmpty()) {
                log.info("WarlockShadow conf HttpSecurity, bind PermitAll=" + String.join("\n,", permed));
                conf.requestMatchers(permed.toArray(String[]::new)).permitAll();
            }

            // 2 Authenticated
            final Set<String> authed = CommonPropHelper.onlyValue(securityProp.getAuthenticated().values());
            if (!authed.isEmpty()) {
                log.info("WarlockShadow conf HttpSecurity, bind Authenticated=" + String.join("\n,", authed));
                conf.requestMatchers(authed.toArray(String[]::new)).authenticated();
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
                    conf.requestMatchers(url).hasAnyAuthority(pms.toArray(Null.StrArr));
                }
            }
        });
    }

    @Bean
    @ConditionalWingsEnabled(abs = WarlockEnabledProp.Key$secHttpBase)
    @Order(WingsOrdered.Lv4Application + 100)
    public HttpSecurityCustomizer warlockSecurityHttpBaseConfigure() {
        log.info("WarlockShadow spring-bean warlockSecurityHttpBaseConfigure");
        return http -> http.httpBasic(c -> {});
    }

    @Bean
    @ConditionalWingsEnabled(abs = WarlockEnabledProp.Key$secHttpAuto)
    @Order(WingsOrdered.Lv4Application + 400)
    public HttpSecurityCustomizer warlockSecurityAutoHttpConfigure(
            ObjectProvider<CsrfTokenRepository> csrf,
            ObjectProvider<RequestCache> cache) {
        log.info("WarlockShadow spring-bean warlockSecurityAutoHttpConfigure");
        return http -> {
            // cors
            http.cors(c -> c.configurationSource(SecurityConfigHelper.corsPermitAll()));

            // cache
            final RequestCache rc = cache.getIfAvailable();
            if (rc == null) {
                http.requestCache(RequestCacheConfigurer::disable);
                log.info("WarlockShadow conf HttpSecurity, requestCache disable");
            }
            else {
                http.requestCache(c -> c.requestCache(rc));
                log.info("WarlockShadow conf HttpSecurity, requestCache " + rc.getClass().getName());
            }

            // csrf
            final CsrfTokenRepository ct = csrf.getIfAvailable();
            if (ct == null) {
                http.csrf(AbstractHttpConfigurer::disable);
                log.info("WarlockShadow conf HttpSecurity, csrf disable");
            }
            else {
                http.csrf(c -> c.csrfTokenRepository(ct));
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
    @ConditionalWingsEnabled(abs = WarlockEnabledProp.Key$secHttpChain)
    @Order(WingsOrdered.Lv4Application + 900)
    public SecurityFilterChain securityFilterChain(WarlockSecurityProp securityProp, HttpSecurity http, Map<String, HttpSecurityCustomizer> configures) throws Exception {
        log.info("WarlockShadow conf securityFilterChain, begin");
        for (Map.Entry<String, HttpSecurityCustomizer> en : configures.entrySet()) {
            log.info("WarlockShadow conf securityFilterChain, bean=" + en.getKey());
            en.getValue().customize(http);
        }

        final String anyRequest = securityProp.getAnyRequest();
        if (StringUtils.hasText(anyRequest)) {
            log.info("WarlockShadow conf securityFilterChain, anyRequest=" + anyRequest);
            String str = anyRequest.trim();
            if (!StringUtils.hasText(str) || "permitAll".equalsIgnoreCase(str)) {
                http.authorizeHttpRequests(c -> c.anyRequest().permitAll());
            }
            else if ("authenticated".equalsIgnoreCase(str)) {
                http.authorizeHttpRequests(c -> c.anyRequest().authenticated());
            }
            else if ("anonymous".equalsIgnoreCase(str)) {
                http.authorizeHttpRequests(c -> c.anyRequest().anonymous());
            }
            else if ("fullyAuthenticated".equalsIgnoreCase(str)) {
                http.authorizeHttpRequests(c -> c.anyRequest().fullyAuthenticated());
            }
            else {
                http.authorizeHttpRequests(c -> c.anyRequest().hasAnyAuthority(str.split("[, \t\r\n]+")));
            }
        }
        log.info("WarlockShadow conf securityFilterChain, done");
        return http.build();
    }


    @Bean
    @ConditionalWingsEnabled(abs = WarlockEnabledProp.Key$secCheckUrl)
    public ApplicationRunnerOrdered securityCheckUrlRunner(WarlockSecurityProp securityProp, ApplicationContext ctx) {
        log.info("WarlockShadow spring-runs securityCheckUrlRunner");
        return new ApplicationRunnerOrdered(WingsOrdered.Lv1Config, ignored -> {
            log.info("WarlockShadow check security url config");
            Map<String, String> matchers = new LinkedHashMap<>();
            Map<String, FakeHttpServletRequest> requests = new LinkedHashMap<>();

            for (var en : securityProp.getWebIgnore().entrySet()) {
                String ptn = en.getValue();
                if (!StringUtils.hasText(ptn)) continue;
                matchers.put("WebIgnore:" + en.getKey(), ptn);
                requests.put(ptn, SecurityConfigHelper.fakeMatcherRequest(ptn));
            }
            for (var en : securityProp.getPermitAll().entrySet()) {
                String ptn = en.getValue();
                if (!StringUtils.hasText(ptn)) continue;
                matchers.put("PermitAll:" + en.getKey(), ptn);
                requests.put(ptn, SecurityConfigHelper.fakeMatcherRequest(ptn));
            }
            for (var en : securityProp.getAuthenticated().entrySet()) {
                String ptn = en.getValue();
                if (!StringUtils.hasText(ptn)) continue;
                matchers.put("Authenticated:" + en.getKey(), ptn);
                requests.put(ptn, SecurityConfigHelper.fakeMatcherRequest(ptn));
            }
            for (var en : securityProp.getAuthority().entrySet()) {
                int c = 0;
                String k = en.getKey();
                for (String ptn : en.getValue()) {
                    if (!StringUtils.hasText(ptn)) continue;
                    matchers.put("Authority:" + k + "[" + (c++) + "]", ptn);
                    requests.put(ptn, SecurityConfigHelper.fakeMatcherRequest(ptn));
                }
            }
            final AtomicReference<RequestMatcher> opt = new AtomicReference<>();
            MatcherHelper matcherHelper = MatcherHelper.of(ctx, opt);

            // check including
            for (var en : matchers.entrySet()) {
                String ptn = en.getValue();
                requests.remove(ptn);
                if (requests.isEmpty()) break;

                matcherHelper.requestMatchers(ptn);
                RequestMatcher mt = opt.get();
                for (var er : requests.entrySet()) {
                    if (mt.matches(er.getValue())) {
                        log.warn(en.getKey() + "=" + ptn + " should not contain " + er.getKey());
                    }
                }
            }
            matchers.clear();
            requests.clear();

            // check auth url
            String loginPage = securityProp.getLoginPage();
            if (StringUtils.hasText(loginPage)) {
                requests.put(loginPage, SecurityConfigHelper.fakeMatcherRequest(loginPage));
            }
            String logoutUrl = securityProp.getLogoutUrl();
            if (StringUtils.hasText(logoutUrl)) {
                requests.put(logoutUrl, SecurityConfigHelper.fakeMatcherRequest(logoutUrl));
            }
            String loginProcUrl = securityProp.getLoginProcUrl();
            if (StringUtils.hasText(loginProcUrl)) {
                requests.put(loginProcUrl, SecurityConfigHelper.fakeMatcherRequest(loginProcUrl));
            }

            StringBuilder err = new StringBuilder();
            for (var en : securityProp.getWebIgnore().entrySet()) {
                String ptn = en.getValue();
                if (!StringUtils.hasText(ptn)) continue;
                matcherHelper.requestMatchers(ptn);
                RequestMatcher mt = opt.get();
                for (var e : requests.entrySet()) {
                    if (mt.matches(e.getValue())) {
                        err.append("\nWebIgnore:").append(en.getKey()).append(" should exclude ").append(e.getKey());
                    }
                }
            }

            String anyRequest = securityProp.getAnyRequest();
            if (!StringUtils.hasText(anyRequest)
                || "permitAll".equalsIgnoreCase(anyRequest)
                || "anonymous".equalsIgnoreCase(anyRequest)) {
                for (var en : securityProp.getPermitAll().entrySet()) {
                    String ptn = en.getValue();
                    if (!StringUtils.hasText(ptn)) continue;
                    if (requests.isEmpty()) break;

                    matcherHelper.requestMatchers(ptn);
                    RequestMatcher mt = opt.get();
                    for (var it = requests.entrySet().iterator(); it.hasNext(); ) {
                        var er = it.next();
                        if (mt.matches(er.getValue())) {
                            log.debug("WarlockShadow security url permit all include " + er.getKey());
                            it.remove();
                        }
                    }
                }
                if (!requests.isEmpty()) {
                    err.append("\nPermitAll should include urls: ").append(String.join(", ", requests.keySet()));
                }
            }

            if (!err.isEmpty()) {
                String msg = err.toString();
                log.error(msg);
                throw new IllegalStateException(
                        "\nWarlockSecurityConfConfiguration has security url conflict to fix." +
                        "\nor disable checking by `wings.enabled.warlock.sec-check-url=false`" +
                        msg);
            }
        });
    }
}
