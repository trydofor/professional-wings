package pro.fessional.wings.warlock.service.auth.impl;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import pro.fessional.wings.slardar.event.EventPublishHelper;
import pro.fessional.wings.slardar.security.impl.ComboWingsUserDetailsService;
import pro.fessional.wings.slardar.security.impl.DefaultWingsUserDetails;
import pro.fessional.wings.warlock.constants.WarlockOrderConst;
import pro.fessional.wings.warlock.event.auth.WarlockAutoRegisterEvent;
import pro.fessional.wings.warlock.security.session.NonceTokenSessionHelper;
import pro.fessional.wings.warlock.service.auth.WarlockAuthnService;
import pro.fessional.wings.warlock.service.auth.WarlockAuthnService.Details;
import pro.fessional.wings.warlock.service.auth.WarlockAuthzService;

import java.util.HashSet;
import java.util.Set;

/**
 * JustAuth UserDetailsService，不存在用户时，自动创建
 *
 * @author trydofor
 * @since 2021-02-22
 */
@Slf4j
public class DefaultUserDetailsCombo implements ComboWingsUserDetailsService.Combo<DefaultWingsUserDetails> {

    public static final int ORDER = WarlockOrderConst.UserDetailsCombo + 10_000;

    @Getter @Setter
    private int order = ORDER;

    @Getter @Setter
    private Set<Enum<?>> autoRegisterType = new HashSet<>();

    @Setter(onMethod_ = {@Autowired})
    protected WarlockAuthnService warlockAuthnService;

    @Setter(onMethod_ = {@Autowired})
    protected WarlockAuthzService warlockAuthzService;

    @Override
    public final DefaultWingsUserDetails loadOrNull(String username, @NotNull Enum<?> authType, @Nullable Object authDetail) {

        Details dt = doLoad(authType, username, authDetail);
        boolean at = false;
        if (dt == null && autoRegisterType.contains(authType)) {
            dt = warlockAuthnService.register(authType, username, authDetail);
            if (dt != null) {
                at = true;
                EventPublishHelper.SyncSpring.publishEvent(new WarlockAutoRegisterEvent(dt));
            }
        }

        if (dt == null) return null;

        if (at) {
            log.info("autoreg auth-user, username={}, auth-type={}, class={}", username, authType, this.getClass());
        }
        else {
            log.info("loading auth-user, username={}, auth-type={}, class={}", username, authType, this.getClass());
        }

        final DefaultWingsUserDetails wud = new DefaultWingsUserDetails();
        warlockAuthnService.auth(wud, dt);
        warlockAuthzService.auth(wud);
        if (!wud.isPreAuthed()) {
            // 无前置验证时，自动注册认为可登录
            wud.setPreAuthed(at || authed(authType));
        }

        NonceTokenSessionHelper.swapNonceUid(dt.getUserId(), authDetail);

        return wud;
    }

    /**
     * 是否验证过，默认false
     *
     * @param authType 类型
     * @return false
     */
    protected boolean authed(Enum<?> authType) {
        return false;
    }

    /**
     * 加载信息
     */
    @Nullable
    protected Details doLoad(@NotNull Enum<?> authType, String username, @Nullable Object authDetail) {
        return warlockAuthnService.load(authType, username);
    }
}
