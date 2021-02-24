package pro.fessional.wings.warlock.service.auth.impl;

import com.alibaba.fastjson.JSON;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import me.zhyd.oauth.enums.AuthUserGender;
import me.zhyd.oauth.model.AuthUser;
import org.jooq.Condition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pro.fessional.wings.faceless.service.journal.JournalService;
import pro.fessional.wings.faceless.service.lightid.LightIdService;
import pro.fessional.wings.slardar.security.WingsAuthTypeParser;
import pro.fessional.wings.slardar.security.WingsTerminalContext;
import pro.fessional.wings.slardar.security.impl.DefaultWingsUserDetails;
import pro.fessional.wings.warlock.database.autogen.tables.WinUserAnthnTable;
import pro.fessional.wings.warlock.database.autogen.tables.WinUserBasicTable;
import pro.fessional.wings.warlock.database.autogen.tables.WinUserLoginTable;
import pro.fessional.wings.warlock.database.autogen.tables.daos.WinUserAnthnDao;
import pro.fessional.wings.warlock.database.autogen.tables.daos.WinUserBasicDao;
import pro.fessional.wings.warlock.database.autogen.tables.daos.WinUserLoginDao;
import pro.fessional.wings.warlock.database.autogen.tables.pojos.WinUserAnthn;
import pro.fessional.wings.warlock.database.autogen.tables.pojos.WinUserBasic;
import pro.fessional.wings.warlock.database.autogen.tables.pojos.WinUserLogin;
import pro.fessional.wings.warlock.enums.autogen.UserGender;
import pro.fessional.wings.warlock.enums.autogen.UserStatus;
import pro.fessional.wings.warlock.service.auth.WarlockAuthnService;
import pro.fessional.wings.warlock.service.auth.help.DetailsMapper;
import pro.fessional.wings.warlock.spring.prop.WarlockSecurityProp;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Locale;

/**
 * @author trydofor
 * @since 2021-02-23
 */
@Service
@Slf4j
public class WarlockAuthnServiceImpl implements WarlockAuthnService {

    @Setter(onMethod = @__({@Autowired}))
    private WinUserBasicDao winUserBasicDao;

    @Setter(onMethod = @__({@Autowired}))
    private WinUserAnthnDao winUserAnthnDao;

    @Setter(onMethod = @__({@Autowired}))
    private WinUserLoginDao winUserLoginDao;

    @Setter(onMethod = @__({@Autowired}))
    private WingsAuthTypeParser wingsAuthTypeParser;

    @Setter(onMethod = @__({@Autowired}))
    private WarlockSecurityProp warlockSecurityProp;

    @Setter(onMethod = @__({@Autowired}))
    private LightIdService lightIdService;

    @Setter(onMethod = @__({@Autowired}))
    private JournalService journalService;

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

    public enum Jane {
        AutoSave,
        Success,
        Failure
    }

    @Override
    @Transactional
    public Details save(Enum<?> authType, String username, AuthUser authUser) {
        if (authUser == null) return null;

        final String at = wingsAuthTypeParser.parse(authType);
        final WinUserBasicTable tu = winUserBasicDao.getTable();
        final long uid = lightIdService.getId(tu.getClass());
        final String mrk = "auto create auth-user auth-type=" + at + "username=" + username;
        val commit = journalService.commit(Jane.AutoSave, uid, mrk);

        WinUserBasic user = new WinUserBasic();
        commit.create(user);
        user.setId(uid);
        user.setNickname(authUser.getNickname());
        user.setAvatar(authUser.getAvatar());
        final AuthUserGender aug = authUser.getGender();
        if (aug == AuthUserGender.FEMALE) {
            user.setGender(UserGender.FEMALE);
        } else if (aug == AuthUserGender.MALE) {
            user.setGender(UserGender.MALE);
        } else {
            user.setGender(UserGender.UNKNOWN);
        }
        user.setLocale(Locale.getDefault());
        user.setZoneid((ZoneId.systemDefault()));
        user.setRemark(authUser.getRemark());
        user.setStatus(UserStatus.ACTIVE);
        winUserBasicDao.insert(user);

        //
        WinUserAnthn auth = new WinUserAnthn();
        commit.create(auth);
        auth.setId(lightIdService.getId(winUserAnthnDao.getTable().getClass()));
        auth.setUserId(uid);
        auth.setAuthType(at);
        auth.setUsername(authUser.getUuid());
        auth.setPassword("pre-authed");
        auth.setPasssalt("pre-authed");

        auth.setExtraPara(JSON.toJSONString(authUser.getToken()));
        auth.setExtraUser(JSON.toJSONString(authUser.getRawUserInfo()));

        long seconds = warlockSecurityProp.getExpiredDuration().getSeconds();
        LocalDateTime expired = commit.getCommitDt().plusSeconds(seconds);
        auth.setExpiredDt(expired);
        auth.setFailedCnt(0);
        auth.setFailedMax(warlockSecurityProp.getMaxFailedCount());

        winUserAnthnDao.insert(auth);

        final Details details = new Details();
        details.setUserId(uid);
        details.setNickname(user.getNickname());
        details.setLocale(user.getLocale());
        details.setZoneId(user.getZoneid());
        details.setStatus(user.getStatus());
        details.setAuthType(authType);
        details.setUsername(auth.getUsername());
        details.setPassword(auth.getPassword());
        details.setPasssalt(auth.getPasssalt());
        details.setExpiredDt(auth.getExpiredDt());

        return details;
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

}
