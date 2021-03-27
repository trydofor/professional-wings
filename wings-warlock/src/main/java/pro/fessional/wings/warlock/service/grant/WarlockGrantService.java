package pro.fessional.wings.warlock.service.grant;

import org.jetbrains.annotations.NotNull;
import pro.fessional.wings.warlock.enums.autogen.GrantType;

import java.util.Set;

/**
 * 授权与撤销，涉及到权限提升，必须结合GrantChecker使用
 * - role拥有的perm
 * - role拥有的role
 * - user拥有的perm
 * - user拥有的role
 *
 * @author trydofor
 * @since 2021-03-05
 */
public interface WarlockGrantService {

    enum Jane {
        Grant,
        Purge,
    }

    /**
     * 把grant赋予refer，使refer拥有grant权限
     *
     * @param refer roleId
     * @param type  类型
     * @param grant RoleId or PermId
     */
    void grantRole(long refer, @NotNull GrantType type, @NotNull Set<Long> grant);

    /**
     * 撤销refer的grant，使refer不在拥有grant权限
     *
     * @param refer roleId
     * @param type  类型
     * @param grant RoleId or PermId
     */
    void purgeRole(long refer, @NotNull GrantType type, @NotNull Set<Long> grant);

    /**
     * 把grant赋予refer，使refer拥有grant权限
     *
     * @param refer userId
     * @param type  类型
     * @param grant RoleId or PermId
     */
    void grantUser(long refer, @NotNull GrantType type, @NotNull Set<Long> grant);

    /**
     * 撤销refer的grant，使refer不在拥有grant权限
     *
     * @param refer userId
     * @param type  类型
     * @param grant RoleId or PermId
     */
    void purgeUser(long refer, @NotNull GrantType type, @NotNull Set<Long> grant);
}
