package pro.fessional.wings.warlock.service.auth.impl;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import pro.fessional.wings.slardar.context.GlobalAttributeHolder;
import pro.fessional.wings.slardar.security.impl.DefaultWingsUserDetails;
import pro.fessional.wings.warlock.constants.WarlockOrderConst;

import java.util.HashSet;
import java.util.Set;

import static pro.fessional.wings.warlock.service.user.WarlockUserAttribute.PermsByUid;
import static pro.fessional.wings.warlock.service.user.WarlockUserAttribute.RolesByUid;

/**
 * 通过user和permit的map关系构造 GrantedAuthority
 *
 * @author trydofor
 * @since 2021-03-05
 */
@Slf4j
public class DefaultPermRoleCombo implements ComboWarlockAuthzService.Combo {

    public static final int ORDER = WarlockOrderConst.UserAuthzCombo + 10_000;

    @Getter
    @Setter
    private int order = ORDER;

    @Setter(onMethod_ = {@Autowired(required = false)})
    private GrantedAuthorityDefaults grantedAuthorityDefaults;

    @Override
    public void auth(@NotNull DefaultWingsUserDetails details) {

        final long uid = details.getUserId();

        final Set<GrantedAuthority> auth = new HashSet<>();

        final Set<String> grantPerms = GlobalAttributeHolder.getAttr(PermsByUid, uid);

        for (String perm : grantPerms) {
            auth.add(new SimpleGrantedAuthority(perm));
        }

        String prefix = grantedAuthorityDefaults == null ? null : grantedAuthorityDefaults.getRolePrefix();
        if (prefix == null) prefix = "ROLE_";
        log.info("set role-prefix={}", prefix);

        final Set<String> grantRoles = GlobalAttributeHolder.getAttr(RolesByUid, uid);
        for (String role : grantRoles) {
            auth.add(new SimpleGrantedAuthority(prefix + role));
        }

        if (auth.isEmpty()) {
            log.info("empty role and perm for uid={}", uid);
        }
        else {
            log.info("add role and perm for uid={}, count={}", uid, auth.size());
            auth.addAll(details.getAuthorities());
            details.setAuthorities(auth);
        }
    }
}
