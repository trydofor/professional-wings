package pro.fessional.wings.warlock.security.userdetails;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.enums.AuthUserGender;
import me.zhyd.oauth.model.AuthUser;
import org.jetbrains.annotations.NotNull;
import pro.fessional.wings.warlock.enums.autogen.UserGender;
import pro.fessional.wings.warlock.enums.autogen.UserStatus;
import pro.fessional.wings.warlock.service.auth.impl.DefaultUserAuthnAutoReg;

import static pro.fessional.wings.warlock.service.user.WarlockUserAuthnService.Authn;
import static pro.fessional.wings.warlock.service.user.WarlockUserBasisService.Basis;

/**
 * @author trydofor
 * @since 2021-02-25
 */
@Slf4j
public class JustAuthUserAuthnAutoReg extends DefaultUserAuthnAutoReg {

    public static final int ORDER = DefaultUserAuthnAutoReg.ORDER - 10;

    public JustAuthUserAuthnAutoReg() {
        setOrder(ORDER);
    }

    @Override
    protected void beforeSave(Basis basis, String username, Object details) {
        AuthUser user = (AuthUser) details;
        basis.setNickname(user.getNickname());
        basis.setAvatar(user.getAvatar());
        final AuthUserGender aug = user.getGender();
        if (aug == AuthUserGender.FEMALE) {
            basis.setGender(UserGender.FEMALE);
        }
        else if (aug == AuthUserGender.MALE) {
            basis.setGender(UserGender.MALE);
        }
        else {
            basis.setGender(UserGender.UNKNOWN);
        }
        basis.setRemark(user.getRemark());
        basis.setStatus(UserStatus.ACTIVE);
        log.info("nickName={}, Gender={}", user.getNickname(), aug);
    }

    @Override
    protected void beforeSave(Authn authn, String username, Object details, long userId) {
        AuthUser user = (AuthUser) details;
        authn.setUsername(user.getUuid());
        authn.setExtraPara(JSON.toJSONString(user.getToken()));
        authn.setExtraUser(JSON.toJSONString(user.getRawUserInfo()));
        log.info("uuid={}, userId={}", user.getUuid(), userId);
    }

    @Override
    public boolean accept(@NotNull Enum<?> authType, String username, Object details) {
        return details instanceof AuthUser;
    }
}
