package pro.fessional.wings.warlock.service.auth.impl;

import lombok.Getter;
import lombok.Setter;
import pro.fessional.wings.slardar.security.WingsAuthDetails;
import pro.fessional.wings.slardar.security.WingsAuthHelper;
import pro.fessional.wings.slardar.security.WingsUserDetails;
import pro.fessional.wings.slardar.security.bind.WingsBindAuthToken;
import pro.fessional.wings.slardar.security.impl.AbstractAuthPermCheckCombo;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * Check Permission via param `authZone`.
 * Can be used for center authentication
 *
 * @author trydofor
 * @since 2022-01-18
 */
public class AuthZonePermChecker extends AbstractAuthPermCheckCombo {

    @Setter @Getter
    private Map<String, Set<String>> zonePerm = Collections.emptyMap();

    @Override
    protected Collection<String> requirePermit(WingsUserDetails userDetails, WingsBindAuthToken authentication) {
        final Object details = authentication.getDetails();
        if (details instanceof WingsAuthDetails) {
            final String authZone = ((WingsAuthDetails) details).getMetaData().get(WingsAuthHelper.AuthZone);
            if (authZone == null) return Collections.emptySet();
            return zonePerm.getOrDefault(authZone, Collections.emptySet());
        }
        return Collections.emptySet();
    }

    @Override public int getOrder() {
        return 0;
    }
}
