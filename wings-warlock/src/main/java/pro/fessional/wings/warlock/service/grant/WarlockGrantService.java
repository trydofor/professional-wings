package pro.fessional.wings.warlock.service.grant;

import org.jetbrains.annotations.NotNull;
import pro.fessional.wings.warlock.enums.autogen.GrantType;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

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
     * @param roleId roleId
     * @param type   类型
     * @param grant  RoleId or PermId
     */
    void grantRole(long roleId, @NotNull GrantType type, @NotNull Collection<Long> grant);

    /**
     * @see #grantRole(long, GrantType, Collection)
     */
    default void grantRole(long refer, @NotNull GrantType type, @NotNull Long... grant) {
        grantRole(refer, type, Arrays.asList(grant));
    }


    /**
     * 撤销refer的grant，使refer不在拥有grant权限
     *
     * @param roleId roleId
     * @param type   类型
     * @param grant  RoleId or PermId
     */
    void purgeRole(long roleId, @NotNull GrantType type, @NotNull Collection<Long> grant);

    /**
     * @see #purgeRole(long, GrantType, Collection)
     */
    default void purgeRole(long roleId, @NotNull GrantType type, @NotNull Long... grant) {
        purgeRole(roleId, type, Arrays.asList(grant));
    }

    /**
     * 把grant赋予refer，使refer拥有grant权限
     *
     * @param userId userId
     * @param type   类型
     * @param grant  RoleId or PermId
     */
    void grantUser(long userId, @NotNull GrantType type, @NotNull Collection<Long> grant);

    /**
     * @see #grantUser(long, GrantType, Collection)
     */
    default void grantUser(long userId, @NotNull GrantType type, @NotNull Long... grant) {
        grantUser(userId, type, Arrays.asList(grant));
    }

    /**
     * 撤销refer的grant，使refer不在拥有grant权限
     *
     * @param userId userId
     * @param type   类型
     * @param grant  RoleId or PermId
     */
    void purgeUser(long userId, @NotNull GrantType type, @NotNull Collection<Long> grant);

    /**
     * @see #purgeUser(long, GrantType, Collection)
     */
    default void purgeUser(long userId, @NotNull GrantType type, @NotNull Long... grant) {
        purgeUser(userId, type, Arrays.asList(grant));
    }

    /**
     * 获取用户的授权，返回key=授权，value=userId
     *
     * @param type   类型
     * @param userId userId
     * @return RoleId/PermId - UserId
     */
    Map<Long, Long> entryUser(@NotNull GrantType type, @NotNull Collection<Long> userId);

    /**
     * @see #entryUser(GrantType, Collection)
     */
    default Map<Long, Long> entryUser(@NotNull GrantType type, @NotNull Long... userId) {
        return entryUser(type, Arrays.asList(userId));
    }

    /**
     * 获取角色的授权，返回key=授权，value=roleId
     *
     * @param type   类型
     * @param roleId roleId
     * @return RoleId/PermId - roleId
     */
    Map<Long, Long> entryRole(@NotNull GrantType type, @NotNull Collection<Long> roleId);

    /**
     * @see #entryRole(GrantType, Collection)
     */
    default Map<Long, Long> entryRole(@NotNull GrantType type, @NotNull Long... roleId) {
        return entryRole(type, Arrays.asList(roleId));
    }
}
