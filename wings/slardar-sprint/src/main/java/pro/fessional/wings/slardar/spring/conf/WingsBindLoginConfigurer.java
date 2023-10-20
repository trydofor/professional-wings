package pro.fessional.wings.slardar.spring.conf;

import jakarta.servlet.http.HttpServletRequest;
import org.jetbrains.annotations.Contract;
import org.springframework.context.ApplicationContext;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.web.authentication.ForwardAuthenticationFailureHandler;
import org.springframework.security.web.authentication.ForwardAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.ui.DefaultLoginPageGeneratingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import pro.fessional.wings.slardar.security.WingsAuthDetailsSource;
import pro.fessional.wings.slardar.security.WingsAuthTypeParser;
import pro.fessional.wings.slardar.security.WingsAuthTypeSource;
import pro.fessional.wings.slardar.security.bind.WingsBindAuthFilter;
import pro.fessional.wings.slardar.security.impl.DefaultWingsAuthTypeParser;
import pro.fessional.wings.slardar.security.impl.DefaultWingsAuthTypeSource;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Same as FormLoginConfigurer (for can not extend `final`)
 *
 * @author trydofor
 * @since 2021-02-07
 */
public class WingsBindLoginConfigurer extends
        AbstractAuthenticationFilterConfigurer<HttpSecurity, WingsBindLoginConfigurer, WingsBindAuthFilter> {

    public WingsBindLoginConfigurer() {
        super(new WingsBindAuthFilter(), null);
        usernameParameter("username");
        passwordParameter("password");
    }

    @Override
    public WingsBindLoginConfigurer loginPage(String loginPage) {
        return super.loginPage(loginPage);
    }

    @Contract("_->this")
    public WingsBindLoginConfigurer loginForward(boolean forward) {
        ((LoginUrlAuthenticationEntryPoint) getAuthenticationEntryPoint()).setUseForward(forward);
        return this;
    }

    @Contract("_->this")
    public WingsBindLoginConfigurer usernameParameter(String usernameParameter) {
        getAuthenticationFilter().setUsernameParameter(usernameParameter);
        return this;
    }

    @Contract("_->this")
    public WingsBindLoginConfigurer passwordParameter(String passwordParameter) {
        getAuthenticationFilter().setPasswordParameter(passwordParameter);
        return this;
    }

    @Contract("_->this")
    public WingsBindLoginConfigurer failureForwardUrl(String forwardUrl) {
        failureHandler(new ForwardAuthenticationFailureHandler(forwardUrl));
        return this;
    }

    @Contract("_->this")
    public WingsBindLoginConfigurer successForwardUrl(String forwardUrl) {
        successHandler(new ForwardAuthenticationSuccessHandler(forwardUrl));
        return this;
    }

    private String loginProcessingUrl = null;
    private Collection<String> loginProcessingMethod = Collections.emptyList();
    private WingsAuthTypeSource wingsAuthTypeSource = null;
    private WingsAuthDetailsSource<?> wingsAuthDetailsSource = null;
    private Enum<?> defaultAuthType = null;
    private final Map<String, Enum<?>> authTypes = new HashMap<>();

    @Contract("_,_->this")
    public WingsBindLoginConfigurer bindAuthTypeToEnums(String type, Enum<?> authType) {
        this.authTypes.put(type, authType);
        return this;
    }

    @Contract("_->this")
    public WingsBindLoginConfigurer bindAuthTypeToEnums(Map<String, Enum<?>> authType) {
        this.authTypes.putAll(authType);
        return this;
    }

    @Contract("_->this")
    public WingsBindLoginConfigurer bindAuthTypeDefault(Enum<?> authType) {
        this.defaultAuthType = authType;
        return this;
    }

    @Contract("_->this")
    public WingsBindLoginConfigurer bindAuthTypeSource(WingsAuthTypeSource wingsAuthTypeSource) {
        this.wingsAuthTypeSource = wingsAuthTypeSource;
        return this;
    }

    @Contract("_->this")
    public WingsBindLoginConfigurer bindAuthDetailsSource(WingsAuthDetailsSource<?> wingsAuthDetailsSource) {
        this.wingsAuthDetailsSource = wingsAuthDetailsSource;
        return this;
    }

    @Override
    public WingsBindLoginConfigurer loginProcessingUrl(String loginProcessingUrl) {
        return loginProcessingUrl(loginProcessingUrl, Collections.singleton("POST"));
    }

    public WingsBindLoginConfigurer loginProcessingUrl(String loginProcessingUrl, Collection<String> method) {
        this.loginProcessingUrl = loginProcessingUrl;
        this.loginProcessingMethod = method;
        return super.loginProcessingUrl(loginProcessingUrl);
    }

    @Override
    protected RequestMatcher createLoginProcessingUrlMatcher(String loginProcessingUrl) {
        if (loginProcessingMethod == null || loginProcessingMethod.isEmpty()) {
            return new AntPathRequestMatcher(loginProcessingUrl);
        }
        else if (loginProcessingMethod.size() == 1) {
            return new AntPathRequestMatcher(loginProcessingUrl, loginProcessingMethod.iterator().next());
        }
        else {
            return new RequestMatcher() {
                private final AntPathRequestMatcher matcher = new AntPathRequestMatcher(loginProcessingUrl);
                private final Set<String> methods = new HashSet<>(loginProcessingMethod);

                @Override
                public boolean matches(HttpServletRequest request) {
                    final boolean matches = matcher.matches(request);
                    if (!matches) return false;

                    final String method = request.getMethod();
                    for (String s : methods) {
                        if (s.equalsIgnoreCase(method)) return true;
                    }

                    return false;
                }
            };
        }
    }

    @Override
    public void init(HttpSecurity http) throws Exception {
        super.init(http);
        initDefaultLoginFilter(http);
        final ApplicationContext context = getBuilder().getSharedObject(ApplicationContext.class);

        if (context == null) return;
        initBindAuthTypeSource(context);
    }

    public LoginUrlAuthenticationEntryPoint getLoginUrlAuthenticationEntryPoint() {
        return (LoginUrlAuthenticationEntryPoint) getAuthenticationEntryPoint();
    }

    //
    private void initBindAuthTypeSource(ApplicationContext context) {
        WingsAuthTypeParser parser = null;
        if (wingsAuthTypeSource == null) {
            if (authTypes.isEmpty()) {
                wingsAuthTypeSource = context.getBeanProvider(WingsAuthTypeSource.class).getIfAvailable();
            }
            else {
                if (defaultAuthType == null && authTypes.size() == 1) {
                    defaultAuthType = authTypes.values().iterator().next();
                }
                parser = new DefaultWingsAuthTypeParser(defaultAuthType, authTypes);
            }
        }

        if (wingsAuthTypeSource == null) {
            if (parser == null) {
                parser = context.getBeanProvider(WingsAuthTypeParser.class).getIfAvailable();
            }
            if (parser != null) {
                wingsAuthTypeSource = new DefaultWingsAuthTypeSource(loginProcessingUrl, parser);
            }
        }

        if (wingsAuthTypeSource != null) {
            getAuthenticationFilter().setWingsBindAuthTypeSource(wingsAuthTypeSource);
        }

        //
        if (wingsAuthDetailsSource == null) {
            wingsAuthDetailsSource = context.getBeanProvider(WingsAuthDetailsSource.class).getIfAvailable();
        }
        if (wingsAuthDetailsSource != null) {
            getAuthenticationFilter().setAuthenticationDetailsSource(wingsAuthDetailsSource);
        }
    }

    private void initDefaultLoginFilter(HttpSecurity http) {
        DefaultLoginPageGeneratingFilter loginPageGeneratingFilter = http.getSharedObject(DefaultLoginPageGeneratingFilter.class);
        if (loginPageGeneratingFilter != null && !isCustomLoginPage()) {
            loginPageGeneratingFilter.setFormLoginEnabled(true);
            loginPageGeneratingFilter.setUsernameParameter(getAuthenticationFilter().getUsernameParameter());
            loginPageGeneratingFilter.setPasswordParameter(getAuthenticationFilter().getPasswordParameter());
            loginPageGeneratingFilter.setLoginPageUrl(getLoginPage());
            loginPageGeneratingFilter.setFailureUrl(getFailureUrl());
            loginPageGeneratingFilter.setAuthenticationUrl(getLoginProcessingUrl());
        }
    }
}
