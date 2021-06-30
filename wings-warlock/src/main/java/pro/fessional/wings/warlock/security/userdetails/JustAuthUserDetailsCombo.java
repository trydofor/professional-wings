package pro.fessional.wings.warlock.security.userdetails;

import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.config.AuthSource;
import me.zhyd.oauth.model.AuthUser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pro.fessional.wings.warlock.constants.WarlockOrderConst;
import pro.fessional.wings.warlock.service.auth.WarlockAuthnService.Details;
import pro.fessional.wings.warlock.service.auth.impl.DefaultUserDetailsCombo;

/**
 * JustAuth UserDetailsService，不存在用户时，自动创建
 *
 * @author trydofor
 * @since 2021-02-22
 */
@Slf4j
public class JustAuthUserDetailsCombo extends DefaultUserDetailsCombo {

    public static final int ORDER = WarlockOrderConst.UserDetailsCombo + 9_000;

    public JustAuthUserDetailsCombo() {
        setOrder(ORDER);
    }

    @Override
    protected boolean authed(Enum<?> authType) {
        return true;
    }

    @Override
    protected Details doLoad(@NotNull Enum<?> authType, String username, @Nullable Object authDetail) {
        if (!(authType instanceof AuthSource)) return null;

        if (username.isEmpty() && authDetail instanceof AuthUser) {
            username = ((AuthUser) authDetail).getUuid();
            log.info("load auth-user by {} use uuid={}", authType, username);
        }
        return super.doLoad(authType, username, authDetail);
    }
}
