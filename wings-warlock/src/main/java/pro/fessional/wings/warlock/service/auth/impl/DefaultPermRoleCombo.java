package pro.fessional.wings.warlock.service.auth.impl;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import pro.fessional.wings.slardar.security.impl.DefaultWingsUserDetails;
import pro.fessional.wings.warlock.constants.WarlockOrderConst;
import pro.fessional.wings.warlock.database.autogen.tables.WinUserPermMapTable;
import pro.fessional.wings.warlock.database.autogen.tables.WinUserRoleMapTable;
import pro.fessional.wings.warlock.database.autogen.tables.daos.WinUserPermMapDao;
import pro.fessional.wings.warlock.database.autogen.tables.daos.WinUserRoleMapDao;
import pro.fessional.wings.warlock.service.perm.PermInheritHelper;
import pro.fessional.wings.warlock.service.perm.WarlockPermService;
import pro.fessional.wings.warlock.service.perm.WarlockRoleService;

import java.util.HashSet;
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
    private WinUserPermMapDao winUserPermMapDao;
    @Setter(onMethod_ = {@Autowired})
    private WinUserRoleMapDao winUserRoleMapDao;

    @Setter(onMethod_ = {@Autowired})
    private WarlockPermService warlockPermService;
    @Setter(onMethod_ = {@Autowired})
    private WarlockRoleService warlockRoleService;

    @Setter(onMethod_ = {@Autowired(required = false)})
    private GrantedAuthorityDefaults grantedAuthorityDefaults;

    @Override
    public void auth(@NotNull DefaultWingsUserDetails details) {
        final Map<Long, String> roleAll = warlockRoleService.loadRoleAll();
        final Map<Long, Set<Long>> roleMap = warlockRoleService.loadRoleMap();

        final long uid = details.getUserId();
        Set<GrantedAuthority> auth = new HashSet<>();

        final Map<Long, String> permAll = warlockPermService.loadPermAll();
        for (Long pid : loadUserPerms(uid)) {
            final Set<String> ps = PermInheritHelper.inheritPerm(pid, permAll);
            for (String p : ps) {
                // 去掉 * 权限
                if (!p.contains(PermInheritHelper.ALL)) {
                    auth.add(new SimpleGrantedAuthority(p));
                }
            }
        }

        String prefix = grantedAuthorityDefaults == null ? null : grantedAuthorityDefaults.getRolePrefix();
        if (prefix == null) prefix = "ROLE_";
        log.info("set role-prefix={}", prefix);
        for (Long rid : loadUserRoles(uid)) {
            final Set<String> rs = PermInheritHelper.inheritRole(rid, roleAll, roleMap);
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

    private Set<Long> loadUserPerms(long uid) {
        final WinUserPermMapTable t = winUserPermMapDao.getTable();
        final Set<Long> pid = winUserPermMapDao
                .ctx()
                .select(t.GrantPerm)
                .from(t)
                .where(t.onlyLive(t.ReferUser.eq(uid)))
                .fetch()
                .intoSet(t.GrantPerm);

        log.info("load {} perm of uid={}", pid.size(), uid);
        return pid;
    }

    private Set<Long> loadUserRoles(long uid) {
        final WinUserRoleMapTable t = winUserRoleMapDao.getTable();
        final Set<Long> pid = winUserRoleMapDao
                .ctx()
                .select(t.GrantRole)
                .from(t)
                .where(t.onlyLive(t.ReferUser.eq(uid)))
                .fetch()
                .intoSet(t.GrantRole);

        log.info("load {} role of uid={}", pid.size(), uid);
        return pid;
    }
}
