package pro.fessional.wings.warlock.service.auth.impl;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jooq.Condition;
import org.springframework.beans.factory.annotation.Autowired;
import pro.fessional.wings.faceless.service.journal.JournalService;
import pro.fessional.wings.silencer.spring.WingsOrdered;
import pro.fessional.wings.slardar.context.AttributeHolder;
import pro.fessional.wings.slardar.context.TerminalContext;
import pro.fessional.wings.slardar.security.WingsAuthTypeParser;
import pro.fessional.wings.warlock.constants.WarlockGlobalAttribute;
import pro.fessional.wings.warlock.database.autogen.tables.WinUserAuthnTable;
import pro.fessional.wings.warlock.database.autogen.tables.WinUserBasisTable;
import pro.fessional.wings.warlock.database.autogen.tables.daos.WinUserAuthnDao;
import pro.fessional.wings.warlock.database.autogen.tables.daos.WinUserBasisDao;
import pro.fessional.wings.warlock.security.error.FailureWaitingInternalAuthenticationServiceException;
import pro.fessional.wings.warlock.service.auth.WarlockAuthnService;
import pro.fessional.wings.warlock.service.auth.WarlockDangerService;
import pro.fessional.wings.warlock.service.user.WarlockUserAuthnService;
import pro.fessional.wings.warlock.service.user.WarlockUserLoginService;
import pro.fessional.wings.warlock.spring.prop.WarlockDangerProp;

import static pro.fessional.wings.slardar.errcode.AuthnErrorEnum.FailureWaiting;

/**
 * @author trydofor
 * @since 2022-07-11
 */
@Slf4j
@Getter @Setter
public class DefaultDaoAuthnCombo implements ComboWarlockAuthnService.Combo {

    public static final int ORDER = WingsOrdered.Lv4Application;
    private int order = ORDER;

    @Setter(onMethod_ = { @Autowired })
    protected WinUserBasisDao winUserBasisDao;

    @Setter(onMethod_ = { @Autowired })
    protected WinUserAuthnDao winUserAuthnDao;

    @Setter(onMethod_ = { @Autowired })
    protected WingsAuthTypeParser wingsAuthTypeParser;

    @Setter(onMethod_ = { @Autowired })
    protected WarlockUserLoginService warlockUserLoginService;

    @Setter(onMethod_ = { @Autowired })
    protected WarlockUserAuthnService warlockUserAuthnService;

    @Setter(onMethod_ = { @Autowired })
    protected JournalService journalService;

    @Setter(onMethod_ = { @Autowired })
    protected WarlockDangerProp warlockDangerProp;

    @Setter(onMethod_ = { @Autowired })
    protected WarlockDangerService warlockDangerService;

    @Override
    public WarlockAuthnService.Details load(@NotNull Enum<?> authType, String username) {
        final int block = warlockDangerService.check(authType, username);
        if (block > 0) {
            throw new FailureWaitingInternalAuthenticationServiceException(
                block,
                FailureWaiting,
                block);
        }

        if (winUserBasisDao.notTableExist() || winUserAuthnDao.notTableExist()) return null;

        final WinUserBasisTable user = winUserBasisDao.getAlias();
        final WinUserAuthnTable auth = winUserAuthnDao.getAlias();
        final String at = wingsAuthTypeParser.parse(authType);

        final Condition cond = user.Id.eq(auth.UserId)
                                      .and(auth.AuthType.eq(at))
                                      .and(auth.Username.eq(username))
                                      .and(user.getOnlyLive())
                                      .and(auth.getOnlyLive());

        return selectDetails(user, auth, authType, cond);
    }

    @Override
    public WarlockAuthnService.Details load(@NotNull Enum<?> authType, long userId) {
        if (winUserBasisDao.notTableExist() || winUserAuthnDao.notTableExist()) return null;

        final WinUserBasisTable user = winUserBasisDao.getAlias();
        final WinUserAuthnTable auth = winUserAuthnDao.getAlias();
        final String at = wingsAuthTypeParser.parse(authType);

        final Condition cond = user.Id.eq(auth.UserId)
                                      .and(auth.AuthType.eq(at))
                                      .and(auth.UserId.eq(userId))
                                      .and(user.getOnlyLive())
                                      .and(auth.getOnlyLive());

        return selectDetails(user, auth, authType, cond);
    }

    @Override
    public void onSuccess(@NotNull Enum<?> authType, long userId, String details) {
        if (winUserAuthnDao.notTableExist()) return;

        final String at = wingsAuthTypeParser.parse(authType);
        journalService.commit(WarlockAuthnService.Jane.Success, userId, "success login auth-type=" + at, commit -> {
            WarlockUserLoginService.Auth la = new WarlockUserLoginService.Auth();
            la.setAuthType(authType);
            la.setUserId(userId);
            la.setDetails(details);
            la.setFailed(false);
            warlockUserLoginService.auth(la);

            final WinUserAuthnTable ta = winUserAuthnDao.getTable();
            winUserAuthnDao
                .ctx()
                .update(ta)
                .set(ta.FailedCnt, 0)
                .set(ta.CommitId, commit.getCommitId())
                .set(ta.ModifyDt, commit.getCommitDt())
                .where(ta.UserId.eq(userId).and(ta.AuthType.eq(at)))
                .execute();
        });
        //
        final String username = TerminalContext.get().getUsername();
        warlockDangerService.allow(authType, username);
    }

    @Override
    public void onFailure(@NotNull Enum<?> authType, String username, String details) {
        if (username == null || username.isEmpty() || winUserAuthnDao.notTableExist()) return;

        final String at = wingsAuthTypeParser.parse(authType);
        final WinUserAuthnTable ta = winUserAuthnDao.getTable();
        var auth = winUserAuthnDao
            .ctx()
            .select(ta.UserId, ta.FailedCnt, ta.FailedMax, ta.Id)
            .from(ta)
            .where(ta.Username.eq(username).and(ta.AuthType.eq(at)).and(ta.getOnlyLive()))
            .fetchOne();

        if (auth == null) {
            log.debug("ignore login failure by not found auth-type={}, username={}", at, username);
            return;
        }

        final long uid = auth.value1();
        final int cnt = auth.value2();
        final long aid = auth.value4();
        final int max = auth.value3();

        final int second = (int) (warlockDangerProp.getRetryStep().toSeconds() * cnt);
        warlockDangerService.block(authType, username, second);

        if (cnt > max) {
            log.debug("ignore login failure by reach max-count={}, auth-type={}, username={}", max, at, username);
            return;
        }

        journalService.commit(WarlockAuthnService.Jane.Failure, uid, "failed login auth-id=" + aid, commit -> {
            // lock user
            if (warlockDangerProp.isMaxFailure() && cnt >= max) {
                log.info("danger user by reach max-count={}, auth-type={}, username={}", max, at, username);
                warlockUserAuthnService.dander(uid, true);
            }

            winUserAuthnDao
                .ctx()
                .update(ta)
                .set(ta.FailedCnt, ta.FailedCnt.add(1))
                .set(ta.CommitId, commit.getCommitId())
                .set(ta.ModifyDt, commit.getCommitDt())
                .where(ta.Id.eq(aid))
                .execute();

            WarlockUserLoginService.Auth la = new WarlockUserLoginService.Auth();
            la.setAuthType(authType);
            la.setUserId(uid);
            la.setDetails(details);
            la.setFailed(true);
            warlockUserLoginService.auth(la);
        });
    }

    private WarlockAuthnService.Details selectDetails(WinUserBasisTable user, WinUserAuthnTable auth,
                                                      Enum<?> authType, Condition cond) {
        final WarlockAuthnService.Details details = winUserAuthnDao
            .ctx()
            .select(auth.UserId, user.Nickname,
                user.Locale, user.Zoneid.as("zoneId"),
                user.Status, auth.Username,
                auth.Password, auth.ExpiredDt)
            .from(user, auth)
            .where(cond)
            .fetchOneInto(WarlockAuthnService.Details.class);

        if (details != null) {
            details.setAuthType(authType);
            final String passsalt = AttributeHolder.tryAttr(WarlockGlobalAttribute.SaltByUid, details.getUserId());
            details.setPasssalt(passsalt);
        }
        return details;
    }
}
