package pro.fessional.wings.slardar.security.bind;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * @author trydofor
 * @since 2021-02-07
 */
public class WingsBindAuthnToken extends UsernamePasswordAuthenticationToken {

    private static final long serialVersionUID = 1023L;

    private final Enum<?> authType;

    public WingsBindAuthnToken(Enum<?> authType, Authentication authCopy) {
        super(authCopy.getPrincipal(), authCopy.getCredentials(), authCopy.getAuthorities());
        this.authType = authType;
        setDetails(authCopy.getDetails());
    }

    public WingsBindAuthnToken(Enum<?> authType, Object principal, Object credentials) {
        super(principal, credentials);
        this.authType = authType;
    }

    public WingsBindAuthnToken(Enum<?> authType, Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
        super(principal, credentials, authorities);
        this.authType = authType;
    }

    public Enum<?> getAuthType() {
        return authType;
    }
}
