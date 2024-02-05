package pro.fessional.wings.warlock.service.grant.impl;

import org.jetbrains.annotations.NotNull;
import pro.fessional.wings.warlock.enums.autogen.GrantType;
import pro.fessional.wings.warlock.service.grant.WarlockGrantService;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * @author trydofor
 * @since 2022-07-11
 */
public class WarlockGrantServiceDummy implements WarlockGrantService {
    @Override
    public void grantRole(long roleId, @NotNull GrantType type, @NotNull Collection<Long> grant) {
        throw new UnsupportedOperationException("Dummy Service");
    }

    @Override
    public void purgeRole(long roleId, @NotNull GrantType type, @NotNull Collection<Long> grant) {
        throw new UnsupportedOperationException("Dummy Service");
    }

    @Override
    public void grantUser(long userId, @NotNull GrantType type, @NotNull Collection<Long> grant) {
        throw new UnsupportedOperationException("Dummy Service");
    }

    @Override
    public void purgeUser(long userId, @NotNull GrantType type, @NotNull Collection<Long> grant) {
        throw new UnsupportedOperationException("Dummy Service");
    }

    @Override
    public Map<Long, Set<Long>> entryUser(@NotNull GrantType type, @NotNull Collection<Long> userId) {
        return Collections.emptyMap();
    }

    @Override
    public Map<Long, Set<Long>> entryRole(@NotNull GrantType type, @NotNull Collection<Long> roleId) {
        return Collections.emptyMap();
    }
}
