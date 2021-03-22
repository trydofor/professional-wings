package pro.fessional.wings.warlock.security.userdetails;

import com.alibaba.fastjson.JSON;
import me.zhyd.oauth.enums.AuthUserGender;
import me.zhyd.oauth.model.AuthUser;
import org.jetbrains.annotations.NotNull;
import pro.fessional.wings.warlock.database.autogen.tables.pojos.WinUserAnthn;
import pro.fessional.wings.warlock.enums.autogen.UserGender;
import pro.fessional.wings.warlock.enums.autogen.UserStatus;
import pro.fessional.wings.warlock.service.auth.impl.DefaultUserAuthnCombo;
import pro.fessional.wings.warlock.service.user.WarlockUserBasicService;

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
    protected void beforeSave(WarlockUserBasicService.User dot, @NotNull Enum<?> authType, String username, Object details) {
        AuthUser user = (AuthUser) details;
        dot.setNickname(user.getNickname());
        dot.setAvatar(user.getAvatar());
        final AuthUserGender aug = user.getGender();
        if (aug == AuthUserGender.FEMALE) {
            dot.setGender(UserGender.FEMALE);
        } else if (aug == AuthUserGender.MALE) {
            dot.setGender(UserGender.MALE);
        } else {
            dot.setGender(UserGender.UNKNOWN);
        }
        dot.setRemark(user.getRemark());
        dot.setStatus(UserStatus.ACTIVE);
    }

    @Override
    protected void beforeSave(WinUserAnthn pojo, @NotNull Enum<?> authType, String username, Object details) {
        AuthUser user = (AuthUser) details;
        pojo.setUsername(user.getUuid());
        pojo.setExtraPara(JSON.toJSONString(user.getToken()));
        pojo.setExtraUser(JSON.toJSONString(user.getRawUserInfo()));
    }

    @Override
    public boolean accept(@NotNull Enum<?> authType, String username, Object details) {
        return details instanceof AuthUser;
    }
}
