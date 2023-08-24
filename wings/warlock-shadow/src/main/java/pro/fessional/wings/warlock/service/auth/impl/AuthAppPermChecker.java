package pro.fessional.wings.warlock.service.auth.impl;

import lombok.Getter;
import lombok.Setter;
import pro.fessional.wings.slardar.security.WingsUserDetails;
import pro.fessional.wings.slardar.security.bind.WingsBindAuthToken;
import pro.fessional.wings.slardar.security.impl.AbstractAuthPermCheckCombo;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * Check Permission via spring.application.name.
 * Typically used for native authentication
 *
 * @author trydofor
 * @since 2022-01-18
 */
public class AuthAppPermChecker extends AbstractAuthPermCheckCombo {

    @Setter @Getter
    private Set<String> appPerm = Collections.emptySet();

    @Override
    protected Collection<String> requirePermit(WingsUserDetails userDetails, WingsBindAuthToken authentication) {
        return appPerm;
    }

    @Override public int getOrder() {
        return 0;
    }
}
