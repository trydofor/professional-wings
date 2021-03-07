package pro.fessional.wings.warlock.service.perm;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author trydofor
 * @since 2021-03-07
 */
public class PermInheritHelper {

    public static final String ALL = "*";
    public static final String SPL = ".";


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
                      .filter(it -> PermInheritHelper.canInherit(permCode, it))
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
     * 通过id，获得继承的角色
     *
     * @param roleId  role
     * @param roleAll 所有角色id-name
     * @param roleMap 所有权限继承id-id
     * @return 包括当前id的继承角色码
     */
    @NotNull
    public static Set<String> inheritRole(long roleId, Map<Long, String> roleAll, Map<Long, Set<Long>> roleMap) {
        if (roleAll == null || roleMap == null) return Collections.emptySet();

        Set<String> all = new HashSet<>();
        recur(0, roleId, all, roleAll, roleMap);

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
