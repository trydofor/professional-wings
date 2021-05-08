package pro.fessional.wings.warlock.service.user.impl;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jooq.Condition;
import org.jooq.Field;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pro.fessional.mirana.data.Null;
import pro.fessional.mirana.data.Z;
import pro.fessional.mirana.pain.CodeException;
import pro.fessional.wings.faceless.service.journal.JournalService;
import pro.fessional.wings.faceless.service.lightid.LightIdService;
import pro.fessional.wings.slardar.context.GlobalAttributeHolder;
import pro.fessional.wings.slardar.security.PasssaltEncoder;
import pro.fessional.wings.slardar.security.WingsAuthTypeParser;
import pro.fessional.wings.warlock.database.autogen.tables.WinUserAnthnTable;
import pro.fessional.wings.warlock.database.autogen.tables.daos.WinUserAnthnDao;
import pro.fessional.wings.warlock.database.autogen.tables.pojos.WinUserAnthn;
import pro.fessional.wings.warlock.enums.errcode.CommonErrorEnum;
import pro.fessional.wings.warlock.service.user.WarlockUserAttribute;
import pro.fessional.wings.warlock.service.user.WarlockUserAuthnService;
import pro.fessional.wings.warlock.spring.prop.WarlockSecurityProp;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author trydofor
 * @since 2021-03-25
 */
@Service
@Slf4j
public class WarlockUserAuthnServiceImpl implements WarlockUserAuthnService {

    @Setter(onMethod_ = {@Autowired})
    private WinUserAnthnDao winUserAnthnDao;

    @Setter(onMethod_ = {@Autowired})
    private WingsAuthTypeParser wingsAuthTypeParser;

    @Setter(onMethod_ = {@Autowired})
    private JournalService journalService;

    @Setter(onMethod_ = {@Autowired})
    private PasswordEncoder passwordEncoder;

    @Setter(onMethod_ = {@Autowired})
    private PasssaltEncoder passsaltEncoder;

    @Setter(onMethod_ = {@Autowired})
    private LightIdService lightIdService;

    @Setter(onMethod_ = {@Autowired})
    private WarlockSecurityProp warlockSecurityProp;

    @Override
    @Transactional
    public long create(long userId, @NotNull Enum<?> authType, @NotNull Authn authn) {
        return journalService.submit(Jane.Create, userId, authType, commit -> {

            final long id = lightIdService.getId(winUserAnthnDao.getTable());

            WinUserAnthn auth = new WinUserAnthn();

            auth.setId(id);
            auth.setUserId(userId);
            auth.setAuthType(wingsAuthTypeParser.parse(authType));
            auth.setUsername(authn.getUsername());

            final String salt = GlobalAttributeHolder.getAttr(WarlockUserAttribute.SaltByUid, userId);
            final String pass = passsaltEncoder.salt(authn.getPassword(), salt);
            auth.setPassword(passwordEncoder.encode(pass));

            auth.setExtraPara(Null.notNull(authn.getExtraPara()));
            auth.setExtraUser(Null.notNull(authn.getExtraUser()));
            if (authn.getExpiredDt() == null) {
                final Duration expire = warlockSecurityProp.getAutoregExpired();
                auth.setExpiredDt(commit.getCommitDt().plusSeconds(expire.getSeconds()));
            }
            else {
                auth.setExpiredDt(authn.getExpiredDt());
            }
            auth.setFailedCnt(Z.notNull(authn.getFailedCnt(), 0));
            auth.setFailedMax(Z.notNull(authn.getFailedMax(), warlockSecurityProp.getAutoregMaxFailed()));

            commit.create(auth);

            try {
                winUserAnthnDao.insert(auth);
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
    public void modify(long userId, @NotNull Enum<?> authType, @NotNull Authn authn) {
        journalService.commit(Jane.Modify, userId, authType, commit -> {

            final WinUserAnthnTable t = winUserAnthnDao.getTable();
            final Condition cond = t.onlyLive(t.AuthType.eq(wingsAuthTypeParser.parse(authType)).and(t.UserId.eq(userId)));
            Map<Field<?>, Object> setter = new HashMap<>();

            if (authn.getPassword() != null) {
                final String slat = GlobalAttributeHolder.getAttr(WarlockUserAttribute.SaltByUid, userId);
                setter.put(t.Password, passsaltEncoder.salt(authn.getPassword(), slat));
            }

            setter.put(t.Username, authn.getUsername());
            setter.put(t.ExtraPara, authn.getExtraPara());
            setter.put(t.ExtraUser, authn.getExtraUser());
            setter.put(t.ExpiredDt, authn.getExpiredDt());
            setter.put(t.FailedCnt, authn.getFailedCnt());
            setter.put(t.FailedMax, authn.getFailedMax());

            setter.put(t.CommitId, commit.getCommitId());
            setter.put(t.ModifyDt, commit.getCommitDt());

            final int rc = winUserAnthnDao.update(setter, cond, true);

            if (rc != 1) {
                log.warn("failed to modify authn. uid={}, type={}, affect={}", userId, authType, rc);
                throw new CodeException(CommonErrorEnum.DataNotFound);
            }
        });
    }


    @Override
    @Transactional
    public void renew(long userId, @NotNull Enum<?> authType, @NotNull Renew renew) {

        String otherInfo = "by userId and auth-type=" + authType;

        journalService.commit(Jane.Renew, userId, otherInfo, commit -> {

            final String at = wingsAuthTypeParser.parse(authType);
            final WinUserAnthnTable t = winUserAnthnDao.getTable();

            final Condition cond = t.onlyLive(t.AuthType.eq(at).and(t.UserId.eq(userId)));

            Map<Field<?>, Object> setter = new HashMap<>();

            if (renew.getPassword() != null) {
                final String slat = GlobalAttributeHolder.getAttr(WarlockUserAttribute.SaltByUid, userId);
                final String pass = passsaltEncoder.salt(renew.getPassword(), slat);
                setter.put(t.Password, passwordEncoder.encode(pass));
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

            final int rc = winUserAnthnDao.update(setter, cond, true);

            if (rc != 1) {
                log.warn("failed to renew {}, key={}, affect={}", otherInfo, userId, rc);
                throw new CodeException(CommonErrorEnum.DataNotFound);
            }
        });
    }

    @Override
    public @NotNull List<Item> list(long userId) {
        final WinUserAnthnTable t = winUserAnthnDao.getTable();
        return winUserAnthnDao.fetchLive(Item.class,
                t.Username,
                t.AuthType,
                t.ExpiredDt,
                t.FailedCnt);
    }
}
