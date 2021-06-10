package pro.fessional.wings.warlock.service.auth.impl;

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
import pro.fessional.wings.warlock.service.perm.RoleNormalizer;
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

    private List<Combo> authCombos = Collections.emptyList();

    @Setter(onMethod_ = {@Autowired})
    private RoleNormalizer roleNormalizer;

    @Setter(onMethod_ = {@Autowired})
    private WarlockRoleService warlockRoleService;

    @Setter(onMethod_ = {@Autowired})
    private WarlockPermService warlockPermService;

    @Setter(onMethod_ = {@Autowired})
    private WarlockGrantService warlockGrantService;

    @Autowired(required = false)
    public void setAuthCombos(List<Combo> authCombos) {
        log.info("inject auth combo, count={}", authCombos.size());
        this.authCombos = authCombos;
    }

    @Override
    public void auth(DefaultWingsUserDetails details) {
        if (details == null) return;

        Set<Object> roleObjs = new HashSet<>();
        Set<Object> permObjs = new HashSet<>();
        for (Combo combo : authCombos) {
            combo.auth(details, roleObjs, permObjs);
        }

        final Set<GrantedAuthority> auth = new HashSet<>();

        final Set<Long> roleIds = new HashSet<>();
        final Set<String> roleStr = new HashSet<>();
        final Set<String> permStr = new HashSet<>();

        buildGrantRoles(roleObjs, auth, roleStr, roleIds);
        buildGrantPerms(permObjs, auth, permStr, roleIds);

        final long uid = details.getUserId();

        GlobalAttributeHolder.putAttr(PermsByUid, uid, permStr);
        GlobalAttributeHolder.putAttr(RolesByUid, uid, roleStr);
    }

    private void buildGrantRoles(Set<Object> roleObjs, Set<GrantedAuthority> auth, Set<String> roleStr, Set<Long> roleIds) {
        for (Object ro : roleObjs) {
            if (ro instanceof Long) {
                roleIds.add((Long) ro);
            }
            else if (ro instanceof String) {
                String str = roleNormalizer.normalize((String) ro);
                roleStr.add(str);
                auth.add(new SimpleGrantedAuthority(str));
            }
            else if (ro instanceof GrantedAuthority) {
                final GrantedAuthority gt = (GrantedAuthority) ro;
                auth.add(gt);
                final String au = gt.getAuthority();
                if (roleNormalizer.hasPrefix(au)) {
                    roleStr.add(au);
                }
            }
            else {
                throw new IllegalStateException("unsupported type=" + ro.getClass() + ", value=" + ro);
            }
        }

        final Map<Long, String> allRoles = warlockRoleService.loadRoleAll();

        for (Map.Entry<Long, String> en : allRoles.entrySet()) {
            if (roleStr.contains(en.getValue())) {
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
                break;
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

        final Set<Long> permIds = warlockGrantService.entryRole(PERM, roleIds).keySet();
        for (Long pid : permIds) {
            final String s = permAll.get(pid);
            if(s != null){
                permStr.add(s);
            }
        }

        for (Object po : permObjs) {
            if (po instanceof Long) {
                final String s = permAll.get(po);
                if(s != null){
                    permStr.add(s);
                }
            }
            else if (po instanceof String) {
                permStr.add((String) po);
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

        for (String str : permStr) {
            final Set<String> ps = PermGrantHelper.inheritPerm(str, permAll);
            for (String p : ps) {
                // 去掉`*`权限
                if (!p.contains(PermGrantHelper.ALL)) {
                    auth.add(new SimpleGrantedAuthority(p));
                    permStr.add(p);
                }
            }
        }
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
