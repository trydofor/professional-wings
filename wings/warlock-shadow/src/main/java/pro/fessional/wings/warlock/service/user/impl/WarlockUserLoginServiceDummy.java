package pro.fessional.wings.warlock.service.user.impl;

import org.jetbrains.annotations.NotNull;
import pro.fessional.mirana.page.PageQuery;
import pro.fessional.wings.warlock.service.user.WarlockUserLoginService;

import java.util.Collections;
import java.util.List;

/**
 * @author trydofor
 * @since 2022-07-11
 */
public class WarlockUserLoginServiceDummy implements WarlockUserLoginService {
    @Override
    public @NotNull List<Item> list(long userId, PageQuery query) {
        return Collections.emptyList();
    }

    @Override
    public void auth(Auth auth) {
    }
}
