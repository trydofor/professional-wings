package pro.fessional.wings.slardar.security.bind;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.Assert;
import pro.fessional.wings.slardar.security.WingsAuthDetailsSource;
import pro.fessional.wings.slardar.security.WingsAuthTypeSource;

/**
 * @author trydofor
 * @since 2021-02-07
 */
public class WingsBindAuthFilter extends UsernamePasswordAuthenticationFilter {

    protected WingsAuthTypeSource wingsAuthTypeSource;

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
        // support both POST and GET (Oauth redirect)
        String username = obtainUsername(request);
        username = (username != null) ? username.trim() : "";

        String password = obtainPassword(request);
        password = (password != null) ? password : "";

        Enum<?> authType = wingsAuthTypeSource.buildAuthType(request);
        WingsBindAuthToken winTkn = new WingsBindAuthToken(authType, username, password);
        final Object details = buildAuthDetails(request, winTkn);
        winTkn.setDetails(details);
        return this.getAuthenticationManager().authenticate(winTkn);
    }

    protected Object buildAuthDetails(HttpServletRequest request, WingsBindAuthToken winTkn) {
        if (authenticationDetailsSource instanceof WingsAuthDetailsSource<?> winAds) {
            return winAds.buildDetails(winTkn.getAuthType(), request);
        }
        else {
            return authenticationDetailsSource.buildDetails(request);
        }
    }

    public WingsAuthTypeSource getWingsBindAuthTypeSource() {
        return wingsAuthTypeSource;
    }

    public void setWingsBindAuthTypeSource(WingsAuthTypeSource wingsAuthTypeSource) {
        Assert.notNull(wingsAuthTypeSource, "wingsBindAuthTypeSource required");
        this.wingsAuthTypeSource = wingsAuthTypeSource;
    }
}
