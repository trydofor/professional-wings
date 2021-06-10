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
import pro.fessional.wings.warlock.service.auth.WarlockAuthnService;
import pro.fessional.wings.warlock.service.auth.WarlockAuthnService.Details;
import pro.fessional.wings.warlock.service.auth.WarlockAuthzService;

import java.util.Collection;
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

    @Getter
    @Setter
    private int order = ORDER;

    private final Set<Enum<?>> autoRegisterType = new HashSet<>();

    @Setter(onMethod_ = {@Autowired})
    private WarlockAuthnService warlockAuthnService;
    @Setter(onMethod_ = {@Autowired})
    private WarlockAuthzService warlockAuthzService;

    @Override
    public DefaultWingsUserDetails loadOrNull(String username, @NotNull Enum<?> authType, @Nullable Object authDetail) {
        if (!accept(authType)) {
            return null;
        }

        Details dt = doLoad(authType, username, authDetail);

        if (dt == null && autoRegisterType.contains(authType)) {
            log.info("auto-register user by auth-user, username={}, auth-type={}", username, authType);
            dt = warlockAuthnService.register(authType, username, authDetail);
            EventPublishHelper.SyncSpring.publishEvent(new WarlockAutoRegisterEvent(dt));
        }

        if (dt == null) {
            log.info("can not load user by username={}, auth-type={} , without auto-register", username, authType);
            return null;
        }

        DefaultWingsUserDetails wud = new DefaultWingsUserDetails();
        warlockAuthnService.auth(wud, dt);
        warlockAuthzService.auth(wud);
        wud.setPreAuthed(authed(authType));

        return wud;
    }

    /**
     * 是否能处理，默认true
     *
     * @param authType 类型
     * @return true
     */
    protected boolean accept(Enum<?> authType) {
        return true;
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

    ///
    public void addAutoRegisterType(Enum<?> en) {
        autoRegisterType.add(en);
    }

    public void addAutoRegisterType(Collection<? extends Enum<?>> en) {
        autoRegisterType.addAll(en);
    }
}
