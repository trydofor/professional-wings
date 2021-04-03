package pro.fessional.wings.warlock.service.grant;

import org.jetbrains.annotations.NotNull;
import pro.fessional.wings.warlock.enums.autogen.GrantType;

import java.util.Arrays;
import java.util.Collection;

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
    void grantRole(long refer, @NotNull GrantType type, @NotNull Collection<Long> grant);

    /**
     * @see #grantRole(long, GrantType, Collection)
     */
    default void grantRole(long refer, @NotNull GrantType type, @NotNull Long... grant) {
        grantRole(refer, type, Arrays.asList(grant));
    }


    /**
     * 撤销refer的grant，使refer不在拥有grant权限
     *
     * @param refer roleId
     * @param type  类型
     * @param grant RoleId or PermId
     */
    void purgeRole(long refer, @NotNull GrantType type, @NotNull Collection<Long> grant);

    /**
     * @see #purgeRole(long, GrantType, Collection)
     */
    default void purgeRole(long refer, @NotNull GrantType type, @NotNull Long... grant) {
        purgeRole(refer, type, Arrays.asList(grant));
    }

    /**
     * 把grant赋予refer，使refer拥有grant权限
     *
     * @param refer userId
     * @param type  类型
     * @param grant RoleId or PermId
     */
    void grantUser(long refer, @NotNull GrantType type, @NotNull Collection<Long> grant);

    /**
     * @see #grantUser(long, GrantType, Collection)
     */
    default void grantUser(long refer, @NotNull GrantType type, @NotNull Long... grant) {
        grantUser(refer, type, Arrays.asList(grant));
    }

    /**
     * 撤销refer的grant，使refer不在拥有grant权限
     *
     * @param refer userId
     * @param type  类型
     * @param grant RoleId or PermId
     */
    void purgeUser(long refer, @NotNull GrantType type, @NotNull Collection<Long> grant);

    /**
     * @see #purgeUser(long, GrantType, Collection)
     */
    default void purgeUser(long refer, @NotNull GrantType type, @NotNull Long... grant) {
        purgeUser(refer, type, Arrays.asList(grant));
    }
}
