package pro.fessional.wings.warlock.service.auth.impl;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import pro.fessional.wings.slardar.security.impl.DefaultWingsUserDetails;
import pro.fessional.wings.warlock.service.auth.WarlockAuthzService;
import pro.fessional.wings.warlock.service.grant.PermGrantHelper;
import pro.fessional.wings.warlock.service.grant.WarlockGrantService;
import pro.fessional.wings.warlock.service.perm.WarlockPermNormalizer;
import pro.fessional.wings.warlock.service.perm.WarlockPermService;
import pro.fessional.wings.warlock.service.perm.WarlockRoleService;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static pro.fessional.wings.warlock.enums.autogen.GrantType.PERM;
import static pro.fessional.wings.warlock.enums.autogen.GrantType.ROLE;

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

        final HashSet<Object> roleObjs = new HashSet<>();
        final HashSet<Object> permObjs = new HashSet<>();

        for (Combo combo : authCombos) {
            if (combo.preAuth(details, roleObjs, permObjs)) {
                log.debug("break authCombos.preAuth, combo={}", combo.getClass());
                break;
            }
        }

        final HashMap<String, GrantedAuthority> auths = new HashMap<>();
        final Collection<GrantedAuthority> olds = details.getAuthorities();
        if (olds != null) {
            for (GrantedAuthority old : olds) {
                auths.put(old.getAuthority(), old);
            }
        }

        final Set<Long> roleIds = new HashSet<>();
        if (authorityRole) {
            buildGrantRoles(roleObjs, auths, roleIds);
        }
        else {
            log.debug("skip authorityRole");
        }

        if (authorityPerm) {
            buildGrantPerms(permObjs, auths, roleIds);
        }
        else {
            log.debug("skip authorityPerm");
        }

        // post
        for (Combo combo : authCombos) {
            combo.postAuth(details, auths);
        }

        details.setAuthorities(new HashSet<>(auths.values()));
    }

    protected void buildGrantRoles(Set<Object> roleObjs, HashMap<String, GrantedAuthority> auth, Set<Long> roleIds) {
        final Set<String> roleStr = new HashSet<>();
        final Set<String> denyStr = new HashSet<>();
        for (Object ro : roleObjs) {
            if (ro instanceof Long) {
                log.debug("add role by id={}", ro);
                roleIds.add((Long) ro);
            }
            else if (ro instanceof String) {
                String str = permNormalizer.role((String) ro);
                int off = permNormalizer.indexDenyPrefix(str);
                if (off > 0) {
                    log.debug("off role by str={}", ro);
                    denyStr.add(str.substring(off));
                }
                else {
                    log.debug("add role by str={}", ro);
                    roleStr.add(str);
                    auth.putIfAbsent(str, new SimpleGrantedAuthority(str));
                }
            }
            else if (ro instanceof final GrantedAuthority gt) {
                final String au = permNormalizer.role(gt.getAuthority());
                log.debug("add role by aut={}", au);
                auth.put(au, gt);  // the existed value has high priority
                roleStr.add(au);
            }
            else {
                throw new IllegalStateException("unsupported type=" + ro.getClass() + ", value=" + ro);
            }
        }

        final Map<Long, String> allRoles = warlockRoleService.loadRoleAll();
        final Set<Long> excIds = new HashSet<>();
        for (Map.Entry<Long, String> en : allRoles.entrySet()) {
            final String str = en.getValue();
            if (denyStr.contains(str)) {
                excIds.add(en.getKey());
            }
            else if (roleStr.contains(str)) {
                roleIds.add(en.getKey());
            }
        }

        // remove before recursion
        auth.keySet().removeAll(denyStr);

        // recursively find all authed Role, N+1 loop until no size change
        Set<Long> sub = new HashSet<>(roleIds);
        while (true) {
            sub = warlockGrantService.entryRole(ROLE, sub).keySet();
            int bs = roleIds.size();
            roleIds.addAll(sub);
            if (bs == roleIds.size()) {
                // no size change, loop done
                roleIds.removeAll(excIds);
                break;
            }
            else {
                roleIds.removeAll(excIds);
            }
        }

        for (Long rid : roleIds) {
            final String s = allRoles.get(rid);
            if (s != null && !denyStr.contains(s)) {
                auth.putIfAbsent(s, new SimpleGrantedAuthority(s));
            }
        }
    }

    protected void buildGrantPerms(Set<Object> permObjs, HashMap<String, GrantedAuthority> auth, Set<Long> roleIds) {
        final Map<Long, String> permAll = warlockPermService.loadPermAll();
        final Set<String> permStr = new HashSet<>();
        final Set<String> denyStr = new HashSet<>();
        for (Object po : permObjs) {
            if (po instanceof Long) {
                final String s = permAll.get(po);
                if (s != null) {
                    permStr.add(s);
                }
            }
            else if (po instanceof String pm) {
                final int off = permNormalizer.indexDenyPrefix(pm);
                if (off < 0) {
                    permStr.add(pm);
                    auth.put(pm, new SimpleGrantedAuthority(pm));
                }
                else {
                    denyStr.add(pm.substring(off));
                }
            }
            else if (po instanceof final GrantedAuthority gt) {
                final String au = gt.getAuthority();
                auth.put(au, gt); // existed value has high priority
                permStr.add(au);
            }
            else {
                throw new IllegalStateException("unsupported type=" + po.getClass() + ", value=" + po);
            }
        }

        permStr.removeAll(denyStr);

        final Set<Long> permIds = warlockGrantService.entryRole(PERM, roleIds).keySet();
        for (Long pid : permIds) {
            final String s = permAll.get(pid);
            if (s != null && !denyStr.contains(s)) {
                permStr.add(s);
            }
        }

        // sub-extension
        for (String str : permStr) {
            final Set<String> ps = PermGrantHelper.inheritPerm(str, permAll);
            for (String s : ps) {
                // remove `*` perm
                if (!s.contains(PermGrantHelper.ALL) && !denyStr.contains(s)) {
                    auth.putIfAbsent(s, new SimpleGrantedAuthority(s));
                }
            }
        }

        auth.keySet().removeAll(denyStr);
    }

    /**
     * Apply combo auth to user. e.g. change the details directly or add roles/perm before th unified processing.
     * Unified processing includes (1) recursive loading, flattening (2) removing exclusions (starting with `-`).
     */
    public interface Combo extends Ordered {
        /**
         * Prepare data for unified processing of roles and perm, e.g. modify role and perm.
         * Return `true` for solo processing, stopping any subsequent Combo (sort by Order),
         * such as directly specify the permissions, no subsequent additions,
         *
         * @param details details with/without GrantedAuthority
         * @param role    id(Long), name(String) or Auth(GrantedAuthority)
         * @param perm    id(Long), name(String) or Auth(GrantedAuthority)
         * @return Whether to stop any subsequent Combo, default false.
         */
        boolean preAuth(@NotNull DefaultWingsUserDetails details, @NotNull HashSet<Object> role, @NotNull HashSet<Object> perm);

        /**
         * Filter, add or remove auths after Unified processing
         *
         * @param details details with/without GrantedAuthority
         * @param auths   Flattened and exclusions removed permissions
         */
        default void postAuth(@NotNull DefaultWingsUserDetails details, @NotNull HashMap<String, GrantedAuthority> auths) {
        }
    }
}
