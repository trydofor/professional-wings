package pro.fessional.wings.slardar.spring.conf;

import org.springframework.context.ApplicationContext;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.web.authentication.ForwardAuthenticationFailureHandler;
import org.springframework.security.web.authentication.ForwardAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.ui.DefaultLoginPageGeneratingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import pro.fessional.wings.slardar.security.WingsAuthTypeParser;
import pro.fessional.wings.slardar.security.WingsAuthTypeSource;
import pro.fessional.wings.slardar.security.bind.WingsBindAuthFilter;
import pro.fessional.wings.slardar.security.impl.DefaultWingsAuthTypeParser;
import pro.fessional.wings.slardar.security.impl.DefaultWingsAuthTypeSource;

import java.util.HashMap;
import java.util.Map;

/**
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

    public WingsBindLoginConfigurer loginForward(boolean forward) {
        ((LoginUrlAuthenticationEntryPoint) this.getAuthenticationEntryPoint()).setUseForward(forward);
        return this;
    }

    public WingsBindLoginConfigurer usernameParameter(String usernameParameter) {
        getAuthenticationFilter().setUsernameParameter(usernameParameter);
        return this;
    }


    public WingsBindLoginConfigurer passwordParameter(String passwordParameter) {
        getAuthenticationFilter().setPasswordParameter(passwordParameter);
        return this;
    }

    public WingsBindLoginConfigurer failureForwardUrl(String forwardUrl) {
        failureHandler(new ForwardAuthenticationFailureHandler(forwardUrl));
        return this;
    }

    public WingsBindLoginConfigurer successForwardUrl(String forwardUrl) {
        successHandler(new ForwardAuthenticationSuccessHandler(forwardUrl));
        return this;
    }

    private String loginProcessingUrl = null;
    private WingsAuthTypeSource bindAuthTypeSource = null;
    private final Map<String, Enum<?>> authTypes = new HashMap<>();

    public WingsBindLoginConfigurer bindAuthTypeToEnums(String type, Enum<?> authType) {
        this.authTypes.put(type, authType);
        return this;
    }

    public WingsBindLoginConfigurer bindAuthTypeToEnums(Map<String, Enum<?>> authType) {
        this.authTypes.putAll(authType);
        return this;
    }

    public WingsBindLoginConfigurer bindAuthTypeSource(WingsAuthTypeSource bindAuthTypeSource) {
        this.bindAuthTypeSource = bindAuthTypeSource;
        return this;
    }

    @Override
    public WingsBindLoginConfigurer loginProcessingUrl(String loginProcessingUrl) {
        this.loginProcessingUrl = loginProcessingUrl;
        return super.loginProcessingUrl(loginProcessingUrl);
    }

    @Override
    protected RequestMatcher createLoginProcessingUrlMatcher(String loginProcessingUrl) {
        return new AntPathRequestMatcher(loginProcessingUrl);
    }

    @Override
    public void init(HttpSecurity http) throws Exception {
        super.init(http);
        initDefaultLoginFilter(http);
        final ApplicationContext context = getBuilder().getSharedObject(ApplicationContext.class);

        if (context == null) return;
        initBindAuthTypeSource(context);
    }

    private void initBindAuthTypeSource(ApplicationContext context) {
        WingsAuthTypeParser parser = null;
        if (bindAuthTypeSource == null) {
            if (authTypes.isEmpty()) {
                bindAuthTypeSource = context.getBeanProvider(WingsAuthTypeSource.class).getIfAvailable();
            }
            else {
                parser = new DefaultWingsAuthTypeParser(authTypes);
            }
        }

        if (bindAuthTypeSource == null) {
            if (parser == null) {
                parser = context.getBeanProvider(WingsAuthTypeParser.class).getIfAvailable();
            }
            if (parser != null) {
                bindAuthTypeSource = new DefaultWingsAuthTypeSource(loginProcessingUrl, parser);
            }
        }

        if (bindAuthTypeSource != null) {
            getAuthenticationFilter().setWingsBindAuthTypeSource(bindAuthTypeSource);
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
