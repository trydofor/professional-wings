package pro.fessional.wings.warlock.security.userdetails;

import com.alibaba.fastjson.JSON;
import me.zhyd.oauth.enums.AuthUserGender;
import me.zhyd.oauth.model.AuthUser;
import org.jetbrains.annotations.NotNull;
import pro.fessional.wings.warlock.enums.autogen.UserGender;
import pro.fessional.wings.warlock.enums.autogen.UserStatus;
import pro.fessional.wings.warlock.service.auth.impl.DefaultUserAuthnCombo;

import static pro.fessional.wings.warlock.service.user.WarlockUserAuthnService.Authn;
import static pro.fessional.wings.warlock.service.user.WarlockUserBasisService.Basis;

/**
 * @author trydofor
 * @since 2021-02-25
 */
public class JustAuthUserAuthnCombo extends DefaultUserAuthnCombo {

    public static final int ORDER = DefaultUserAuthnCombo.ORDER - 10;

    public JustAuthUserAuthnCombo() {
        setOrder(ORDER);
    }

    @Override
    protected void beforeSave(Basis basis, @NotNull Enum<?> authType, String username, Object details) {
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
    }

    @Override
    protected void beforeSave(Authn authn, @NotNull Enum<?> authType, String username, Object details) {
        AuthUser user = (AuthUser) details;
        authn.setUsername(user.getUuid());
        authn.setExtraPara(JSON.toJSONString(user.getToken()));
        authn.setExtraUser(JSON.toJSONString(user.getRawUserInfo()));
    }

    @Override
    public boolean accept(@NotNull Enum<?> authType, String username, Object details) {
        return details instanceof AuthUser;
    }
}
