package pro.fessional.wings.warlock.service.auth.impl;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jooq.Condition;
import org.jooq.Record2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import pro.fessional.wings.slardar.security.impl.DefaultWingsUserDetails;
import pro.fessional.wings.warlock.constants.WarlockOrderConst;
import pro.fessional.wings.warlock.database.autogen.tables.WinUserGrantTable;
import pro.fessional.wings.warlock.database.autogen.tables.daos.WinUserGrantDao;
import pro.fessional.wings.warlock.enums.autogen.GrantType;
import pro.fessional.wings.warlock.service.grant.PermGrantHelper;
import pro.fessional.wings.warlock.service.perm.WarlockPermService;
import pro.fessional.wings.warlock.service.perm.WarlockRoleService;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 通过user和permit的map关系构造 GrantedAuthority
 *
 * @author trydofor
 * @since 2021-03-05
 */
@Service
@Slf4j
public class DefaultPermRoleCombo implements ComboWarlockAuthzService.Combo {

    public static final int ORDER = WarlockOrderConst.UserAuthzCombo + 10_000;

    @Getter
    @Setter
    private int order = ORDER;

    @Setter(onMethod_ = {@Autowired})
    private WinUserGrantDao winUserGrantDao;

    @Setter(onMethod_ = {@Autowired})
    private WarlockPermService warlockPermService;
    @Setter(onMethod_ = {@Autowired})
    private WarlockRoleService warlockRoleService;

    @Setter(onMethod_ = {@Autowired(required = false)})
    private GrantedAuthorityDefaults grantedAuthorityDefaults;

    @Override
    public void auth(@NotNull DefaultWingsUserDetails details) {
        final Map<Long, String> roleAll = warlockRoleService.loadRoleAll();
        final Map<Long, Set<Long>> roleGrant = warlockRoleService.loadRoleGrant();

        final long uid = details.getUserId();
        final WinUserGrantTable t = winUserGrantDao.getTable();
        final Condition cond = t.onlyLive(t.ReferUser.eq(uid).and(t.GrantType.eq(1)));
        final Map<Integer, List<Long>> grants = winUserGrantDao
                .ctx()
                .select(t.GrantType, t.GrantEntry)
                .from(t)
                .where(cond)
                .fetch()
                .intoGroups(Record2::value1, Record2::value2);

        final Set<GrantedAuthority> auth = new HashSet<>();
        final Map<Long, String> permAll = warlockPermService.loadPermAll();
        final List<Long> grantPerms = grants.getOrDefault(GrantType.PERM.getId(), Collections.emptyList());
        log.info("load {} perm grant for uid={}", grantPerms.size(), uid);
        for (Long pid : grantPerms) {
            final Set<String> ps = PermGrantHelper.inheritPerm(pid, permAll);
            for (String p : ps) {
                // 去掉 * 权限
                if (!p.contains(PermGrantHelper.ALL)) {
                    auth.add(new SimpleGrantedAuthority(p));
                }
            }
        }

        String prefix = grantedAuthorityDefaults == null ? null : grantedAuthorityDefaults.getRolePrefix();
        if (prefix == null) prefix = "ROLE_";
        log.info("set role-prefix={}", prefix);
        final List<Long> grantRoles = grants.getOrDefault(GrantType.ROLE.getId(), Collections.emptyList());
        log.info("load {} role grant for uid={}", grantRoles.size(), uid);
        for (Long rid : grantRoles) {
            final Set<String> rs = PermGrantHelper.grantRole(rid, roleAll, roleGrant);
            for (String r : rs) {
                auth.add(new SimpleGrantedAuthority(prefix + r));
            }
        }

        if (auth.isEmpty()) {
            log.info("empty role and perm for uid={}", uid);
        } else {
            log.info("add role and perm for uid={}, count={}", uid, auth.size());
            auth.addAll(details.getAuthorities());
            details.setAuthorities(auth);
        }
    }
}
