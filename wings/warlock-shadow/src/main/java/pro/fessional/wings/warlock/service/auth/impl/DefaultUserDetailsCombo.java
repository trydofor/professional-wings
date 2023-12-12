package pro.fessional.wings.warlock.service.auth.impl;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import pro.fessional.wings.silencer.spring.WingsOrdered;
import pro.fessional.wings.slardar.event.EventPublishHelper;
import pro.fessional.wings.slardar.security.WingsAuthDetails;
import pro.fessional.wings.slardar.security.impl.ComboWingsUserDetailsService;
import pro.fessional.wings.slardar.security.impl.DefaultWingsUserDetails;
import pro.fessional.wings.warlock.event.auth.WarlockAutoRegisterEvent;
import pro.fessional.wings.warlock.service.auth.WarlockAuthnService;
import pro.fessional.wings.warlock.service.auth.WarlockAuthnService.Details;
import pro.fessional.wings.warlock.service.auth.WarlockAuthzService;

import java.util.HashSet;
import java.util.Set;

/**
 * @author trydofor
 * @since 2021-02-22
 */
@Slf4j
@Getter @Setter
public class DefaultUserDetailsCombo implements ComboWingsUserDetailsService.Combo<DefaultWingsUserDetails> {

    public static final int ORDER = WingsOrdered.Lv3Service + 900;

    private int order = ORDER;

    private Set<Enum<?>> autoRegisterType = new HashSet<>();

    @Setter(onMethod_ = {@Autowired})
    protected WarlockAuthnService warlockAuthnService;

    @Setter(onMethod_ = {@Autowired})
    protected WarlockAuthzService warlockAuthzService;

    @Override
    public final DefaultWingsUserDetails loadOrNull(String username, @NotNull Enum<?> authType, @Nullable WingsAuthDetails authDetail) {

        Details dt = doLoad(username, authType, authDetail);
        boolean at = false;
        if (dt == null && canRegister(username, authType, authDetail)) {
            dt = doRegister(username, authType, authDetail);
            if (dt != null) {
                at = true;
                EventPublishHelper.SyncSpring.publishEvent(new WarlockAutoRegisterEvent(dt));
            }
        }

        if (dt == null) return null;

        if (at) {
            log.debug("autoreg auth-user, username={}, auth-type={}, class={}", username, authType, this.getClass());
        }
        else {
            log.debug("loading auth-user, username={}, auth-type={}, class={}", username, authType, this.getClass());
        }

        final DefaultWingsUserDetails wud = newUserDetails(dt);
        warlockAuthnService.auth(wud, dt);
        warlockAuthzService.auth(wud);
        if (!wud.isPreAuthed()) {
            // If there is no pre-authentication, auto register consider as logged in.
            wud.setPreAuthed(at || asAuthed(wud));
        }

        return wud;
    }

    protected DefaultWingsUserDetails newUserDetails(@NotNull Details dt) {
        return new DefaultWingsUserDetails();
    }

    /**
     * register user if it can be registered
     *
     * @see #canRegister(String, Enum, WingsAuthDetails)
     */
    protected Details doRegister(String username, @NotNull Enum<?> authType, @Nullable WingsAuthDetails authDetail) {
        return warlockAuthnService.register(authType, username, authDetail);
    }

    /**
     * can register if load null.
     */
    protected boolean canRegister(String username, @NotNull Enum<?> authType, @Nullable WingsAuthDetails authDetail) {
        return autoRegisterType.contains(authType);
    }

    /**
     * Whether to pass the auth (preAuth), default false
     */
    public boolean asAuthed(@NotNull DefaultWingsUserDetails details) {
        return false;
    }

    /**
     * Load details
     */
    @Nullable
    public Details doLoad(String username, @NotNull Enum<?> authType, @Nullable WingsAuthDetails authDetail) {
        return warlockAuthnService.load(authType, username);
    }
}
