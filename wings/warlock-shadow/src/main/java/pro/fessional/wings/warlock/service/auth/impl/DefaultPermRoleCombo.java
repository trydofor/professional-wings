package pro.fessional.wings.warlock.service.auth.impl;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import pro.fessional.wings.slardar.security.impl.DefaultWingsUserDetails;
import pro.fessional.wings.spring.consts.OrderedWarlockConst;
import pro.fessional.wings.warlock.enums.autogen.GrantType;
import pro.fessional.wings.warlock.service.grant.WarlockGrantService;

import java.util.HashSet;

/**
 * Create GrantedAuthority by the mapping of user and permit
 *
 * @author trydofor
 * @since 2021-03-05
 */
@Slf4j
public class DefaultPermRoleCombo implements ComboWarlockAuthzService.Combo {

    @Getter @Setter
    private int order = OrderedWarlockConst.DefaultPermRoleCombo;

    @Setter(onMethod_ = {@Autowired})
    protected WarlockGrantService warlockGrantService;

    @Override
    public boolean preAuth(@NotNull DefaultWingsUserDetails details, @NotNull HashSet<Object> role, @NotNull HashSet<Object> perm) {

        final long uid = details.getUserId();
        final var roles = warlockGrantService.entryUser(GrantType.ROLE, uid);
        log.debug("got roles for uid={}, size={}", uid, roles.size());
        role.addAll(roles.keySet());

        final var perms = warlockGrantService.entryUser(GrantType.PERM, uid);
        log.debug("got perms for uid={}, size={}", uid, perms.size());
        perm.addAll(perms.keySet());

        return false;
    }
}
