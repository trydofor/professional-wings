package pro.fessional.wings.warlock.service.auth.impl;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import pro.fessional.mirana.data.Null;
import pro.fessional.wings.slardar.security.impl.DefaultWingsUserDetails;
import pro.fessional.wings.warlock.service.perm.WarlockPermNormalizer;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Add/remove auth by username and authType.
 * Role and Perm are not distinguished, and the Role need a fixed prefix.
 *
 * @author trydofor
 * @since 2021-03-05
 */
@Slf4j
@Getter @Setter
public class MemoryTypedAuthzCombo implements ComboWarlockAuthzService.Combo {

    public static final int ORDER = DefaultPermRoleCombo.ORDER + 100;

    private int order = ORDER;

    @Setter(onMethod_ = {@Autowired})
    protected WarlockPermNormalizer permNormalizer;

    private final Map<Long, Set<String>> userAuthz = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> namedAuthz = new ConcurrentHashMap<>();
    private final Map<String, Map<Enum<?>, Set<String>>> typedAuthz = new ConcurrentHashMap<>();

    /**
     * Add Perm/Role to userId
     *
     * @param userId user id
     * @param authz  Perm/Role
     */
    public void addAuthz(long userId, @NotNull String... authz) {
        addAuthz(userId, Arrays.asList(authz));
    }

    /**
     * Add Perm/Role to userId
     *
     * @param userId user id
     * @param authz  Perm/Role
     */
    public void addAuthz(long userId, @NotNull Collection<String> authz) {
        final Set<String> set = userAuthz.computeIfAbsent(userId, k -> new CopyOnWriteArraySet<>());
        set.addAll(authz);
    }

    /**
     * Add Perm/Role to user by username
     *
     * @param username login username
     * @param authz    Perm/Role
     */
    public void addAuthz(@NotNull String username, @NotNull String... authz) {
        addAuthz(username, Arrays.asList(authz));
    }

    /**
     * Add Perm/Role to user by username
     *
     * @param username login username
     * @param authz    Perm/Role
     */
    public void addAuthz(@NotNull String username, @NotNull Collection<String> authz) {
        addAuthz(username, null, authz);
    }

    /**
     * Add Perm/Role to user by username and authType
     *
     * @param username login username
     * @param authType only username if null
     * @param authz    Perm/Role
     */
    public void addAuthz(@NotNull String username, Enum<?> authType, @NotNull String... authz) {
        addAuthz(username, authType, Arrays.asList(authz));
    }

    /**
     * Add Perm/Role to user by username and authType
     *
     * @param username login username
     * @param authType only username if null
     * @param authz    Perm/Role
     */
    public void addAuthz(@NotNull String username, Enum<?> authType, @NotNull Collection<String> authz) {
        if (authType == null || authType == Null.Enm) {
            final Set<String> set = namedAuthz.computeIfAbsent(username, k -> new CopyOnWriteArraySet<>());
            set.addAll(authz);
        }
        else {
            final Map<Enum<?>, Set<String>> map = typedAuthz.computeIfAbsent(username, k -> new ConcurrentHashMap<>());
            final Set<String> set = map.computeIfAbsent(authType, k -> new CopyOnWriteArraySet<>());
            set.addAll(authz);
        }
    }

    /**
     * delete all Perm/Role of userId
     */
    public void delAuthz(long userId) {
        userAuthz.remove(userId);
    }

    /**
     * delete given Perm/Role of userId
     *
     * @param userId userid
     * @param authz  given Perm/Role
     */
    public void delAuthz(long userId, @NotNull Collection<String> authz) {
        final Set<String> set = userAuthz.get(userId);
        if (set != null) {
            set.removeAll(authz);
        }
    }

    /**
     * delete all Perm/Role of user by login username
     */
    public void delAuthz(@NotNull String username) {
        namedAuthz.remove(username);
    }

    /**
     * delete given Perm/Role of user by login username
     *
     * @param username login username
     * @param authz    given Perm/Role
     */
    public void delAuthz(@NotNull String username, @NotNull Collection<String> authz) {
        final Set<String> set = namedAuthz.get(username);
        if (set != null) {
            set.removeAll(authz);
        }
    }

    /**
     * delete all Perm/Role of user by login username and authType
     *
     * @param username login username
     * @param authType only username if null
     */
    public void delAuthz(@NotNull String username, Enum<?> authType) {
        if (authType == null) {
            delAuthz(username);
        }
        else {
            final Map<Enum<?>, Set<String>> map = typedAuthz.get(username);
            if (map != null) {
                map.remove(authType);
            }
        }
    }

    /**
     * delete given Perm/Role of user by login username and authType
     *
     * @param username login username
     * @param authType only username if null
     * @param authz    given Perm/Role
     */
    public void delAuthz(@NotNull String username, Enum<?> authType, @NotNull Collection<String> authz) {
        if (authType == null) {
            delAuthz(username, authz);
        }
        else {
            final Map<Enum<?>, Set<String>> map = typedAuthz.get(username);
            if (map != null) {
                final Set<String> set = map.get(authType);
                if (set != null) {
                    set.removeAll(authz);
                }
            }
        }
    }

    @Override
    public boolean preAuth(@NotNull DefaultWingsUserDetails details, @NotNull HashSet<Object> role, @NotNull HashSet<Object> perm) {

        final Set<String> uaz = userAuthz.get(details.getUserId());
        if (uaz != null) {
            for (String s : uaz) {
                if (permNormalizer.indexRolePrefix(s) < 0) {
                    perm.add(s);
                    log.debug("add uid-perm={}", s);
                }
                else {
                    role.add(s);
                    log.debug("add uid-role={}", s);
                }
            }
        }

        final Set<String> naz = namedAuthz.get(details.getUsername());
        if (naz != null) {
            for (String s : naz) {
                if (permNormalizer.indexRolePrefix(s) < 0) {
                    perm.add(s);
                    log.debug("add name-perm={}", s);
                }
                else {
                    role.add(s);
                    log.debug("add name-role={}", s);
                }
            }
        }

        final Map<Enum<?>, Set<String>> taz = typedAuthz.get(details.getUsername());
        if (taz != null) {
            final Enum<?> at = details.getAuthType();
            final Set<String> az2 = taz.get(at);
            if (az2 != null) {
                for (String s : az2) {
                    if (permNormalizer.indexRolePrefix(s) < 0) {
                        perm.add(s);
                        log.debug("add type-perm={}, type={}", s, at);
                    }
                    else {
                        role.add(s);
                        log.debug("add type-role={}, type={}", s, at);
                    }
                }
            }
        }

        return false;
    }
}
