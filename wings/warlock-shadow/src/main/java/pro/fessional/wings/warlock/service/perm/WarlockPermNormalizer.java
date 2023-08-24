package pro.fessional.wings.warlock.service.perm;

import lombok.Getter;
import lombok.Setter;

/**
 * Normalize role, add some prefix
 *
 * @author trydofor
 * @since 2021-06-08
 */
public class WarlockPermNormalizer {

    public static final String DenyPrefix = "-";

    @Setter @Getter
    private String rolePrefix = "ROLE_";

    /**
     * Normalize role, add `DenyPrefix` or `rolePrefix`
     *
     * @see #DenyPrefix
     * @see #getRolePrefix()
     */
    public String role(String name) {
        int p = name.startsWith(DenyPrefix) ? DenyPrefix.length() : 0;
        if (p == 0) {
            return name.startsWith(rolePrefix) ? name : rolePrefix + name;
        }
        else {
            final int v = name.indexOf(rolePrefix, p);
            if (v == p) {
                return name;
            }
            else {
                return DenyPrefix + rolePrefix + name.substring(p);
            }
        }
    }

    /**
     * Calc the index of `DenyPrefix` role.
     *
     * @param name role string
     * @return `-1` means no DenyPrefix
     */
    public int indexDenyPrefix(String name) {
        return name.startsWith(DenyPrefix) ? DenyPrefix.length() : -1;
    }

    /**
     * Cale index of `rolePrefix`
     *
     * @param name role string
     * @return `-1` means no rolePrefix
     */
    public int indexRolePrefix(String name) {
        int p = name.startsWith(DenyPrefix) ? DenyPrefix.length() : 0;
        final int r = name.indexOf(rolePrefix, p);
        return r == p ? r + rolePrefix.length() : -1;
    }
}
