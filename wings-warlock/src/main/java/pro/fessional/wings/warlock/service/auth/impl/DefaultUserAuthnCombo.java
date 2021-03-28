package pro.fessional.wings.warlock.service.auth.impl;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pro.fessional.mirana.code.RandCode;
import pro.fessional.wings.faceless.service.journal.JournalService;
import pro.fessional.wings.slardar.security.PasssaltEncoder;
import pro.fessional.wings.warlock.constants.WarlockOrderConst;
import pro.fessional.wings.warlock.enums.autogen.UserGender;
import pro.fessional.wings.warlock.enums.autogen.UserStatus;
import pro.fessional.wings.warlock.service.auth.WarlockAuthnService;
import pro.fessional.wings.warlock.service.user.WarlockUserAuthnService;
import pro.fessional.wings.warlock.service.user.WarlockUserBasisService;
import pro.fessional.wings.warlock.spring.prop.WarlockSecurityProp;

import java.time.ZoneId;
import java.util.Locale;

/**
 * @author trydofor
 * @since 2021-02-25
 */
@Service
@Slf4j
public class DefaultUserAuthnCombo implements ComboWarlockAuthnService.Combo {

    public static final int ORDER = WarlockOrderConst.UserAuthnCombo + 10_000;

    @Getter
    @Setter
    private int order = ORDER;

    @Setter(onMethod_ = {@Autowired})
    private WarlockUserBasisService warlockUserBasisService;

    @Setter(onMethod_ = {@Autowired})
    private WarlockUserAuthnService warlockUserAuthnService;

    @Setter(onMethod_ = {@Autowired})
    private WarlockSecurityProp warlockSecurityProp;

    @Setter(onMethod_ = {@Autowired})
    private JournalService journalService;

    @Setter(onMethod_ = {@Autowired})
    private PasssaltEncoder passsaltEncoder;

    @Override
    @Transactional
    public WarlockAuthnService.Details create(@NotNull Enum<?> authType, String username, Object details) {

        final String mrk = "auto create auth-user auth-type=" + authType + "username=" + username;
        return journalService.submit(WarlockAuthnService.Jane.AutoSave, username, mrk, commit -> {

            WarlockUserBasisService.User user = new WarlockUserBasisService.User();
            user.setNickname(username);
            user.setAvatar("");
            user.setGender(UserGender.UNKNOWN);
            user.setLocale(Locale.getDefault());
            user.setZoneid((ZoneId.systemDefault()));
            user.setRemark("auto register");
            user.setStatus(UserStatus.UNINIT);

            beforeSave(user, authType, username, details);
            long uid = warlockUserBasisService.create(user);
            //
            WarlockUserAuthnService.Authn authn = new WarlockUserAuthnService.Authn();

            authn.setUsername(username);
            authn.setExtraPara("");
            authn.setExtraUser("");
            authn.setPasssalt(passsaltEncoder.salt(60));

            authn.setPassword(RandCode.human(16));
            authn.setExpiredDt(commit.getCommitDt().plusSeconds(warlockSecurityProp.getAutoregExpired().getSeconds()));
            authn.setFailedCnt(0);
            authn.setFailedMax(warlockSecurityProp.getAutoregMaxFailed());

            beforeSave(authn, authType, username, details);
            warlockUserAuthnService.create(uid, authType, authn);

            final WarlockAuthnService.Details result = new WarlockAuthnService.Details();
            result.setUserId(uid);
            result.setNickname(user.getNickname());
            result.setLocale(user.getLocale());
            result.setZoneId(user.getZoneid());
            result.setStatus(user.getStatus());
            result.setAuthType(authType);

            result.setUsername(authn.getUsername());
            result.setPassword(authn.getPassword());
            result.setPasssalt(authn.getPasssalt());
            result.setExpiredDt(authn.getExpiredDt());

            return result;
        });
    }

    protected void beforeSave(WarlockUserBasisService.User dto, @NotNull Enum<?> authType, String username, Object details) {
    }

    protected void beforeSave(WarlockUserAuthnService.Authn dto, @NotNull Enum<?> authType, String username, Object details) {
    }

    @Override
    public boolean accept(@NotNull Enum<?> authType, String username, Object details) {
        return false;
    }
}
