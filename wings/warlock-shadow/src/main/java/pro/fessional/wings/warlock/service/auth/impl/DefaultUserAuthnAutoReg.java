package pro.fessional.wings.warlock.service.auth.impl;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import pro.fessional.mirana.code.RandCode;
import pro.fessional.wings.faceless.service.journal.JournalService;
import pro.fessional.wings.silencer.spring.WingsOrdered;
import pro.fessional.wings.slardar.context.AttributeHolder;
import pro.fessional.wings.slardar.context.TerminalContext;
import pro.fessional.wings.slardar.security.WingsAuthDetails;
import pro.fessional.wings.warlock.constants.WarlockGlobalAttribute;
import pro.fessional.wings.warlock.enums.autogen.UserGender;
import pro.fessional.wings.warlock.enums.autogen.UserStatus;
import pro.fessional.wings.warlock.service.auth.WarlockAuthnService;
import pro.fessional.wings.warlock.service.auth.WarlockAuthnService.Details;
import pro.fessional.wings.warlock.service.user.WarlockUserAuthnService;
import pro.fessional.wings.warlock.service.user.WarlockUserBasisService;
import pro.fessional.wings.warlock.spring.prop.WarlockSecurityProp;

import static pro.fessional.wings.warlock.service.user.WarlockUserAuthnService.Authn;
import static pro.fessional.wings.warlock.service.user.WarlockUserBasisService.Basis;

/**
 * @author trydofor
 * @since 2021-02-25
 */
@Slf4j
@Getter @Setter
public class DefaultUserAuthnAutoReg implements ComboWarlockAuthnService.AutoReg {

    public static final int ORDER = WingsOrdered.Lv3Service + 10;
    private int order = ORDER;

    @Setter(onMethod_ = {@Autowired})
    protected WarlockUserBasisService warlockUserBasisService;

    @Setter(onMethod_ = {@Autowired})
    protected WarlockUserAuthnService warlockUserAuthnService;

    @Setter(onMethod_ = {@Autowired})
    protected WarlockSecurityProp warlockSecurityProp;

    @Setter(onMethod_ = {@Autowired})
    protected JournalService journalService;

    @Override
    @Transactional
    public Details create(@NotNull Enum<?> authType, String username, WingsAuthDetails details) {

        final String mrk = "auto create auth-user auth-type=" + authType + "username=" + username;
        return journalService.submit(WarlockAuthnService.Jane.AutoSave, username, mrk, commit -> {
            Basis user = new Basis();
            user.setNickname(username);
            user.setAvatar("");
            user.setGender(UserGender.UNKNOWN);
            user.setLocale(TerminalContext.defaultLocale());
            user.setZoneId(TerminalContext.defaultZoneId());
            user.setRemark("auto register");
            user.setStatus(UserStatus.UNINIT);

            Long uid = beforeSave(user, username, details);
            if (uid == null) {
                uid = warlockUserBasisService.create(user);
                log.debug("auto register user authType={}, username={}, userId={}", authType, username, uid);
                afterSave(user, username, details, uid);
            }
            //
            Authn authn = new Authn();
            authn.setAuthType(authType);
            authn.setUsername(username);
            authn.setExtraPara("");
            authn.setExtraUser("");
            authn.setExpiredDt(commit.getCommitDt().plusSeconds(warlockSecurityProp.getAutoregExpired().getSeconds()));
            authn.setFailedCnt(0);
            authn.setFailedMax(warlockSecurityProp.getAutoregMaxFailed());

            // Plain text, encrypt in WarlockUserAuthnService later.
            authn.setPassword(RandCode.human(16));

            Long aid = beforeSave(authn, username, details, uid);
            if (aid == null) {
                aid = warlockUserAuthnService.create(uid, authn);
                log.debug("auto register auth authType={}, username={}, authId={}", authType, username, aid);
                afterSave(authn, username, details, uid, aid);
            }

            final Details result = new Details();
            result.setUserId(uid);
            result.setNickname(user.getNickname());
            result.setLocale(user.getLocale());
            result.setZoneId(user.getZoneId());
            result.setStatus(user.getStatus());
            result.setAuthType(authType);

            result.setUsername(authn.getUsername());
            result.setPassword(authn.getPassword());
            result.setPasssalt(AttributeHolder.tryAttr(WarlockGlobalAttribute.SaltByUid, uid));
            result.setExpiredDt(authn.getExpiredDt());

            return result;
        });
    }

    /**
     * nonnull return value means existed user
     */
    protected Long beforeSave(Basis basis, String username, WingsAuthDetails details) {
        return null;
    }

    protected void afterSave(Basis basis, String username, WingsAuthDetails details, long userId) {
    }

    /**
     * nonnull return value means existed authn
     */
    protected Long beforeSave(Authn authn, String username, WingsAuthDetails details, long userId) {
        return null;
    }

    protected void afterSave(Authn authn, String username, WingsAuthDetails details, long userId, long authId) {
    }

    @Override
    public boolean accept(@NotNull Enum<?> authType, String username, WingsAuthDetails details) {
        return false;
    }
}
