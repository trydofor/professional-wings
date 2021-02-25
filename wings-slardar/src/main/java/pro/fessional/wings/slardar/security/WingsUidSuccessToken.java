package pro.fessional.wings.slardar.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * 认证成功，wings中应该只有此token，
 * Details 应更是 WingsUserDetails。
 * Principal 为 userId
 *
 * @author trydofor
 * @since 2021-02-25
 */
public class WingsUidSuccessToken extends AbstractAuthenticationToken {

    private static final long serialVersionUID = 1979L;

    private final long userId;

    public WingsUidSuccessToken(WingsUserDetails details, Collection<? extends GrantedAuthority> authorities) {
        this(details.getUserId(), details, authorities);
    }

    public WingsUidSuccessToken(long userId, Collection<? extends GrantedAuthority> authorities) {
        this(userId, null, authorities);
    }

    public WingsUidSuccessToken(long userId, Object details, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        super.setDetails(details);
        super.setAuthenticated(true);
        this.userId = userId;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return userId;
    }

    public long getUserId() {
        return userId;
    }

    @Override
    public void setAuthenticated(boolean authenticated) {
        throw new IllegalArgumentException("the final Authentication, always true");
    }
}
