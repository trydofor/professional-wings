package pro.fessional.wings.slardar.security.conf;

import org.springframework.context.ApplicationContext;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.web.authentication.ForwardAuthenticationFailureHandler;
import org.springframework.security.web.authentication.ForwardAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.ui.DefaultLoginPageGeneratingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import pro.fessional.wings.slardar.security.auth.WingsBindAuthTypeSource;
import pro.fessional.wings.slardar.security.auth.WingsBindAuthTypeSourceDefault;
import pro.fessional.wings.slardar.security.auth.WingsBindAuthnFilter;

/**
 * @author trydofor
 * @since 2021-02-07
 */
public class WingsBindLoginConfigurer extends
        AbstractAuthenticationFilterConfigurer<HttpSecurity, WingsBindLoginConfigurer, WingsBindAuthnFilter> {

    public WingsBindLoginConfigurer() {
        super(new WingsBindAuthnFilter(), null);
        usernameParameter("username");
        passwordParameter("password");
    }

    @Override
    public WingsBindLoginConfigurer loginPage(String loginPage) {
        return super.loginPage(loginPage);
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

    private String headerName = null;
    private String paramName = null;
    private Enum<?>[] authType = null;
    private WingsBindAuthTypeSource wingsBindAuthtypeSource = null;

    public WingsBindLoginConfigurer bindAuthtypeToHeader(String headerName) {
        this.headerName = headerName;
        return this;
    }

    public WingsBindLoginConfigurer bindAuthtypeToParam(String paramName) {
        this.paramName = paramName;
        return this;
    }

    public WingsBindLoginConfigurer bindAuthtypeToEnums(Enum<?>... authType) {
        this.authType = authType;
        return this;
    }

    public WingsBindLoginConfigurer bindAuthtypeSource(WingsBindAuthTypeSource bindAuthtypeSource) {
        this.wingsBindAuthtypeSource = bindAuthtypeSource;
        return this;
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
        if (wingsBindAuthtypeSource == null) {
            if (authType != null) {
                wingsBindAuthtypeSource = new WingsBindAuthTypeSourceDefault(getLoginProcessingUrl(), paramName, headerName, authType);
            } else {
                wingsBindAuthtypeSource = context.getBeanProvider(WingsBindAuthTypeSource.class).getIfAvailable();
            }
        }

        if (wingsBindAuthtypeSource != null) {
            getAuthenticationFilter().setWingsBindAuthTypeSource(wingsBindAuthtypeSource);
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
