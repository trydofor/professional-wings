package pro.fessional.wings.warlock.service.grant;

import org.jetbrains.annotations.NotNull;
import pro.fessional.wings.warlock.enums.autogen.GrantType;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * <pre>
 * Authorization and revocation, which involves elevation of privilege, must be used with GrantChecker
 * - Role-owned perm
 * - Role-owned role
 * - User-owned perm
 * - user-owned role
 * </pre>
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
     * Grant role/perm to role to give the `grant` privilege.
     *
     * @param roleId roleId
     * @param type   grant type
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
     * Revoke role/perm to role to remove the `grant` privilege.
     *
     * @param roleId roleId
     * @param type   grant type
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
     * Grant role/perm to user to give the `grant` privilege.
     *
     * @param userId userId
     * @param type   grant type
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
     * Revoke role/perm to user to remove the `grant` privilege.
     *
     * @param userId userId
     * @param type   grant type
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
     * Get user granted privileges. return the map of RoleId/PermId to UserId
     *
     * @param type   grant type
     * @param userId userId
     * @return RoleId/PermId - UserId
     */
    Map<Long, Set<Long>> entryUser(@NotNull GrantType type, @NotNull Collection<Long> userId);

    /**
     * @see #entryUser(GrantType, Collection)
     */
    default Map<Long, Set<Long>> entryUser(@NotNull GrantType type, @NotNull Long... userId) {
        return entryUser(type, Arrays.asList(userId));
    }

    /**
     * Get role granted privileges. return the map of RoleId/PermId to UserId
     *
     * @param type   grant type
     * @param roleId roleId
     * @return RoleId/PermId - roleId
     */
    Map<Long, Set<Long>> entryRole(@NotNull GrantType type, @NotNull Collection<Long> roleId);

    /**
     * @see #entryRole(GrantType, Collection)
     */
    default Map<Long, Set<Long>> entryRole(@NotNull GrantType type, @NotNull Long... roleId) {
        return entryRole(type, Arrays.asList(roleId));
    }
}
