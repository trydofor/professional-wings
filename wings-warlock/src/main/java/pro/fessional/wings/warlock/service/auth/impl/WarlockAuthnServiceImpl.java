package pro.fessional.wings.warlock.service.auth.impl;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.jooq.Condition;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pro.fessional.wings.faceless.database.helper.ModifyAssert;
import pro.fessional.wings.faceless.service.journal.JournalService;
import pro.fessional.wings.faceless.service.lightid.LightIdService;
import pro.fessional.wings.slardar.security.PasssaltEncoder;
import pro.fessional.wings.slardar.security.WingsAuthTypeParser;
import pro.fessional.wings.slardar.security.WingsTerminalContext;
import pro.fessional.wings.slardar.security.impl.DefaultWingsUserDetails;
import pro.fessional.wings.warlock.database.autogen.tables.WinUserAnthnTable;
import pro.fessional.wings.warlock.database.autogen.tables.WinUserBasicTable;
import pro.fessional.wings.warlock.database.autogen.tables.WinUserLoginTable;
import pro.fessional.wings.warlock.database.autogen.tables.daos.WinUserAnthnDao;
import pro.fessional.wings.warlock.database.autogen.tables.daos.WinUserBasicDao;
import pro.fessional.wings.warlock.database.autogen.tables.daos.WinUserLoginDao;
import pro.fessional.wings.warlock.database.autogen.tables.pojos.WinUserLogin;
import pro.fessional.wings.warlock.enums.autogen.UserStatus;
import pro.fessional.wings.warlock.service.auth.WarlockAuthnService;
import pro.fessional.wings.warlock.service.auth.help.DetailsMapper;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author trydofor
 * @since 2021-02-23
 */
@Service
@Slf4j
public class WarlockAuthnServiceImpl implements WarlockAuthnService, InitializingBean {

    @Setter(onMethod = @__({@Autowired}))
    private WinUserBasicDao winUserBasicDao;

    @Setter(onMethod = @__({@Autowired}))
    private WinUserAnthnDao winUserAnthnDao;

    @Setter(onMethod = @__({@Autowired}))
    private WinUserLoginDao winUserLoginDao;

    @Setter(onMethod = @__({@Autowired}))
    private WingsAuthTypeParser wingsAuthTypeParser;

    @Setter(onMethod = @__({@Autowired}))
    private LightIdService lightIdService;

    @Setter(onMethod = @__({@Autowired}))
    private JournalService journalService;

    @Setter(onMethod = @__({@Autowired}))
    private ObjectProvider<Saver> saverProvider;

    @Setter(onMethod = @__({@Autowired}))
    private PasssaltEncoder passsaltEncoder;

    private List<Saver> orderedSavers = Collections.emptyList();

    @Override
    public void afterPropertiesSet() {
        orderedSavers = saverProvider.orderedStream().collect(Collectors.toList());
        log.info("inject {} savers", orderedSavers.size());
    }

    @Override
    public Details load(Enum<?> authType, String username) {
        final WinUserBasicTable user = winUserBasicDao.getAlias();
        final WinUserAnthnTable auth = winUserAnthnDao.getAlias();
        final String at = wingsAuthTypeParser.parse(authType);

        final Condition cond = user.Id.eq(auth.UserId)
                                      .and(auth.AuthType.eq(at))
                                      .and(auth.Username.eq(username))
                                      .and(user.onlyLiveData)
                                      .and(auth.onlyLiveData);

        final Details details = winUserAnthnDao
                .ctx()
                .select(auth.UserId, user.Nickname, user.Locale, user.Zoneid, user.Status,
                        auth.Username, auth.Password, auth.Passsalt, auth.ExpiredDt)
                .from(user, auth)
                .where(cond)
                .fetchOneInto(Details.class);
        if (details != null) {
            details.setAuthType(authType);
        }
        return details;
    }

    @Override
    public void auth(DefaultWingsUserDetails userDetails, Details details) {
        if (userDetails == null || details == null) return;

        DetailsMapper.into(details, userDetails);

        switch (details.getStatus()) {
            case ACTIVE:
            case UNINIT:
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
    public Details save(Enum<?> authType, String username, Object details) {
        for (Saver saver : orderedSavers) {
            if (saver.accept(authType, username, details)) {
                final Details dt = saver.save(authType, username, details);
                if (dt != null) return dt;
            }
        }
        return null;
    }

    @Override
    public void onSuccess(Enum<?> authType, long userId, String details) {
        final WingsTerminalContext.Context tc = WingsTerminalContext.get();
        final WinUserLoginTable t = winUserLoginDao.getTable();

        WinUserLogin po = new WinUserLogin();
        po.setId(lightIdService.getId(t.getClass()));
        po.setUserId(userId);
        final String at = wingsAuthTypeParser.parse(authType);
        po.setAuthType(at);
        po.setLoginIp(tc.getRemoteIp());
        po.setLoginDt(LocalDateTime.now());
        po.setTerminal(tc.getAgentInfo());
        po.setDetails(details);
        po.setFailed(false);
        winUserLoginDao.insert(po);

        val commit = journalService.commit(Jane.Success, userId, "success login auth-type=" + at);
        final WinUserAnthnTable ta = winUserAnthnDao.getTable();
        winUserAnthnDao
                .ctx()
                .update(ta)
                .set(ta.FailedCnt, 0)
                .set(ta.CommitId, commit.getCommitId())
                .set(ta.ModifyDt, commit.getCommitDt())
                .where(ta.UserId.eq(userId))
                .execute();
    }

    @Override
    public void onFailure(Enum<?> authType, String username) {
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

        final int cnt = auth.value2();
        final int max = auth.value3();
        if (cnt > max) {
            log.info("ignore login failure by reach max-count={}, auth-type={}, username={}", auth.value3(), at, username);
            timingAttack();
            return;
        }

        final long bgn = System.currentTimeMillis();
        final long uid = auth.value1();
        final long aid = auth.value4();

        val commit = journalService.commit(Jane.Failure, uid, "failed login auth-id=" + aid);
        // 锁账号
        if (cnt >= max) {
            final WinUserBasicTable tu = winUserBasicDao.getTable();
            winUserBasicDao
                    .ctx()
                    .update(tu)
                    .set(tu.Status, UserStatus.DANGER)
                    .set(tu.CommitId, commit.getCommitId())
                    .set(tu.ModifyDt, commit.getCommitDt())
                    .set(tu.Remark, "locked by reach the max failure count=" + max)
                    .where(tu.Id.eq(uid))
                    .execute();
        }

        final WingsTerminalContext.Context tc = WingsTerminalContext.get();
        final WinUserLoginTable tl = winUserLoginDao.getTable();
        WinUserLogin po = new WinUserLogin();
        po.setId(lightIdService.getId(tl.getClass()));
        po.setUserId(uid);
        po.setAuthType(at);
        po.setLoginIp(tc.getRemoteIp());
        po.setLoginDt(LocalDateTime.now());
        po.setTerminal(tc.getAgentInfo());
        po.setDetails("");
        po.setFailed(true);
        winUserLoginDao.insert(po);


        winUserAnthnDao
                .ctx()
                .update(ta)
                .set(ta.FailedCnt, ta.FailedCnt.add(1))
                .set(ta.CommitId, commit.getCommitId())
                .set(ta.ModifyDt, commit.getCommitDt())
                .where(ta.Id.eq(aid))
                .execute();

        lastTiming = System.currentTimeMillis() - bgn;
    }

    private volatile long lastTiming = 0;

    private void timingAttack() {
        final long t = lastTiming;
        if (t > 10 && t < 10000) {
            try {
                Thread.sleep(t);
            } catch (InterruptedException e) {
                // ignore
            }
        }
    }

    @Override
    public void renew(Enum<?> authType, String username, Authn authn) {
        renew(authType, authn, username, null);
    }

    @Override
    public void renew(Enum<?> authType, long userId, Authn authn) {
        renew(authType, authn, null, userId);
    }

    private void renew(Enum<?> authType, Authn authn, String username, Long userId) {

        if (authn.getExpiredIn() == null && authn.getMaxFailed() == null
                && authn.getPassword() == null && !authn.isZeroFail()) {
            log.info("nothing to renew auth-type={}, username={}, userId={}", authType, username, userId);
            return;
        }

        final String at = wingsAuthTypeParser.parse(authType);
        final WinUserAnthnTable t = winUserAnthnDao.getTable();
        final Condition cond;
        final JournalService.Journal commit;
        if (userId != null) {
            cond = t.AuthType.eq(at).and(t.UserId.eq(userId)).and(t.onlyLiveData);
            commit = journalService.commit(Jane.Renew, userId, "by userId and auth-type=" + authType);
        } else {
            cond = t.AuthType.eq(at).and(t.Username.eq(username)).and(t.onlyLiveData);
            commit = journalService.commit(Jane.Renew, username, "by username and auth-type=" + authType);
        }

        val update = winUserAnthnDao
                .ctx()
                .update(t)
                .set(t.CommitId, commit.getCommitId())
                .set(t.ModifyDt, commit.getCommitDt());

        if (authn.isZeroFail()) {
            update.set(t.FailedCnt, 0);
        }

        if (authn.getPassword() != null) {
            val rc = winUserAnthnDao
                    .ctx()
                    .select(t.Passsalt)
                    .from(t)
                    .where(cond)
                    .fetchOne();

            if (rc == null) {
                throw new IllegalStateException("failed to found authn by auth-type=" + at + ", user-id=" + userId);
            }
            update.set(t.Password, passsaltEncoder.salt(authn.getPassword(), rc.value1()));
        }

        final Integer maxFailed = authn.getMaxFailed();
        if (maxFailed != null && maxFailed > 0) {
            update.set(t.FailedMax, maxFailed);
        }

        if (authn.getExpiredIn() != null) {
            final LocalDateTime expired = commit.getCommitDt().plusSeconds(authn.getExpiredIn().getSeconds());
            update.set(t.ExpiredDt, expired);
        }

        final int af = update
                .where(cond)
                .execute();

        ModifyAssert.one(af, "failed to renew auth-type={}, userId={}, username={}", at, userId, username);
    }
}
