package pro.fessional.wings.warlock.security.justauth;

import lombok.experimental.Delegate;
import me.zhyd.oauth.config.AuthConfig;
import pro.fessional.mirana.text.StringTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.Set;

/**
 * @author trydofor
 * @since 2021-11-04
 */
public class AuthConfigWrapper extends AuthConfig {

    public static final String RedirectUriHost = "{host}";
    public static final String RedirectUriScheme = "{scheme}";

    public static AuthConfig tryWrap(AuthConfig config, Set<String> safeHost) {
        final String uri = config.getRedirectUri();
        if (uri != null && (uri.contains(RedirectUriHost) || uri.contains(RedirectUriScheme))) {
            return new AuthConfigWrapper(config, safeHost, null);
        }
        return config;
    }

    @Delegate(excludes = DelegateExclude.class)
    private final AuthConfig config;
    private final HttpServletRequest request;
    private final Set<String> safeHost;

    public AuthConfigWrapper(AuthConfig config, Set<String> safeHost, HttpServletRequest request) {
        this.config = config;
        this.request = request;
        this.safeHost = safeHost;
    }

    public AuthConfig wrap(HttpServletRequest request) {
        return new AuthConfigWrapper(config, safeHost, request);
    }

    private interface DelegateExclude {
        String getRedirectUri();
    }

    @Override
    public String getRedirectUri() {
        final String uri = config.getRedirectUri();
        if (request == null) return uri;

        String host = request.getHeader("Host");
        if (safeHost != null) {
            final String key = request.getParameter("host");
            if (key != null) {
                if (safeHost.contains(key)) {
                    host = key;
                }
            }
        }

        return StringTemplate
                .dyn(uri)
                .bindStr(RedirectUriHost, host)
                .bindStr(RedirectUriScheme, request.getScheme())
                .toString();
    }
}
