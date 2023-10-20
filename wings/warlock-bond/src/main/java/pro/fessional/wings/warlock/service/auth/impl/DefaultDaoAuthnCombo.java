package pro.fessional.wings.warlock.service.auth.impl;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jooq.Condition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import pro.fessional.wings.faceless.service.journal.JournalService;
import pro.fessional.wings.slardar.context.GlobalAttributeHolder;
import pro.fessional.wings.slardar.context.TerminalContext;
import pro.fessional.wings.slardar.errcode.AuthnErrorEnum;
import pro.fessional.wings.slardar.security.WingsAuthTypeParser;
import pro.fessional.wings.spring.consts.OrderedWarlockConst;
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

import java.util.Locale;

/**
 * @author trydofor
 * @since 2022-07-11
 */
@Slf4j
public class DefaultDaoAuthnCombo implements ComboWarlockAuthnService.Combo {

    @Getter @Setter
    private int order = OrderedWarlockConst.DefaultDaoAuthnCombo;

    @Setter(onMethod_ = {@Autowired})
    protected WinUserBasisDao winUserBasisDao;

    @Setter(onMethod_ = {@Autowired})
    protected WinUserAuthnDao winUserAuthnDao;

    @Setter(onMethod_ = {@Autowired})
    protected WingsAuthTypeParser wingsAuthTypeParser;

    @Setter(onMethod_ = {@Autowired})
    protected WarlockUserLoginService warlockUserLoginService;

    @Setter(onMethod_ = {@Autowired})
    protected WarlockUserAuthnService warlockUserAuthnService;

    @Setter(onMethod_ = {@Autowired})
    protected JournalService journalService;

    @Setter(onMethod_ = {@Autowired})
    protected MessageSource messageSource;

    @Setter(onMethod_ = {@Autowired})
    protected WarlockDangerProp warlockDangerProp;

    @Setter(onMethod_ = {@Autowired})
    protected WarlockDangerService warlockDangerService;

    @Override
    public WarlockAuthnService.Details load(@NotNull Enum<?> authType, String username) {
        final int block = warlockDangerService.check(authType, username);
        if (block > 0) {
            final Locale locale = LocaleContextHolder.getLocale();
            final String code = AuthnErrorEnum.FailureWaiting.getCode();
            final String message = messageSource.getMessage(code, new Object[]{block}, locale);
            throw new FailureWaitingInternalAuthenticationServiceException(block, code, message);
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
        val auth = winUserAuthnDao
                .ctx()
                .select(ta.UserId, ta.FailedCnt, ta.FailedMax, ta.Id)
                .from(ta)
                .where(ta.Username.eq(username).and(ta.AuthType.eq(at)).and(ta.getOnlyLive()))
                .fetchOne();

        if (auth == null) {
            log.info("ignore login failure by not found auth-type={}, username={}", at, username);
            return;
        }

        final long uid = auth.value1();
        final int cnt = auth.value2();
        final long aid = auth.value4();
        final int max = auth.value3();

        final int second = (int) (warlockDangerProp.getRetryStep().toSeconds() * cnt);
        warlockDangerService.block(authType, username, second);

        if (cnt > max) {
            log.info("ignore login failure by reach max-count={}, auth-type={}, username={}", max, at, username);
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
            final String passsalt = GlobalAttributeHolder.tryAttr(WarlockGlobalAttribute.SaltByUid, details.getUserId());
            details.setPasssalt(passsalt);
        }
        return details;
    }
}
