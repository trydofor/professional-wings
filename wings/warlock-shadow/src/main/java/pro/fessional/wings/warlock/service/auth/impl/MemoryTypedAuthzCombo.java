package pro.fessional.wings.warlock.service.auth.impl;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import pro.fessional.mirana.data.Null;
import pro.fessional.wings.slardar.security.impl.DefaultWingsUserDetails;
import pro.fessional.wings.warlock.constants.WarlockOrderConst;
import pro.fessional.wings.warlock.service.perm.WarlockPermNormalizer;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 根据username和authType等对授权进行增减。
 * Role和Perm不进行区分，需要自行指定Role前缀。
 *
 * @author trydofor
 * @since 2021-03-05
 */
@Slf4j
public class MemoryTypedAuthzCombo implements ComboWarlockAuthzService.Combo {

    @Getter @Setter
    private int order = WarlockOrderConst.MemoryTypedAuthzCombo;

    @Setter(onMethod_ = {@Autowired})
    protected WarlockPermNormalizer permNormalizer;

    private final Map<Long, Set<String>> userAuthz = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> namedAuthz = new ConcurrentHashMap<>();
    private final Map<String, Map<Enum<?>, Set<String>>> typedAuthz = new ConcurrentHashMap<>();

    /**
     * 按userId，授予权限和角色
     *
     * @param userId 用户id
     * @param authz  权限和角色
     */
    public void addAuthz(long userId, @NotNull String... authz) {
        addAuthz(userId, Arrays.asList(authz));
    }

    /**
     * 按userId，授予权限和角色
     *
     * @param userId 用户id
     * @param authz  权限和角色
     */
    public void addAuthz(long userId, @NotNull Collection<String> authz) {
        final Set<String> set = userAuthz.computeIfAbsent(userId, k -> new CopyOnWriteArraySet<>());
        set.addAll(authz);
    }

    /**
     * 按登录名，授予权限和角色
     *
     * @param username 登录名
     * @param authz    权限和角色
     */
    public void addAuthz(@NotNull String username, @NotNull String... authz) {
        addAuthz(username, Arrays.asList(authz));
    }

    /**
     * 按登录名，授予权限和角色
     *
     * @param username 登录名
     * @param authz    权限和角色
     */
    public void addAuthz(@NotNull String username, @NotNull Collection<String> authz) {
        addAuthz(username, null, authz);
    }

    /**
     * 按登录名和类型，授予权限和角色
     *
     * @param username 登录名
     * @param authType 类型, null时，仅按登录名
     * @param authz    权限和角色
     */
    public void addAuthz(@NotNull String username, Enum<?> authType, @NotNull String... authz) {
        addAuthz(username, authType, Arrays.asList(authz));
    }

    /**
     * 按登录名和类型，授予权限和角色
     *
     * @param username 登录名
     * @param authType 类型, null时，仅按登录名
     * @param authz    权限和角色
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
     * 按userId，删除所有授权
     *
     * @param userId 用户id
     */
    public void delAuthz(long userId) {
        userAuthz.remove(userId);
    }

    /**
     * 按userId，删除指定授权
     *
     * @param userId 用户id
     * @param authz  指定类型
     */
    public void delAuthz(long userId, @NotNull Collection<String> authz) {
        final Set<String> set = userAuthz.get(userId);
        if (set != null) {
            set.removeAll(authz);
        }
    }

    /**
     * 按登录名，删除所有授权
     */
    public void delAuthz(@NotNull String username) {
        namedAuthz.remove(username);
    }

    /**
     * 按登录名，删除指定授权
     *
     * @param username 登录名
     * @param authz    指定类型
     */
    public void delAuthz(@NotNull String username, @NotNull Collection<String> authz) {
        final Set<String> set = namedAuthz.get(username);
        if (set != null) {
            set.removeAll(authz);
        }
    }

    /**
     * 按登录名和类型，删除所有授权
     *
     * @param username 登录名
     * @param authType 登录类型, null时，按登录名授权
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
     * 按登录名和类型，删除指定授权
     *
     * @param username 登录名
     * @param authType 登录类型, null时，按登录名授权
     * @param authz    指定类型
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
