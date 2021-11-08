package pro.fessional.wings.warlock.security.justauth;

import lombok.experimental.Delegate;
import me.zhyd.oauth.config.AuthConfig;
import pro.fessional.mirana.text.StringTemplate;

import javax.servlet.http.HttpServletRequest;

/**
 * @author trydofor
 * @since 2021-11-04
 */
public class AuthConfigWrapper extends AuthConfig {

    public static final String RedirectUriHost = "{host}";
    public static final String RedirectUriScheme = "{scheme}";
    public static final String RedirectUriPort = "{port}";

    public static AuthConfig tryWrap(AuthConfig config) {
        final String uri = config.getRedirectUri();
        if (uri != null && (uri.contains(RedirectUriHost) || uri.contains(RedirectUriScheme) || uri.contains(RedirectUriPort))) {
            return new AuthConfigWrapper(config, null);
        }
        return config;
    }

    @Delegate(excludes = DelegateExclude.class)
    private final AuthConfig config;
    private final HttpServletRequest request;

    public AuthConfigWrapper(AuthConfig config, HttpServletRequest request) {
        this.config = config;
        this.request = request;
    }

    public AuthConfig wrap(HttpServletRequest request) {
        return new AuthConfigWrapper(config, request);
    }

    private interface DelegateExclude {
        String getRedirectUri();
    }

    @Override
    public String getRedirectUri() {
        final String uri = config.getRedirectUri();
        if (request != null) {
            return StringTemplate
                    .dyn(uri)
                    .bindStr(RedirectUriHost, request.getHeader("Host"))
                    .bindStr(RedirectUriScheme, request.getScheme())
                    .toString();
        }
        return uri;
    }
}
