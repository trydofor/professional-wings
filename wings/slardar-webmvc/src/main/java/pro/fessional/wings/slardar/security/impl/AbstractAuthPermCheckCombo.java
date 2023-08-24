package pro.fessional.wings.slardar.security.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import pro.fessional.wings.slardar.security.WingsUserDetails;
import pro.fessional.wings.slardar.security.bind.WingsBindAuthToken;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Case-sensitive permission checking
 *
 * @author trydofor
 * @since 2022-01-19
 */

public abstract class AbstractAuthPermCheckCombo implements ComboWingsAuthCheckService.Combo {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public boolean check(WingsUserDetails userDetails, WingsBindAuthToken authentication) {
        final Collection<String> permit = requirePermit(userDetails, authentication);
        if (permit == null || permit.isEmpty()) return true;

        final Set<String> all = userDetails.getAuthorities()
                                           .stream()
                                           .map(GrantedAuthority::getAuthority)
                                           .collect(Collectors.toSet());
        for (String s : permit) {
            if (all.contains(s)) {
                return true;
            }
        }

        log.info("reject login, require any permit {}", permit);
        return false;
    }

    protected abstract Collection<String> requirePermit(WingsUserDetails userDetails, WingsBindAuthToken authentication);

}
