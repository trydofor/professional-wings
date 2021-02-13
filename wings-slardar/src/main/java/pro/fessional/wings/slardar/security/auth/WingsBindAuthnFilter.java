package pro.fessional.wings.slardar.security.auth;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author trydofor
 * @since 2021-02-07
 */
public class WingsBindAuthnFilter extends UsernamePasswordAuthenticationFilter {

    private WingsBindAuthTypeSource wingsBindAuthTypeSource;

    public WingsBindAuthnFilter() {
        super();
        setPostOnly(false);
    }

    public WingsBindAuthnFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
        setPostOnly(false);
    }

    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        Assert.notNull(wingsBindAuthTypeSource, "wingsBindAuthTypeSource must be specified. should expose a Bean by type or manual config");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        // 同时支持post和get（主要是第三方redirect）
        String username = obtainUsername(request);
        username = (username != null) ? username.trim() : "";

        String password = obtainPassword(request);
        password = (password != null) ? password : "";

        Enum<?> authType = wingsBindAuthTypeSource.buildAuthType(request);
        WingsBindAuthnToken authRequest = new WingsBindAuthnToken(authType, username, password);
        if (authenticationDetailsSource instanceof WingsBindAuthnDetailsSource<?>) {
            WingsBindAuthnDetailsSource<?> ads = (WingsBindAuthnDetailsSource<?>) authenticationDetailsSource;
            authRequest.setDetails(ads.buildDetails(authType, request));
        } else {
            authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
        }

        return this.getAuthenticationManager().authenticate(authRequest);
    }

    public WingsBindAuthTypeSource getWingsBindAuthTypeSource() {
        return wingsBindAuthTypeSource;
    }

    public void setWingsBindAuthTypeSource(WingsBindAuthTypeSource wingsBindAuthTypeSource) {
        Assert.notNull(wingsBindAuthTypeSource, "wingsBindAuthtypeSource required");
        this.wingsBindAuthTypeSource = wingsBindAuthTypeSource;
    }
}
