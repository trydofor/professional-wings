package pro.fessional.wings.slardar.security.bind;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * 认证用token，中间状态
 *
 * @author trydofor
 * @since 2021-02-07
 */
public class WingsBindAuthToken extends UsernamePasswordAuthenticationToken {

    private static final long serialVersionUID = 1023L;

    private final Enum<?> authType;

    public WingsBindAuthToken(Enum<?> authType, Object principal, Object credentials) {
        super(principal, credentials);
        this.authType = authType;
    }

    public WingsBindAuthToken(Enum<?> authType, Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
        super(principal, credentials, authorities);
        this.authType = authType;
    }

    public Enum<?> getAuthType() {
        return authType;
    }
}
