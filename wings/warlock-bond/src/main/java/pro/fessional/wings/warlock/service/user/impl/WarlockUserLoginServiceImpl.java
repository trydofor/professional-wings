package pro.fessional.wings.warlock.service.user.impl;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import pro.fessional.mirana.page.PageQuery;
import pro.fessional.mirana.page.PageResult;
import pro.fessional.wings.faceless.database.jooq.helper.PageJooqHelper;
import pro.fessional.wings.faceless.service.lightid.LightIdService;
import pro.fessional.wings.slardar.context.Now;
import pro.fessional.wings.slardar.context.TerminalAttribute;
import pro.fessional.wings.slardar.context.TerminalContext;
import pro.fessional.wings.slardar.context.TerminalContextAware;
import pro.fessional.wings.slardar.security.WingsAuthTypeParser;
import pro.fessional.wings.warlock.database.autogen.tables.WinUserLoginTable;
import pro.fessional.wings.warlock.database.autogen.tables.daos.WinUserLoginDao;
import pro.fessional.wings.warlock.database.autogen.tables.pojos.WinUserLogin;
import pro.fessional.wings.warlock.service.user.WarlockUserLoginService;

/**
 * @author trydofor
 * @since 2021-03-26
 */
@Slf4j
public class WarlockUserLoginServiceImpl implements WarlockUserLoginService, TerminalContextAware {

    @Setter(onMethod_ = {@Autowired})
    protected WinUserLoginDao winUserLoginDao;

    @Setter(onMethod_ = {@Autowired})
    protected WingsAuthTypeParser wingsAuthTypeParser;

    @Setter(onMethod_ = {@Autowired})
    protected LightIdService lightIdService;

    @Override
    public @NotNull PageResult<Item> list(long userId, PageQuery query) {
        if (winUserLoginDao.notTableExist()) return PageResult.empty();

        final WinUserLoginTable t = winUserLoginDao.getTable();
        return PageJooqHelper.use(winUserLoginDao, query)
                             .count()
                             .from(t)
                             .whereTrue()
                             .order(t.LoginDt.desc())
                             .fetch(t.AuthType, t.LoginIp, t.LoginDt, t.Terminal, t.Failed)
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

        final TerminalContext.Context tc = getTerminalContext();
        po.setLoginIp(tc.getTerminal(TerminalAttribute.TerminalAddr));
        po.setTerminal(tc.getTerminal(TerminalAttribute.TerminalAgent));
        winUserLoginDao.insert(po);
    }
}
