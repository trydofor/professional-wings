package pro.fessional.wings.warlock.service.auth.impl;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.fessional.mirana.code.RandCode;
import pro.fessional.wings.faceless.service.journal.JournalService;
import pro.fessional.wings.faceless.service.lightid.LightIdService;
import pro.fessional.wings.slardar.security.PasssaltEncoder;
import pro.fessional.wings.slardar.security.WingsAuthTypeParser;
import pro.fessional.wings.warlock.constants.WarlockOrderConst;
import pro.fessional.wings.warlock.database.autogen.tables.daos.WinUserAnthnDao;
import pro.fessional.wings.warlock.database.autogen.tables.pojos.WinUserAnthn;
import pro.fessional.wings.warlock.enums.autogen.UserGender;
import pro.fessional.wings.warlock.enums.autogen.UserStatus;
import pro.fessional.wings.warlock.service.auth.WarlockAuthnService;
import pro.fessional.wings.warlock.service.user.WarlockUserBasicService;
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
    private WarlockUserBasicService warlockUserBasicService;

    @Setter(onMethod_ = {@Autowired})
    private WinUserAnthnDao winUserAnthnDao;

    @Setter(onMethod_ = {@Autowired})
    private WingsAuthTypeParser wingsAuthTypeParser;

    @Setter(onMethod_ = {@Autowired})
    private WarlockSecurityProp warlockSecurityProp;

    @Setter(onMethod_ = {@Autowired})
    private LightIdService lightIdService;

    @Setter(onMethod_ = {@Autowired})
    private JournalService journalService;

    @Setter(onMethod_ = {@Autowired})
    private PasssaltEncoder passsaltEncoder;

    @Override
    public WarlockAuthnService.Details create(@NotNull Enum<?> authType, String username, Object details) {

        final String at = wingsAuthTypeParser.parse(authType);
        final String mrk = "auto create auth-user auth-type=" + at + "username=" + username;
        return journalService.submit(WarlockAuthnService.Jane.AutoSave, username, mrk, commit -> {

            WarlockUserBasicService.User user = new WarlockUserBasicService.User();
            user.setNickname(username);
            user.setAvatar("");
            user.setGender(UserGender.UNKNOWN);
            user.setLocale(Locale.getDefault());
            user.setZoneid((ZoneId.systemDefault()));
            user.setRemark("auto register");
            user.setStatus(UserStatus.UNINIT);

            beforeSave(user, authType, username, details);
            long uid = warlockUserBasicService.createUser(user);
            //
            WinUserAnthn auth = new WinUserAnthn();
            commit.create(auth);
            auth.setId(lightIdService.getId(winUserAnthnDao.getTable().getClass()));
            auth.setUserId(uid);
            auth.setAuthType(at);
            auth.setUsername(username);
            auth.setExtraPara("");
            auth.setExtraUser("");
            auth.setFailedCnt(0);

            long seconds;
            if (details instanceof WarlockAuthnService.Authn) {
                final WarlockAuthnService.Authn an = (WarlockAuthnService.Authn) details;
                seconds = an.getExpiredIn().getSeconds();
                auth.setFailedMax(an.getMaxFailed());
                auth.setPassword(an.getPassword());
            } else {
                seconds = warlockSecurityProp.getAutoregExpired().getSeconds();
                auth.setFailedMax(warlockSecurityProp.getAutoregMaxFailed());
                auth.setPassword(RandCode.human(16));
            }
            auth.setPasssalt(passsaltEncoder.salt(60));
            auth.setExpiredDt(commit.getCommitDt().plusSeconds(seconds));

            beforeSave(auth, authType, username, details);
            winUserAnthnDao.insert(auth);

            final WarlockAuthnService.Details result = new WarlockAuthnService.Details();
            result.setUserId(uid);
            result.setNickname(user.getNickname());
            result.setLocale(user.getLocale());
            result.setZoneId(user.getZoneid());
            result.setStatus(user.getStatus());
            result.setAuthType(authType);
            result.setUsername(auth.getUsername());
            result.setPassword(auth.getPassword());
            result.setPasssalt(auth.getPasssalt());
            result.setExpiredDt(auth.getExpiredDt());

            return result;
        });
    }

    protected void beforeSave(WarlockUserBasicService.User dto, @NotNull Enum<?> authType, String username, Object details) {
    }

    protected void beforeSave(WinUserAnthn pojo, @NotNull Enum<?> authType, String username, Object details) {
    }

    @Override
    public boolean accept(@NotNull Enum<?> authType, String username, Object details) {
        return false;
    }
}
