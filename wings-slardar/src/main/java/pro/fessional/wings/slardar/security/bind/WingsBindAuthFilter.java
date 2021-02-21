package pro.fessional.wings.slardar.security.bind;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.Assert;
import pro.fessional.wings.slardar.security.WingsAuthDetailsSource;
import pro.fessional.wings.slardar.security.WingsAuthTypeSource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author trydofor
 * @since 2021-02-07
 */
public class WingsBindAuthFilter extends UsernamePasswordAuthenticationFilter {

    private WingsAuthTypeSource wingsAuthTypeSource;

    public WingsBindAuthFilter() {
        super();
        setPostOnly(false);
    }

    public WingsBindAuthFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
        setPostOnly(false);
    }

    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        Assert.notNull(wingsAuthTypeSource, "wingsBindAuthTypeSource must be specified. should expose a Bean by type or manual config");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        // 同时支持post和get（主要是第三方redirect）
        String username = obtainUsername(request);
        username = (username != null) ? username.trim() : "";

        String password = obtainPassword(request);
        password = (password != null) ? password : "";

        Enum<?> authType = wingsAuthTypeSource.buildAuthType(request);
        WingsBindAuthToken authRequest = new WingsBindAuthToken(authType, username, password);
        if (authenticationDetailsSource instanceof WingsAuthDetailsSource<?>) {
            WingsAuthDetailsSource<?> ads = (WingsAuthDetailsSource<?>) authenticationDetailsSource;
            authRequest.setDetails(ads.buildDetails(authType, request));
        } else {
            authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
        }

        return this.getAuthenticationManager().authenticate(authRequest);
    }

    public WingsAuthTypeSource getWingsBindAuthTypeSource() {
        return wingsAuthTypeSource;
    }

    public void setWingsBindAuthTypeSource(WingsAuthTypeSource wingsAuthTypeSource) {
        Assert.notNull(wingsAuthTypeSource, "wingsBindAuthTypeSource required");
        this.wingsAuthTypeSource = wingsAuthTypeSource;
    }
}
