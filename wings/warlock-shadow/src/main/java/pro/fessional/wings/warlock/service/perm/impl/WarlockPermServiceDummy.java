package pro.fessional.wings.warlock.service.perm.impl;

import org.jetbrains.annotations.NotNull;
import pro.fessional.wings.warlock.service.perm.WarlockPermService;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * @author trydofor
 * @since 2022-07-11
 */
public class WarlockPermServiceDummy implements WarlockPermService {
    @Override
    public Map<Long, String> loadPermAll() {
        return Collections.emptyMap();
    }

    @Override
    public void create(@NotNull String scopes, @NotNull Collection<Act> acts) {
        throw new UnsupportedOperationException("Dummy Service");
    }

    @Override
    public void modify(long permId, @NotNull String remark) {
        throw new UnsupportedOperationException("Dummy Service");
    }
}
