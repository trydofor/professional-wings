package pro.fessional.wings.warlock.service.user.impl;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jooq.Condition;
import org.jooq.Field;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import pro.fessional.mirana.data.Null;
import pro.fessional.mirana.data.Z;
import pro.fessional.mirana.pain.CodeException;
import pro.fessional.wings.faceless.service.journal.JournalService;
import pro.fessional.wings.faceless.service.lightid.LightIdService;
import pro.fessional.wings.slardar.context.GlobalAttributeHolder;
import pro.fessional.wings.slardar.security.PasssaltEncoder;
import pro.fessional.wings.slardar.security.PasswordHelper;
import pro.fessional.wings.slardar.security.WingsAuthTypeParser;
import pro.fessional.wings.warlock.database.autogen.tables.WinUserAuthnTable;
import pro.fessional.wings.warlock.database.autogen.tables.daos.WinUserAuthnDao;
import pro.fessional.wings.warlock.database.autogen.tables.pojos.WinUserAuthn;
import pro.fessional.wings.warlock.enums.errcode.CommonErrorEnum;
import pro.fessional.wings.warlock.service.user.WarlockUserAttribute;
import pro.fessional.wings.warlock.service.user.WarlockUserAuthnService;
import pro.fessional.wings.warlock.spring.prop.WarlockSecurityProp;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author trydofor
 * @since 2021-03-25
 */
@Slf4j
public class WarlockUserAuthnServiceImpl implements WarlockUserAuthnService {

    @Setter(onMethod_ = {@Autowired})
    protected WinUserAuthnDao winUserAuthnDao;

    @Setter(onMethod_ = {@Autowired})
    protected WingsAuthTypeParser wingsAuthTypeParser;

    @Setter(onMethod_ = {@Autowired})
    protected JournalService journalService;

    @Setter(onMethod_ = {@Autowired})
    protected PasswordEncoder passwordEncoder;

    @Setter(onMethod_ = {@Autowired})
    protected PasssaltEncoder passsaltEncoder;

    @Setter(onMethod_ = {@Autowired})
    protected LightIdService lightIdService;

    @Setter(onMethod_ = {@Autowired})
    protected WarlockSecurityProp warlockSecurityProp;

    @Override
    @Transactional
    public long create(long userId, @NotNull Authn authn) {
        Enum<?> authType = authn.getAuthType();
        return journalService.submit(Jane.Create, userId, authType, commit -> {

            final long id = lightIdService.getId(winUserAuthnDao.getTable());

            WinUserAuthn auth = new WinUserAuthn();

            auth.setId(id);
            auth.setUserId(userId);
            auth.setAuthType(wingsAuthTypeParser.parse(authType));
            auth.setUsername(authn.getUsername());

            final String salt = GlobalAttributeHolder.tryAttr(WarlockUserAttribute.SaltByUid, userId);
            PasswordHelper helper = new PasswordHelper(passwordEncoder, passsaltEncoder);
            auth.setPassword(helper.hash(authn.getPassword(), salt));

            auth.setExtraPara(Null.notNull(authn.getExtraPara()));
            auth.setExtraUser(Null.notNull(authn.getExtraUser()));
            if (authn.getExpiredDt() == null) {
                final Duration expire = warlockSecurityProp.getAutoregExpired();
                auth.setExpiredDt(commit.getCommitDt().plusSeconds(expire.getSeconds()));
            }
            else {
                auth.setExpiredDt(authn.getExpiredDt());
            }
            auth.setFailedCnt(Z.notNullSafe(0,authn.getFailedCnt()));
            auth.setFailedMax(Z.notNullSafe(warlockSecurityProp.getAutoregMaxFailed(), authn.getFailedMax()));

            commit.create(auth);

            try {
                winUserAuthnDao.insert(auth);
            }
            catch (Exception e) {
                log.error("failed to insert authn " + authn, e);
                // 可能唯一约束或字段超长
                throw new CodeException(e, CommonErrorEnum.DataExisted);
            }
            return id;
        });
    }

    @Override
    @Transactional
    public void modify(long userId, @NotNull Authn authn) {
        Enum<?> authType = authn.getAuthType();
        journalService.commit(Jane.Modify, userId, authType, commit -> {

            final WinUserAuthnTable t = winUserAuthnDao.getTable();
            final Condition cond = t.onlyLive(t.AuthType.eq(wingsAuthTypeParser.parse(authType)).and(t.UserId.eq(userId)));
            Map<Field<?>, Object> setter = new HashMap<>();

            if (authn.getPassword() != null) {
                PasswordHelper helper = new PasswordHelper(passwordEncoder, passsaltEncoder);
                final String slat = GlobalAttributeHolder.tryAttr(WarlockUserAttribute.SaltByUid, userId);
                setter.put(t.Password, helper.hash(authn.getPassword(), slat));
            }

            setter.put(t.Username, authn.getUsername());
            setter.put(t.ExtraPara, authn.getExtraPara());
            setter.put(t.ExtraUser, authn.getExtraUser());
            setter.put(t.ExpiredDt, authn.getExpiredDt());
            setter.put(t.FailedCnt, authn.getFailedCnt());
            setter.put(t.FailedMax, authn.getFailedMax());

            setter.put(t.CommitId, commit.getCommitId());
            setter.put(t.ModifyDt, commit.getCommitDt());

            final int rc = winUserAuthnDao.update(t, setter, cond, true);

            if (rc != 1) {
                log.warn("failed to modify authn. uid={}, type={}, affect={}", userId, authType, rc);
                throw new CodeException(CommonErrorEnum.DataNotFound);
            }
        });
    }

    @Override
    @Transactional
    public void renew(long userId, @NotNull Renew renew) {
        String otherInfo = "by userId and auth-type=" + renew.getAuthType();
        journalService.commit(Jane.Renew, userId, otherInfo, commit -> {

            final String at = wingsAuthTypeParser.parse(renew.getAuthType());
            final WinUserAuthnTable t = winUserAuthnDao.getTable();

            final Condition cond = t.onlyLive(t.AuthType.eq(at).and(t.UserId.eq(userId)));

            Map<Field<?>, Object> setter = new HashMap<>();

            if (renew.getPassword() != null) {
                final String slat = GlobalAttributeHolder.tryAttr(WarlockUserAttribute.SaltByUid, userId);
                PasswordHelper helper = new PasswordHelper(passwordEncoder, passsaltEncoder);
                setter.put(t.Password, helper.hash(renew.getPassword(), slat));
            }

            if (renew.getExpiredDt() != null) {
                setter.put(t.ExpiredDt, renew.getExpiredDt());
            }
            else {
                final Duration expire = warlockSecurityProp.getAutoregExpired();
                setter.put(t.ExpiredDt, commit.getCommitDt().plusSeconds(expire.getSeconds()));
            }

            if (renew.getFailedMax() != null) {
                setter.put(t.FailedMax, renew.getFailedMax());
            }
            else {
                setter.put(t.FailedMax, warlockSecurityProp.getAutoregMaxFailed());
            }

            if (renew.getFailedCnt() != null) {
                setter.put(t.FailedCnt, renew.getFailedCnt());
            }
            else {
                setter.put(t.FailedCnt, 0);
            }

            setter.put(t.CommitId, commit.getCommitId());
            setter.put(t.ModifyDt, commit.getCommitDt());

            final int rc = winUserAuthnDao.update(t, setter, cond, true);

            if (rc != 1) {
                log.warn("failed to renew {}, key={}, affect={}", otherInfo, userId, rc);
                throw new CodeException(CommonErrorEnum.DataNotFound);
            }
        });
    }

    @Override
    public @NotNull List<Item> list(long userId) {
        if (winUserAuthnDao.notTableExist()) return Collections.emptyList();

        final WinUserAuthnTable t = winUserAuthnDao.getTable();
        return winUserAuthnDao.fetchLive(Item.class, t,
                t.Username,
                t.AuthType,
                t.ExpiredDt,
                t.FailedCnt);
    }
}
