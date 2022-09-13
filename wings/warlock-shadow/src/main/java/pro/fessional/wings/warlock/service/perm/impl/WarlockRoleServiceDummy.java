package pro.fessional.wings.warlock.service.perm.impl;

import org.jetbrains.annotations.NotNull;
import pro.fessional.wings.warlock.service.perm.WarlockRoleService;

import java.util.Collections;
import java.util.Map;

/**
 * @author trydofor
 * @since 2022-07-11
 */
public class WarlockRoleServiceDummy implements WarlockRoleService {
    @Override
    public Map<Long, String> loadRoleAll() {
        return Collections.emptyMap();
    }

    @Override
    public long create(@NotNull String name, String remark) {
        throw new UnsupportedOperationException("Dummy Service");
    }

    @Override
    public void modify(long roleId, String remark) {
        throw new UnsupportedOperationException("Dummy Service");
    }
}
