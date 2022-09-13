package pro.fessional.wings.warlock.service.auth.impl;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jooq.Condition;
import org.springframework.beans.factory.annotation.Autowired;
import pro.fessional.wings.faceless.service.journal.JournalService;
import pro.fessional.wings.slardar.context.GlobalAttributeHolder;
import pro.fessional.wings.slardar.event.EventPublishHelper;
import pro.fessional.wings.slardar.security.WingsAuthTypeParser;
import pro.fessional.wings.warlock.constants.WarlockOrderConst;
import pro.fessional.wings.warlock.database.autogen.tables.WinUserAuthnTable;
import pro.fessional.wings.warlock.database.autogen.tables.WinUserBasisTable;
import pro.fessional.wings.warlock.database.autogen.tables.daos.WinUserAuthnDao;
import pro.fessional.wings.warlock.database.autogen.tables.daos.WinUserBasisDao;
import pro.fessional.wings.warlock.enums.autogen.UserStatus;
import pro.fessional.wings.warlock.event.auth.WarlockMaxFailedEvent;
import pro.fessional.wings.warlock.service.auth.WarlockAuthnService;
import pro.fessional.wings.warlock.service.user.WarlockUserAttribute;
import pro.fessional.wings.warlock.service.user.WarlockUserLoginService;

/**
 * @author trydofor
 * @since 2022-07-11
 */
@Slf4j
public class DefaultDaoAuthnCombo implements ComboWarlockAuthnService.Combo {

    @Getter @Setter
    private int order = WarlockOrderConst.DefaultDaoAuthnCombo;

    @Setter(onMethod_ = {@Autowired})
    protected WinUserBasisDao winUserBasisDao;

    @Setter(onMethod_ = {@Autowired})
    protected WinUserAuthnDao winUserAuthnDao;

    @Setter(onMethod_ = {@Autowired})
    protected WingsAuthTypeParser wingsAuthTypeParser;

    @Setter(onMethod_ = {@Autowired})
    protected WarlockUserLoginService warlockUserLoginService;

    @Setter(onMethod_ = {@Autowired})
    protected JournalService journalService;

    @Override
    public WarlockAuthnService.Details load(@NotNull Enum<?> authType, String username) {
        if (winUserBasisDao.notTableExist() || winUserAuthnDao.notTableExist()) return null;

        final WinUserBasisTable user = winUserBasisDao.getAlias();
        final WinUserAuthnTable auth = winUserAuthnDao.getAlias();
        final String at = wingsAuthTypeParser.parse(authType);

        final Condition cond = user.Id.eq(auth.UserId)
                                      .and(auth.AuthType.eq(at))
                                      .and(auth.Username.eq(username))
                                      .and(user.onlyLiveData)
                                      .and(auth.onlyLiveData);

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
                                      .and(user.onlyLiveData)
                                      .and(auth.onlyLiveData);

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
                    .where(ta.UserId.eq(userId))
                    .execute();
        });
    }

    @Override
    public void onFailure(@NotNull Enum<?> authType, String username) {
        if (username == null || username.isEmpty() || winUserAuthnDao.notTableExist()) return;

        final String at = wingsAuthTypeParser.parse(authType);
        final WinUserAuthnTable ta = winUserAuthnDao.getTable();
        val auth = winUserAuthnDao
                .ctx()
                .select(ta.UserId, ta.FailedCnt, ta.FailedMax, ta.Id)
                .from(ta)
                .where(ta.Username.eq(username).and(ta.AuthType.eq(at)).and(ta.onlyLiveData))
                .fetchOne();

        if (auth == null) {
            log.info("ignore login failure by not found auth-type={}, username={}", at, username);
            return;
        }

        final long uid = auth.value1();
        final int cnt = auth.value2();
        final long aid = auth.value4();
        final int max = auth.value3();

        //
        if (cnt > max - 3) {
            WarlockMaxFailedEvent evt = new WarlockMaxFailedEvent();
            evt.setCurrent(cnt);
            evt.setMaximum(max);
            evt.setUserId(uid);
            EventPublishHelper.SyncSpring.publishEvent(evt);
        }

        if (cnt > max) {
            log.info("ignore login failure by reach max-count={}, auth-type={}, username={}", auth.value3(), at, username);
            return;
        }

        journalService.commit(WarlockAuthnService.Jane.Failure, uid, "failed login auth-id=" + aid, commit -> {
            // 锁账号
            if (cnt >= max && !winUserBasisDao.notTableExist()) {
                final WinUserBasisTable tu = winUserBasisDao.getTable();
                winUserBasisDao
                        .ctx()
                        .update(tu)
                        .set(tu.Status, UserStatus.DANGER)
                        .set(tu.CommitId, commit.getCommitId())
                        .set(tu.ModifyDt, commit.getCommitDt())
                        .set(tu.Remark, "locked by reach the max failure count=" + max)
                        .where(tu.Id.eq(uid))
                        .execute();
            }

            WarlockUserLoginService.Auth la = new WarlockUserLoginService.Auth();
            la.setAuthType(authType);
            la.setUserId(uid);
            la.setDetails("");
            la.setFailed(true);
            warlockUserLoginService.auth(la);

            winUserAuthnDao
                    .ctx()
                    .update(ta)
                    .set(ta.FailedCnt, ta.FailedCnt.add(1))
                    .set(ta.CommitId, commit.getCommitId())
                    .set(ta.ModifyDt, commit.getCommitDt())
                    .where(ta.Id.eq(aid))
                    .execute();
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
            final String passsalt = GlobalAttributeHolder.getAttr(WarlockUserAttribute.SaltByUid, details.getUserId());
            details.setPasssalt(passsalt);
        }
        return details;
    }
}
