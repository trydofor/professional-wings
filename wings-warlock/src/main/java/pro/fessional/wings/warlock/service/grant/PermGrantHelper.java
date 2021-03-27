package pro.fessional.wings.warlock.service.grant;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 根权限是空，这样unite之后是 `.*`。
 * inherit指，使用`.`分隔的scope，存在隐式的继承关系。
 * grant值，绑定授权。
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
        } else if (p == 0) {
            return new String[]{"", permit.substring(1)};
        } else {
            return new String[]{"", permit};
        }
    }

    /**
     * top 是否包括了 sub.
     * `*` 包括所有，同名的action作比较
     *
     * @param top system.*
     * @param sub system.menu.read
     * @return 包含
     */
    public static boolean canInherit(String top, String sub) {
        return canInherit(top, sub, SPL);
    }

    /**
     * 不区分大小写，top 是否包括了 sub.
     * `*` 包括所有，同名的action作比较
     *
     * @param top system.*
     * @param sub system.menu.read
     * @param spl 分隔符，默认`.`
     * @return 包含
     */
    public static boolean canInherit(String top, String sub, String spl) {
        if (top == null || sub == null) return false;
        if (spl == null) spl = SPL;

        int t1 = top.lastIndexOf(spl);
        if (t1 < 0) {
            if (top.equals(ALL)) {
                return true;
            } else {
                return top.equalsIgnoreCase(sub);
            }
        } else {
            final int hl = t1 + 1;
            if (top.regionMatches(hl, ALL, 0, ALL.length())) {
                return t1 == 0 || sub.regionMatches(true, 0, top, 0, hl);
            } else {
                int s1 = sub.lastIndexOf(spl);
                if (s1 < 0) {
                    return false;
                } else {
                    return sub.regionMatches(true, 0, top, 0, hl)
                            && sub.regionMatches(true, s1 + 1, top, hl, top.length() - hl);
                }
            }
        }
    }

    /**
     * 通过id，获得继承的权限码，system.menu 继承于 system
     *
     * @param permCode code
     * @param permAll  所有权限id-code
     * @return 包括当前id的继承权限码
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
     * 通过id，获得继承的权限码，system.menu 继承于 system
     *
     * @param permId  id
     * @param permAll 所有权限id-code
     * @return 包括当前id的继承权限码
     */
    @NotNull
    public static Set<String> inheritPerm(long permId, Map<Long, String> permAll) {
        return inheritPerm(permAll.get(permId), permAll);
    }

    /**
     * 通过id，获得授权的角色
     *
     * @param roleId    role
     * @param roleAll   所有角色id-name
     * @param roleGrant 拥有权限继承id-Set[id]
     * @return 包括当前id的继承角色码
     */
    @NotNull
    public static Set<String> grantRole(long roleId, Map<Long, String> roleAll, Map<Long, Set<Long>> roleGrant) {
        if (roleAll == null || roleGrant == null) return Collections.emptySet();

        Set<String> all = new HashSet<>();
        recur(0, roleId, all, roleAll, roleGrant);

        return all;
    }

    private static void recur(int cnt, Long rid, Set<String> out, Map<Long, String> all, Map<Long, Set<Long>> map) {
        if (rid == null) return;

        if (cnt > 10_0000) throw new IllegalStateException("may be dead loop in role mapping");

        final String rcd = all.get(rid);
        if (rcd == null) return;

        out.add(rcd);
        final Set<Long> sub = map.getOrDefault(rid, Collections.emptySet());
        for (Long id : sub) {
            recur(cnt + 1, id, out, all, map);
        }
    }
}
