package pro.fessional.wings.warlock.security.userdetails;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.model.AuthUser;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import pro.fessional.wings.slardar.security.impl.ComboWingsUserDetailsService;
import pro.fessional.wings.slardar.security.impl.DefaultWingsUserDetails;
import pro.fessional.wings.warlock.constants.WarlockOrderConst;
import pro.fessional.wings.warlock.service.auth.WarlockAuthnService;
import pro.fessional.wings.warlock.service.auth.WarlockAuthzService;

/**
 * JustAuth UserDetailsService，不存在用户时，自动创建
 *
 * @author trydofor
 * @since 2021-02-22
 */
@Getter
@Setter
@Slf4j
public class CommonUserDetailsCombo implements ComboWingsUserDetailsService.Combo<DefaultWingsUserDetails> {

    public static final int ORDER = WarlockOrderConst.UserDetailsCombo + 1000;

    private int order = ORDER;
    private boolean autoCreate = true;

    @Setter(onMethod = @__({@Autowired}))
    private WarlockAuthnService warlockAuthnService;
    @Setter(onMethod = @__({@Autowired}))
    private WarlockAuthzService warlockAuthzService;

    @Override
    public DefaultWingsUserDetails loadOrNull(String username, @Nullable Enum<?> authType, @Nullable Object authDetail) {
        if (!accept(authType)) {
            return null;
        }

        username = judgeUsername(username, authType, authDetail);

        WarlockAuthnService.Details dt = warlockAuthnService.load(authType, username);
        if (dt == null && autoCreate) {
            log.info("auto-create user by auth-user, username={}, auth-type={}", username, authType);
            dt = warlockAuthnService.save(authType, username, authDetail);
        }

        if (dt == null) {
            log.info("can not load user by username={}, auth-type={}, auto-create={}", username, authType, autoCreate);
            return null;
        }

        DefaultWingsUserDetails wud = new DefaultWingsUserDetails();
        warlockAuthnService.auth(wud, dt);
        warlockAuthzService.auth(wud);
        wud.setPreAuthed(authed(authType));

        return wud;
    }

    protected boolean accept(Enum<?> authType) {
        return true;
    }

    protected boolean authed(Enum<?> authType) {
        return false;
    }

    protected String judgeUsername(String username, @Nullable Enum<?> authType, @Nullable Object authDetail) {
        if (username.isEmpty() && authDetail instanceof AuthUser) {
            username = ((AuthUser) authDetail).getUuid();
        }
        return username;
    }
}
