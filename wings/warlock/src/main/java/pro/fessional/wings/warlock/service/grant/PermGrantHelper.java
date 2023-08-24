package pro.fessional.wings.warlock.service.grant;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <pre>
 * Root permission is empty, so the unite result is followed by `. *`.
 * `inherit` means the scopes separated by `.` is an implicit inheritance relationship.
 * `grant` means binding authorization.
 * </pre>
 *
 * @author trydofor
 * @since 2021-03-07
 */
public class PermGrantHelper {

    public static final String ALL = "*";
    public static final String SPL = ".";


    public static String unitePermit(String scopes, String action) {
        return scopes + SPL + action;
    }

    public static String[] splitPermit(String permit) {
        int p = permit.lastIndexOf(SPL);
        if (p > 0) {
            return new String[]{permit.substring(0, p), permit.substring(p + 1)};
        }
        else if (p == 0) {
            return new String[]{"", permit.substring(1)};
        }
        else {
            return new String[]{"", permit};
        }
    }

    /**
     * Whether the `top` include `sub` (sub inherit top, case-insensitive)
     * `*` means all, Compare actions with the same name
     *
     * @param top e.g. `system.*`
     * @param sub e.g. `system.menu.read`
     */
    public static boolean canInherit(String top, String sub) {
        return canInherit(top, sub, SPL);
    }

    /**
     * Whether the `top` include `sub` (sub inherit top, case-insensitive)
     * `*` means all, Compare actions with the same name
     *
     * @param top e.g. `system.*`
     * @param sub e.g. `system.menu.read`
     * @param spl separator, default `.`
     */
    public static boolean canInherit(String top, String sub, String spl) {
        if (top == null || sub == null) return false;
        if (spl == null) spl = SPL;

        int t1 = top.lastIndexOf(spl);
        if (t1 < 0) {
            if (top.equals(ALL)) {
                return true;
            }
            else {
                return top.equalsIgnoreCase(sub);
            }
        }
        else {
            final int hl = t1 + 1;
            if (top.regionMatches(hl, ALL, 0, ALL.length())) {
                return t1 == 0 || sub.regionMatches(true, 0, top, 0, hl);
            }
            else {
                int s1 = sub.lastIndexOf(spl);
                if (s1 < 0) {
                    return false;
                }
                else {
                    return sub.regionMatches(true, 0, top, 0, hl)
                           && sub.regionMatches(true, s1 + 1, top, hl, top.length() - hl);
                }
            }
        }
    }

    /**
     * Get the inherited permission by code. e.g. `system.menu` inherits from `system`
     *
     * @param permCode top perm code
     * @param permAll  all perms id-code map
     * @return inherited perms
     */
    @NotNull
    public static Set<String> inheritPerm(String permCode, Map<Long, String> permAll) {
        if (permAll == null || permCode == null) return Collections.emptySet();
        return permAll.values()
                      .stream()
                      .filter(it -> PermGrantHelper.canInherit(permCode, it))
                      .collect(Collectors.toSet());
    }

    /**
     * Get the inherited permission by id. e.g. `system.menu` inherits from `system`
     *
     * @param permId  top perm id
     * @param permAll  all perms id-code map
     * @return inherited perms
     */
    @NotNull
    public static Set<String> inheritPerm(long permId, Map<Long, String> permAll) {
        return inheritPerm(permAll.get(permId), permAll);
    }

    /**
     * Get authorized roles by id
     *
     * @param roleId    top role id
     * @param roleAll   all roles id-name map
     * @param roleGrant id-Set[id] map with inheritance
     * @return inherited roles
     */
    @NotNull
    public static Set<String> grantRole(long roleId, Map<Long, String> roleAll, Map<Long, Set<Long>> roleGrant) {
        if (roleAll == null || roleGrant == null) return Collections.emptySet();

        Map<Long, String> out = new HashMap<>();
        recur(roleId, out, roleAll, roleGrant);

        return new HashSet<>(out.values());
    }

    private static void recur(Long rid, Map<Long, String> out, Map<Long, String> all, Map<Long, Set<Long>> map) {

        final String rcd = all.get(rid);
        if (rcd == null) return;

        out.put(rid, rcd);
        final Set<Long> sub = map.getOrDefault(rid, Collections.emptySet());
        for (Long id : sub) {
            if (!out.containsKey(id)) {
                recur(id, out, all, map);
            }
        }
    }
}
