package pro.fessional.wings.slardar.spring.help;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationContext;
import org.springframework.security.config.annotation.web.AbstractRequestMatcherRegistry;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.util.pattern.PathPattern;
import pro.fessional.wings.slardar.security.WingsUserDetailsService;
import pro.fessional.wings.slardar.servlet.request.FakeHttpServletRequest;
import pro.fessional.wings.slardar.spring.conf.WingsBindAuthnConfigurer;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

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

    public static class MatcherHelper extends AbstractRequestMatcherRegistry<MatcherHelper> {
        private final Consumer<List<RequestMatcher>> matchersConsumer;

        public MatcherHelper(ApplicationContext context, Consumer<List<RequestMatcher>> matchersConsumer) {
            this.matchersConsumer = matchersConsumer;
            setApplicationContext(context);
        }

        @Override
        protected MatcherHelper chainRequestMatchers(List<RequestMatcher> requestMatchers) {
            matchersConsumer.accept(requestMatchers);
            return this;
        }

        public static MatcherHelper of(ApplicationContext context, AtomicReference<RequestMatcher> ref) {
            return new MatcherHelper(context, it -> ref.set(it.get(0)));
        }

        public static MatcherHelper of(ApplicationContext context, RequestMatcher[] ref) {
            return new MatcherHelper(context, it -> it.toArray(ref));
        }
    }

    @NotNull
    public static WingsBindAuthnConfigurer<WingsUserDetailsService> auth() {
        return new WingsBindAuthnConfigurer<>();
    }

    /**
     * encode PathPattern token to url
     *
     * @see PathPattern
     */
    @NotNull
    public static String encodePathPattern(@NotNull String path) {
        path = URLEncoder.encode(path, StandardCharsets.UTF_8);
        path = path.replace("%2F", "/");
        return path;
    }

    /**
     * fake the HttpServletRequest to test matcher
     */
    @NotNull
    public static FakeHttpServletRequest fakeMatcherRequest(@NotNull String path) {
        path = encodePathPattern(path);
        FakeHttpServletRequest request = new FakeHttpServletRequest();
        request.setPathInfo(path);
        request.setRequestURI(path);
        return request;
    }

    /**
     * fake the HttpServletRequest with servletName to test matcher.
     * see <a href="https://github.com/trydofor/professional-wings/issues/226">Failed to find servlet xx in the servlet context</a>
     */
    @NotNull
    public static FakeHttpServletRequest fakeMatcherRequest(@NotNull String path, String servletName) {
        path = encodePathPattern(path);
        FakeHttpServletRequest request = new FakeHttpServletRequest();
        request.setPathInfo(path);
        request.setRequestURI(path);
        if (servletName != null) {
            request.getHttpServletMapping().setServletName(servletName);
        }
        return request;
    }

    // ////
    @NotNull
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
        return new String[]{ "/oauth/**", "/error" };
    }

    @NotNull
    public static String[] testAntPaths() {
        return new String[]{ "/test/**" };
    }

    @NotNull
    public static String[] loginAntPaths() {
        return new String[]{ "/login", "/login/**", "/logout" };
    }

    @NotNull
    public static String[] swaggerAntPaths() {
        return new String[]{ "/swagger*/**", "/webjars/**" };
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
