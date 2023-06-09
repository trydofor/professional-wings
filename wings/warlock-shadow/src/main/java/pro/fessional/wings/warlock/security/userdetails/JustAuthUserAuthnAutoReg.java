package pro.fessional.wings.warlock.security.userdetails;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.enums.AuthUserGender;
import me.zhyd.oauth.model.AuthUser;
import org.jetbrains.annotations.NotNull;
import pro.fessional.mirana.best.AssertArgs;
import pro.fessional.wings.slardar.fastjson.FastJsonHelper;
import pro.fessional.wings.slardar.security.WingsAuthDetails;
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

    @Override
    protected void beforeSave(Basis basis, String username, WingsAuthDetails details) {
        AuthUser user = (AuthUser) details.getRealData();
        AssertArgs.notNull(user, "need JustAuth User");
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
    protected void beforeSave(Authn authn, String username, WingsAuthDetails details, long userId) {
        AuthUser user = (AuthUser) details.getRealData();
        AssertArgs.notNull(user, "need JustAuth User");
        authn.setUsername(user.getUuid());
        authn.setExtraPara(JSON.toJSONString(user.getToken(), FastJsonHelper.DefaultWriter()));
        authn.setExtraUser(JSON.toJSONString(user.getRawUserInfo(), FastJsonHelper.DefaultWriter()));
        log.info("uuid={}, userId={}", user.getUuid(), userId);
    }

    @Override
    public boolean accept(@NotNull Enum<?> authType, String username, WingsAuthDetails details) {
        return details.getRealData() instanceof AuthUser;
    }
}
