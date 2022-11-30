package pro.fessional.wings.slardar.security.impl;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import pro.fessional.wings.slardar.context.TerminalContext;
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
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true, builderMethodName = "")
public class DefaultWingsUserDetails implements WingsUserDetails {

    private long userId;
    private String nickname;
    private Locale locale = TerminalContext.defaultLocale();
    private ZoneId zoneId = TerminalContext.defaultZoneId();

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

    private Collection<GrantedAuthority> authorities = Collections.emptySet();
}
