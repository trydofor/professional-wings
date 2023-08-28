package pro.fessional.wings.slardar.spring.help;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import pro.fessional.wings.slardar.security.WingsUserDetailsService;
import pro.fessional.wings.slardar.spring.conf.WingsBindAuthnConfigurer;
import pro.fessional.wings.slardar.spring.conf.WingsBindLoginConfigurer;
import pro.fessional.wings.slardar.spring.conf.WingsHttpPermitConfigurer;

import java.util.Collection;

/**
 * <pre>
 * antMatcher("/users/**") matches any path starting with /users
 * antMatchers("/users") matches only the exact /users URL
 * mvcMatchers("/users") matches /users, /users/, /users.html
 * </pre>
 *
 * @author trydofor
 * @since 2020-08-10
 */
public class SecurityConfigHelper {


    /**
     * <pre>
     * protected void configure(HttpSecurity http) throws Exception {
     *     http
     *         .apply(wingsPermit())
     *             .permitAllCors(true)
     *             .and()
     *         ...;
     * }
     * </pre>
     */
    public static HttpHelper http() {
        return new HttpHelper();
    }

    public static class HttpHelper extends AbstractHttpConfigurer<HttpHelper, HttpSecurity> {
        @Contract("_->this")
        public HttpHelper httpPermit(Customizer<WingsHttpPermitConfigurer> customizer) throws Exception {
            final WingsHttpPermitConfigurer conf = getBuilder().apply(new WingsHttpPermitConfigurer());
            customizer.customize(conf);
            return this;
        }

        @Contract("_->this")
        public HttpHelper bindLogin(Customizer<WingsBindLoginConfigurer> customizer) throws Exception {
            final WingsBindLoginConfigurer conf = getBuilder().apply(new WingsBindLoginConfigurer());
            customizer.customize(conf);
            return this;
        }
    }

    @NotNull
    public static WingsBindAuthnConfigurer<WingsUserDetailsService> auth() {
        return new WingsBindAuthnConfigurer<>();
    }

    /**
     * <a href="https://spring.io/security/cve-2023-34035">CVE-2023-34035: Authorization rules can be misconfigured when using multiple servlets</a>
     */
    @NotNull
    public static RequestMatcher[] requestMatchers(@Nullable MvcRequestMatcher.Builder mvcMatcher, String... path) {
        RequestMatcher[] matchers = new RequestMatcher[path.length];
        for (int index = 0; index < path.length; index++) {
            String p = path[index];
            matchers[index] = mvcMatcher == null ? new AntPathRequestMatcher(p, null) : mvcMatcher.pattern(p);
        }
        return matchers;
    }

    /**
     * <a href="https://spring.io/security/cve-2023-34035">CVE-2023-34035: Authorization rules can be misconfigured when using multiple servlets</a>
     */
    @NotNull
    public static RequestMatcher[] requestMatchers(@Nullable MvcRequestMatcher.Builder mvcMatcher, Collection<String> path) {
        RequestMatcher[] matchers = new RequestMatcher[path.size()];
        int index = 0;
        for (String p : path) {
            matchers[index++] = mvcMatcher == null ? new AntPathRequestMatcher(p, null) : mvcMatcher.pattern(p);
        }
        return matchers;
    }


    // ////
    public static CorsConfigurationSource corsPermitAll() {
        return request -> {
            CorsConfiguration conf = new CorsConfiguration();
            conf.addAllowedHeader("*");
            conf.addAllowedOrigin("*");
            conf.addAllowedMethod("*");
            conf.setMaxAge(1800L);
            return conf;
        };
    }

    @NotNull
    public static String[] oauth2AntPaths() {
        return new String[]{"/oauth/**", "/error"};
    }

    @NotNull
    public static String[] testAntPaths() {
        return new String[]{"/test/**"};
    }

    @NotNull
    public static String[] loginAntPaths() {
        return new String[]{"/login", "/login/**", "/logout"};
    }

    @NotNull
    public static String[] swaggerAntPaths() {
        return new String[]{"/swagger*/**", "/webjars/**"};
    }

    public static void prefixMvc(String mvc, String[] path) {
        if (mvc == null || mvc.isBlank()) return;

        if (mvc.endsWith("/")) {
            mvc = mvc.substring(0, mvc.lastIndexOf('/'));
        }

        for (int i = 0; i < path.length; i++) {
            path[i] = mvc + path[i];
        }
    }
}
