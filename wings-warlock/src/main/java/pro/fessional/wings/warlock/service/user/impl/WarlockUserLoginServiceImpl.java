package pro.fessional.wings.warlock.service.user.impl;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.fessional.mirana.page.PageQuery;
import pro.fessional.wings.warlock.database.autogen.tables.WinUserLoginTable;
import pro.fessional.wings.warlock.database.autogen.tables.daos.WinUserLoginDao;
import pro.fessional.wings.warlock.service.user.WarlockUserLoginService;

import java.util.List;

/**
 * @author trydofor
 * @since 2021-03-26
 */

@Service
@Slf4j
public class WarlockUserLoginServiceImpl implements WarlockUserLoginService {

    @Setter(onMethod_ = {@Autowired})
    private WinUserLoginDao winUserLoginDao;

    @Override
    public @NotNull List<Item> list(long userId, PageQuery query) {
        final WinUserLoginTable t = winUserLoginDao.getTable();
        return winUserLoginDao
                .ctx()
                .select(t.AuthType, t.LoginIp, t.LoginDt, t.Terminal, t.Failed)
                .from(t)
                .orderBy(t.LoginDt.desc())
                .limit(query.toOffset(), query.getSize())
                .fetch()
                .into(Item.class);
    }
}
