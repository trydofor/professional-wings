package pro.fessional.wings.warlock.service.user.impl;

import org.jetbrains.annotations.NotNull;
import pro.fessional.wings.warlock.service.user.WarlockUserAuthnService;

import java.util.Collections;
import java.util.List;

/**
 * @author trydofor
 * @since 2022-07-11
 */
public class WarlockUserAuthnServiceDummy implements WarlockUserAuthnService {
    @Override
    public long create(long userId, @NotNull Authn authn) {
        throw new UnsupportedOperationException("Dummy Service");
    }

    @Override
    public void modify(long userId, @NotNull Authn authn) {
        throw new UnsupportedOperationException("Dummy Service");
    }

    @Override
    public void renew(long userId, @NotNull Renew renew) {
        throw new UnsupportedOperationException("Dummy Service");
    }

    @Override
    public void dander(long userId, boolean danger, @NotNull Enum<?>... authType) {
        throw new UnsupportedOperationException("Dummy Service");
    }

    @Override
    public @NotNull List<Item> list(long userId) {
        return Collections.emptyList();
    }
}
