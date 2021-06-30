package pro.fessional.wings.warlock.service.auth.impl;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jooq.Condition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.transaction.annotation.Transactional;
import pro.fessional.wings.faceless.service.journal.JournalService;
import pro.fessional.wings.slardar.context.GlobalAttributeHolder;
import pro.fessional.wings.slardar.event.EventPublishHelper;
import pro.fessional.wings.slardar.security.WingsAuthTypeParser;
import pro.fessional.wings.slardar.security.impl.DefaultWingsUserDetails;
import pro.fessional.wings.warlock.database.autogen.tables.WinUserAnthnTable;
import pro.fessional.wings.warlock.database.autogen.tables.WinUserBasisTable;
import pro.fessional.wings.warlock.database.autogen.tables.daos.WinUserAnthnDao;
import pro.fessional.wings.warlock.database.autogen.tables.daos.WinUserBasisDao;
import pro.fessional.wings.warlock.enums.autogen.UserStatus;
import pro.fessional.wings.warlock.event.auth.WarlockMaxFailedEvent;
import pro.fessional.wings.warlock.service.auth.WarlockAuthnService;
import pro.fessional.wings.warlock.service.auth.help.AuthnDetailsMapper;
import pro.fessional.wings.warlock.service.user.WarlockUserAttribute;
import pro.fessional.wings.warlock.service.user.WarlockUserLoginService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * @author trydofor
 * @since 2021-02-23
 */
@Slf4j
public class ComboWarlockAuthnService implements WarlockAuthnService {

    @Setter(onMethod_ = {@Autowired})
    private WinUserBasisDao winUserBasisDao;

    @Setter(onMethod_ = {@Autowired})
    private WinUserAnthnDao winUserAnthnDao;

    @Setter(onMethod_ = {@Autowired})
    private WingsAuthTypeParser wingsAuthTypeParser;

    @Setter(onMethod_ = {@Autowired})
    private JournalService journalService;

    @Setter(onMethod_ = {@Autowired})
    private WarlockUserLoginService warlockUserLoginService;

    private List<AutoReg> authAutoRegs = Collections.emptyList();

    @Autowired(required = false)
    public void setAuthAutoRegs(List<AutoReg> authAutoRegs) {
        log.info("inject auth combo, count={}", authAutoRegs.size());
        this.authAutoRegs = authAutoRegs;
    }

    @Override
    public Details load(@NotNull Enum<?> authType, String username) {
        final WinUserBasisTable user = winUserBasisDao.getAlias();
        final WinUserAnthnTable auth = winUserAnthnDao.getAlias();
        final String at = wingsAuthTypeParser.parse(authType);

        final Condition cond = user.Id.eq(auth.UserId)
                                      .and(auth.AuthType.eq(at))
                                      .and(auth.Username.eq(username))
                                      .and(user.onlyLiveData)
                                      .and(auth.onlyLiveData);

        final Details details = winUserAnthnDao
                .ctx()
                .select(auth.UserId, user.Nickname,
                        user.Locale, user.Zoneid.as("zoneId"),
                        user.Status, auth.Username,
                        auth.Password, auth.ExpiredDt)
                .from(user, auth)
                .where(cond)
                .fetchOneInto(Details.class);
        if (details != null) {
            details.setAuthType(authType);
            final String passsalt = GlobalAttributeHolder.getAttr(WarlockUserAttribute.SaltByUid, details.getUserId());
            details.setPasssalt(passsalt);
        }
        return details;
    }

    @Override
    public void auth(DefaultWingsUserDetails userDetails, Details details) {
        if (userDetails == null || details == null) return;

        AuthnDetailsMapper.into(details, userDetails);

        switch (details.getStatus()) {
            case UNINIT:
            case ACTIVE:
            case INFIRM:
            case UNSAFE:
                userDetails.setEnabled(true);
                userDetails.setAccountNonExpired(true);
                userDetails.setAccountNonLocked(true);
                break;
            case DANGER:
                userDetails.setEnabled(true);
                userDetails.setAccountNonExpired(true);
                userDetails.setAccountNonLocked(false);
                break;
            default:
                userDetails.setEnabled(false);
                userDetails.setAccountNonExpired(false);
                userDetails.setAccountNonLocked(false);
        }

        userDetails.setCredentialsNonExpired(details.getExpiredDt().isAfter(LocalDateTime.now()));
    }

    @Override
    @Transactional
    public Details register(@NotNull Enum<?> authType, String username, Object details) {
        for (AutoReg autoReg : authAutoRegs) {
            if (autoReg.accept(authType, username, details)) {
                final Details dt = autoReg.create(authType, username, details);
                if (dt != null) {
                    log.info("register by AutoReg={}", autoReg.getClass());
                    return dt;
                }
            }
        }
        return null;
    }

    @Override
    public void onSuccess(@NotNull Enum<?> authType, long userId, String details) {
        final String at = wingsAuthTypeParser.parse(authType);
        journalService.commit(Jane.Success, userId, "success login auth-type=" + at, commit -> {
            WarlockUserLoginService.Auth la = new WarlockUserLoginService.Auth();
            la.setAuthType(authType);
            la.setUserId(userId);
            la.setDetails(details);
            la.setFailed(false);
            warlockUserLoginService.auth(la);

            final WinUserAnthnTable ta = winUserAnthnDao.getTable();
            winUserAnthnDao
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
        if (username == null || username.isEmpty()) return;

        final String at = wingsAuthTypeParser.parse(authType);
        final WinUserAnthnTable ta = winUserAnthnDao.getTable();
        val auth = winUserAnthnDao
                .ctx()
                .select(ta.UserId, ta.FailedCnt, ta.FailedMax, ta.Id)
                .from(ta)
                .where(ta.Username.eq(username).and(ta.AuthType.eq(at)).and(ta.onlyLiveData))
                .fetchOne();

        if (auth == null) {
            log.info("ignore login failure by not found auth-type={}, username={}", at, username);
            timingAttack();
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
            timingAttack();
            return;
        }

        final long bgn = System.currentTimeMillis();


        journalService.commit(Jane.Failure, uid, "failed login auth-id=" + aid, commit -> {
            // 锁账号
            if (cnt >= max) {
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

            winUserAnthnDao
                    .ctx()
                    .update(ta)
                    .set(ta.FailedCnt, ta.FailedCnt.add(1))
                    .set(ta.CommitId, commit.getCommitId())
                    .set(ta.ModifyDt, commit.getCommitDt())
                    .where(ta.Id.eq(aid))
                    .execute();

            lastTiming = System.currentTimeMillis() - bgn;
        });
    }

    private volatile long lastTiming = 0;

    private void timingAttack() {
        final long t = lastTiming;
        if (t > 10 && t < 10000) {
            try {
                Thread.sleep(t);
            }
            catch (InterruptedException e) {
                // ignore
            }
        }
    }

    // /////
    public interface AutoReg extends Ordered {
        /**
         * 不需要事务,在外层事务内调用
         */
        Details create(@NotNull Enum<?> authType, String username, Object details);

        boolean accept(@NotNull Enum<?> authType, String username, Object details);
    }
}
