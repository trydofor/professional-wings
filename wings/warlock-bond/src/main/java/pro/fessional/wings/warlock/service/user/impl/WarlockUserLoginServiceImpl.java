package pro.fessional.wings.warlock.service.user.impl;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import pro.fessional.mirana.page.PageQuery;
import pro.fessional.wings.faceless.service.lightid.LightIdService;
import pro.fessional.wings.slardar.context.Now;
import pro.fessional.wings.slardar.context.TerminalContext;
import pro.fessional.wings.slardar.security.WingsAuthTypeParser;
import pro.fessional.wings.warlock.database.autogen.tables.WinUserLoginTable;
import pro.fessional.wings.warlock.database.autogen.tables.daos.WinUserLoginDao;
import pro.fessional.wings.warlock.database.autogen.tables.pojos.WinUserLogin;
import pro.fessional.wings.warlock.service.user.WarlockUserLoginService;

import java.util.Collections;
import java.util.List;

/**
 * @author trydofor
 * @since 2021-03-26
 */
@Slf4j
public class WarlockUserLoginServiceImpl implements WarlockUserLoginService {

    @Setter(onMethod_ = {@Autowired})
    protected WinUserLoginDao winUserLoginDao;

    @Setter(onMethod_ = {@Autowired})
    protected WingsAuthTypeParser wingsAuthTypeParser;

    @Setter(onMethod_ = {@Autowired})
    protected LightIdService lightIdService;

    @Override
    public @NotNull List<Item> list(long userId, PageQuery query) {
        if (winUserLoginDao.notTableExist()) return Collections.emptyList();

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

    @Override
    public void auth(Auth auth) {
        if (winUserLoginDao.notTableExist()) return;

        WinUserLogin po = new WinUserLogin();
        po.setId(lightIdService.getId(winUserLoginDao.getTable()));

        po.setUserId(auth.getUserId());
        po.setLoginDt(Now.localDateTime());
        po.setDetails(auth.getDetails());
        po.setFailed(auth.isFailed());

        final String at = wingsAuthTypeParser.parse(auth.getAuthType());
        po.setAuthType(at);

        final TerminalContext.Context tc = TerminalContext.get();
        po.setLoginIp(tc.getTerminal(TerminalContext.RemoteIp));
        po.setTerminal(tc.getTerminal(TerminalContext.AgentInfo));
        winUserLoginDao.insert(po);
    }
}
