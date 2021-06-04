package pro.fessional.wings.warlock.service.auth.impl;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.GrantedAuthority;
import pro.fessional.wings.slardar.security.impl.DefaultWingsUserDetails;
import pro.fessional.wings.warlock.constants.WarlockOrderConst;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 根据username和authType等对授权进行增减。
 * Role和Perm不进行区分，且不进行递归操作
 *
 * @author trydofor
 * @since 2021-03-05
 */
@Slf4j
public class MemoryTypedAuthzCombo implements ComboWarlockAuthzService.Combo {

    public static final int ORDER = WarlockOrderConst.UserAuthzCombo + 11_000;

    @Getter @Setter
    private int order = ORDER;

    private final Map<String, Set<GrantedAuthority>> namedAuthz = new ConcurrentHashMap<>();
    private final Map<String, Map<Enum<?>, Set<GrantedAuthority>>> typedAuthz = new ConcurrentHashMap<>();

    public void addAuthz(String username, GrantedAuthority... authz) {
        addAuthz(username, Arrays.asList(authz));
    }

    public void addAuthz(String username, Enum<?> authType, GrantedAuthority... authz) {
        addAuthz(username, authType, Arrays.asList(authz));
    }

    public void addAuthz(String username, Collection<GrantedAuthority> authz) {
        addAuthz(username, null, authz);
    }

    public void addAuthz(String username, Enum<?> authType, Collection<GrantedAuthority> authz) {
        if (authType == null) {
            final Set<GrantedAuthority> set = namedAuthz.computeIfAbsent(username, k -> new CopyOnWriteArraySet<>());
            set.addAll(authz);
        }
        else {
            final Map<Enum<?>, Set<GrantedAuthority>> map = typedAuthz.computeIfAbsent(username, k -> new ConcurrentHashMap<>());
            final Set<GrantedAuthority> set = map.computeIfAbsent(authType, k -> new CopyOnWriteArraySet<>());
            set.addAll(authz);
        }
    }

    public void delAuthz(String username, GrantedAuthority... authz) {
        delAuthz(username, Arrays.asList(authz));
    }

    public void delAuthz(String username, Enum<?> authType, GrantedAuthority... authz) {
        delAuthz(username, authType, Arrays.asList(authz));
    }

    public void delAuthz(String username, Collection<GrantedAuthority> authz) {
        delAuthz(username, null, authz);
    }

    public void delAuthz(String username, Enum<?> authType, Collection<GrantedAuthority> authz) {

    }

    @Override
    public void auth(@NotNull DefaultWingsUserDetails details) {
        final Collection<GrantedAuthority> granted = details.getAuthorities();

        final Set<GrantedAuthority> az1 = namedAuthz.get(details.getUsername());
        if (az1 != null) {
            granted.addAll(az1);
        }

        final Map<Enum<?>, Set<GrantedAuthority>> tpa = typedAuthz.get(details.getUsername());
        if (tpa != null) {
            final Set<GrantedAuthority> az2 = tpa.get(details.getAuthType());
            if (az2 != null) {
                granted.addAll(az2);
            }
        }
    }
}
