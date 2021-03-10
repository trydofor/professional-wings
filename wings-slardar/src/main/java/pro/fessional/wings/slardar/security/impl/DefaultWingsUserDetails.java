package pro.fessional.wings.slardar.security.impl;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import pro.fessional.wings.slardar.security.WingsUserDetails;

import java.time.ZoneId;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;

/**
 * @author trydofor
 * @since 2021-02-18
 */
@Data
public class DefaultWingsUserDetails implements WingsUserDetails {

    private long userId;
    private String nickname;
    private Locale locale = Locale.getDefault();
    private ZoneId zoneId = ZoneId.systemDefault();

    // additionalAuthenticationChecks
    private String username;
    private String password;
    private String passsalt = "";
    private Enum<?> authType;

    private boolean preAuthed = false;
    // PreAuthenticationChecks
    private boolean enabled = true;
    private boolean accountNonExpired = true;
    private boolean accountNonLocked = true;
    // PostAuthenticationChecks
    private boolean credentialsNonExpired = true;

    private Collection<? extends GrantedAuthority> authorities = Collections.emptySet();
}
