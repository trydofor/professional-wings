package pro.fessional.wings.warlock.security.userdetails;

import com.alibaba.fastjson.JSON;
import me.zhyd.oauth.enums.AuthUserGender;
import me.zhyd.oauth.model.AuthUser;
import pro.fessional.wings.warlock.database.autogen.tables.pojos.WinUserAnthn;
import pro.fessional.wings.warlock.database.autogen.tables.pojos.WinUserBasic;
import pro.fessional.wings.warlock.enums.autogen.UserGender;
import pro.fessional.wings.warlock.enums.autogen.UserStatus;
import pro.fessional.wings.warlock.service.auth.impl.CommonUserAuthnSaver;

/**
 * @author trydofor
 * @since 2021-02-25
 */
public class JustAuthUserAuthnSaver extends CommonUserAuthnSaver {

    public static final int ORDER = CommonUserAuthnSaver.ORDER - 10;

    public JustAuthUserAuthnSaver() {
        setOrder(ORDER);
    }

    @Override
    protected void beforeInsert(WinUserBasic pojo, Enum<?> authType, String username, Object details) {
        AuthUser user = (AuthUser) details;
        pojo.setNickname(user.getNickname());
        pojo.setAvatar(user.getAvatar());
        final AuthUserGender aug = user.getGender();
        if (aug == AuthUserGender.FEMALE) {
            pojo.setGender(UserGender.FEMALE);
        } else if (aug == AuthUserGender.MALE) {
            pojo.setGender(UserGender.MALE);
        } else {
            pojo.setGender(UserGender.UNKNOWN);
        }
        pojo.setRemark(user.getRemark());
        pojo.setStatus(UserStatus.ACTIVE);
    }

    @Override
    protected void beforeInsert(WinUserAnthn pojo, Enum<?> authType, String username, Object details) {
        AuthUser user = (AuthUser) details;
        pojo.setUsername(user.getUuid());
        pojo.setExtraPara(JSON.toJSONString(user.getToken()));
        pojo.setExtraUser(JSON.toJSONString(user.getRawUserInfo()));
    }

    @Override
    public boolean accept(Enum<?> authType, String username, Object details) {
        return details instanceof AuthUser;
    }
}
