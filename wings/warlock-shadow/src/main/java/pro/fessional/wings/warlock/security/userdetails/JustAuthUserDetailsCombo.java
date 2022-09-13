package pro.fessional.wings.warlock.security.userdetails;

import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.config.AuthSource;
import me.zhyd.oauth.model.AuthUser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pro.fessional.wings.slardar.security.WingsAuthDetails;
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

    @Override
    public boolean authed(Enum<?> authType) {
        return true;
    }

    @Override
    public Details doLoad(String username, @NotNull Enum<?> authType, @Nullable WingsAuthDetails authDetail) {
        if (!(authType instanceof AuthSource) || authDetail == null) return null;

        final Object authUser = authDetail.getRealData();
        if (username.isEmpty() && authUser instanceof AuthUser) {
            username = ((AuthUser) authUser).getUuid();
            log.info("load auth-user by {} use uuid={}", authType, username);
        }
        return super.doLoad(username, authType, authDetail);
    }
}
