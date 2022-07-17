package pro.fessional.wings.warlock.service.auth.impl;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import pro.fessional.wings.slardar.context.GlobalAttributeHolder;
import pro.fessional.wings.slardar.security.impl.DefaultWingsUserDetails;
import pro.fessional.wings.warlock.service.auth.WarlockAuthzService;
import pro.fessional.wings.warlock.service.grant.PermGrantHelper;
import pro.fessional.wings.warlock.service.grant.WarlockGrantService;
import pro.fessional.wings.warlock.service.perm.WarlockPermNormalizer;
import pro.fessional.wings.warlock.service.perm.WarlockPermService;
import pro.fessional.wings.warlock.service.perm.WarlockRoleService;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static pro.fessional.wings.warlock.enums.autogen.GrantType.PERM;
import static pro.fessional.wings.warlock.enums.autogen.GrantType.ROLE;
import static pro.fessional.wings.warlock.service.user.WarlockUserAttribute.PermsByUid;
import static pro.fessional.wings.warlock.service.user.WarlockUserAttribute.RolesByUid;

/**
 * @author trydofor
 * @since 2021-02-23
 */
@Slf4j
public class ComboWarlockAuthzService implements WarlockAuthzService {

    @Setter(onMethod_ = {@Autowired})
    protected WarlockPermNormalizer permNormalizer;

    @Setter(onMethod_ = {@Autowired})
    protected WarlockRoleService warlockRoleService;

    @Setter(onMethod_ = {@Autowired})
    protected WarlockPermService warlockPermService;

    @Setter(onMethod_ = {@Autowired})
    protected WarlockGrantService warlockGrantService;

    @Setter(onMethod_ = {@Autowired(required = false)})
    private List<Combo> authCombos = Collections.emptyList();

    @Setter @Getter
    private boolean authorityRole = true;
    @Setter @Getter
    private boolean authorityPerm = true;

    @Override
    public void auth(DefaultWingsUserDetails details) {
        if (details == null) return;

        Set<Object> roleObjs = new HashSet<>();
        Set<Object> permObjs = new HashSet<>();
        for (Combo combo : authCombos) {
            combo.auth(details, roleObjs, permObjs);
        }

        final long uid = details.getUserId();
        final Set<GrantedAuthority> auths = new HashSet<>();

        final Set<Long> roleIds = new HashSet<>();

        if (authorityRole) {
            final Set<String> roleStr = new HashSet<>();
            buildGrantRoles(roleObjs, auths, roleStr, roleIds);
            GlobalAttributeHolder.putAttr(RolesByUid, uid, roleStr);
        }
        else {
            log.debug("skip authorityRole");
        }

        if (authorityPerm) {
            final Set<String> permStr = new HashSet<>();
            buildGrantPerms(permObjs, auths, permStr, roleIds);
            permStr.removeIf(it -> it.contains(PermGrantHelper.ALL));
            GlobalAttributeHolder.putAttr(PermsByUid, uid, permStr);
        }
        else {
            log.debug("skip authorityPerm");
        }

        details.setAuthorities(auths);
    }

    private void buildGrantRoles(Set<Object> roleObjs, Set<GrantedAuthority> auth, Set<String> roleStr, Set<Long> roleIds) {
        Set<String> excStr = new HashSet<>();
        for (Object ro : roleObjs) {
            if (ro instanceof Long) {
                log.debug("add role by id={}", ro);
                roleIds.add((Long) ro);
            }
            else if (ro instanceof String) {
                String str = permNormalizer.role((String) ro);
                int off = permNormalizer.indexExcludePrefix(str);
                if (off > 0) {
                    log.debug("off role by str={}", ro);
                    excStr.add(str.substring(off));
                }
                else {
                    log.debug("add role by str={}", ro);
                    roleStr.add(str);
                    auth.add(new SimpleGrantedAuthority(str));
                }
            }
            else if (ro instanceof GrantedAuthority) {
                final GrantedAuthority gt = (GrantedAuthority) ro;
                auth.add(gt);
                final String au = gt.getAuthority();
                log.debug("add role by aut={}", au);
                if (permNormalizer.indexRolePrefix(au) >= 0) {
                    roleStr.add(au);
                }
            }
            else {
                throw new IllegalStateException("unsupported type=" + ro.getClass() + ", value=" + ro);
            }
        }

        // 移除
        roleStr.removeAll(excStr);
        auth.removeIf(it -> excStr.contains(it.getAuthority()));

        final Map<Long, String> allRoles = warlockRoleService.loadRoleAll();
        final Set<Long> excIds = new HashSet<>();
        for (Map.Entry<Long, String> en : allRoles.entrySet()) {
            final String str = en.getValue();
            if (excStr.contains(str)) {
                excIds.add(en.getKey());
            }
            else if (roleStr.contains(str)) {
                roleIds.add(en.getKey());
            }
        }

        // 递归找到所有授权Role，N+1操作，直到size不增加
        Set<Long> sub = new HashSet<>(roleIds);
        while (true) {
            sub = warlockGrantService.entryRole(ROLE, sub).keySet();
            int bs = roleIds.size();
            roleIds.addAll(sub);
            if (bs == roleIds.size()) {
                // size无变化，说明全遍历
                roleIds.removeAll(excIds);
                break;
            }
            else {
                roleIds.removeAll(excIds);
            }
        }

        for (Long rid : roleIds) {
            final String s = allRoles.get(rid);
            if (s == null) continue;
            auth.add(new SimpleGrantedAuthority(s));
            roleStr.add(s);
        }
    }

    private void buildGrantPerms(Set<Object> permObjs, Set<GrantedAuthority> auth, Set<String> permStr, Set<Long> roleIds) {
        final Map<Long, String> permAll = warlockPermService.loadPermAll();

        Set<String> excStr = new HashSet<>();
        for (Object po : permObjs) {
            if (po instanceof Long) {
                final String s = permAll.get(po);
                if (s != null) {
                    permStr.add(s);
                }
            }
            else if (po instanceof String) {
                String pm = (String) po;
                final int off = permNormalizer.indexExcludePrefix(pm);
                if (off < 0) {
                    permStr.add(pm);
                    auth.add(new SimpleGrantedAuthority(pm));
                }
                else {
                    excStr.add(pm.substring(off));
                }
            }
            else if (po instanceof GrantedAuthority) {
                final GrantedAuthority gt = (GrantedAuthority) po;
                auth.add(gt);
                permStr.add(gt.getAuthority());
            }
            else {
                throw new IllegalStateException("unsupported type=" + po.getClass() + ", value=" + po);
            }
        }

        permStr.removeAll(excStr);
        auth.removeIf(it -> excStr.contains(it.getAuthority()));

        final Set<Long> permIds = warlockGrantService.entryRole(PERM, roleIds).keySet();
        for (Long pid : permIds) {
            final String s = permAll.get(pid);
            if (s != null && !excStr.contains(s)) {
                permStr.add(s);
            }
        }

        Set<String> tmp = new HashSet<>();
        for (String str : permStr) {
            final Set<String> ps = PermGrantHelper.inheritPerm(str, permAll);
            for (String s : ps) {
                // 去掉`*`权限
                if (!s.contains(PermGrantHelper.ALL) && !excStr.contains(s)) {
                    auth.add(new SimpleGrantedAuthority(s));
                    tmp.add(s);
                }
            }
        }

        permStr.addAll(tmp);
    }

    // /////
    public interface Combo extends Ordered {
        /**
         * 对用户进行组合授权，可直接修改 details 或 增加role，perm，以便统一处理递归
         *
         * @param details details with/without GrantedAuthority
         * @param role    id(Long),name(String) or Auth(GrantedAuthority)
         * @param perm    id(Long),name(String) or Auth(GrantedAuthority)
         */
        void auth(@NotNull DefaultWingsUserDetails details, @NotNull Set<Object> role, @NotNull Set<Object> perm);
    }
}
