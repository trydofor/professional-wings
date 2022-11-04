package pro.fessional.wings.warlock.service.perm;

import lombok.Getter;
import lombok.Setter;

/**
 * 加工role，增加prefix
 *
 * @author trydofor
 * @since 2021-06-08
 */
public class WarlockPermNormalizer {

    public static final String DenyPrefix = "-";

    @Setter @Getter
    private String rolePrefix = "ROLE_";

    /**
     * 标准化role，增加`负项`或`前缀`
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
     * 计算`负项`的index, -1表示非排除项
     *
     * @param name 角色字符串
     * @return -1表示非排除项。
     */
    public int indexDenyPrefix(String name) {
        return name.startsWith(DenyPrefix) ? DenyPrefix.length() : -1;
    }

    /**
     * 计算`前缀`index
     *
     * @param name 角色字符串
     * @return -1表示无前缀排除项。
     */
    public int indexRolePrefix(String name) {
        int p = name.startsWith(DenyPrefix) ? DenyPrefix.length() : 0;
        final int r = name.indexOf(rolePrefix, p);
        return r == p ? r + rolePrefix.length() : -1;
    }
}
