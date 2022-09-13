package pro.fessional.wings.warlock.service.user.impl;

import org.jetbrains.annotations.NotNull;
import pro.fessional.wings.warlock.service.user.WarlockUserBasisService;

/**
 * @author trydofor
 * @since 2022-07-11
 */
public class WarlockUserBasisServiceDummy implements WarlockUserBasisService {

    @Override
    public long create(@NotNull Basis user) {
        throw new UnsupportedOperationException("Dummy Service");
    }

    @Override
    public void modify(long userId, @NotNull Basis user) {
        throw new UnsupportedOperationException("Dummy Service");
    }
}
