package pro.fessional.wings.warlock.security.userdetails;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.context.event.EventListener;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import pro.fessional.mirana.data.Null;
import pro.fessional.mirana.time.ThreadNow;
import pro.fessional.wings.slardar.security.PasssaltEncoder;
import pro.fessional.wings.slardar.security.PasswordHelper;
import pro.fessional.wings.slardar.security.WingsAuthDetails;
import pro.fessional.wings.slardar.security.WingsAuthTypeParser;
import pro.fessional.wings.slardar.security.impl.DefaultWingsUserDetails;
import pro.fessional.wings.warlock.event.auth.WarlockNonceSendEvent;
import pro.fessional.wings.warlock.service.auth.impl.DefaultUserDetailsCombo;

import java.util.Collections;
import java.util.Set;

/**
 * A one-time credential login that expires immediately after use,
 * regardless of whether authentication passes or fails.
 *
 * @author trydofor
 * @see WarlockNonceSendEvent
 * @since 2021-02-22
 */
@Slf4j
@Setter @Getter
@RequiredArgsConstructor
public class NonceUserDetailsCombo extends DefaultUserDetailsCombo {

    private final @NotNull Cache cache;
    private @NotNull Set<Enum<?>> acceptNonceType = Collections.emptySet();

    @Setter(onMethod_ = {@Autowired})
    protected PasswordEncoder passwordEncoder;
    @Setter(onMethod_ = {@Autowired})
    protected PasssaltEncoder passsaltEncoder;
    @Setter(onMethod_ = {@Autowired})
    protected WingsAuthTypeParser authTypeParser;

    @Override
    @Nullable
    public UserDetails postAudit(@NotNull UserDetails useDetail, String username, @NotNull Enum<?> authType, @Nullable WingsAuthDetails authDetail) {
//        log.warn("DEBUG_ONLY postAudit, this={}, cache={}, user={}, type={}", System.identityHashCode(this), System.identityHashCode(cache), username, authType);
        if (authType != Null.Enm && !acceptNonceType.contains(authType)) {
            return useDetail;
        }

        final String key = cacheKey(authType, useDetail.getUsername());
        final WarlockNonceSendEvent event = cache.get(key, WarlockNonceSendEvent.class);
        if (event == null) return useDetail;
        //
        cache.evict(key);
        if (event.getExpired() < ThreadNow.millis()) {
            log.info("nonce expired username={}", useDetail.getUsername());
            return useDetail;
        }

        if (useDetail instanceof DefaultWingsUserDetails details) {
            log.debug("nonce change password, username={}", useDetail.getUsername());
            PasswordHelper helper = new PasswordHelper(passwordEncoder, passsaltEncoder);
            details.setPassword(helper.hash(event.getNonce(), details.getPasssalt()));
        }

        return useDetail;
    }

    @EventListener
    public void handleNonceSendEvent(WarlockNonceSendEvent event) {
        String key = cacheKey(event.getAuthType(), event.getUsername());
        cache.put(key, event);
//        log.warn("DEBUG_ONLY handleNonceSendEvent, this={}, cache={}, event={}", System.identityHashCode(this), System.identityHashCode(cache), event);
        log.info("put WarlockNonceSendEvent to cache={}, key={}", cache.getName(), key);
    }

    private String cacheKey(Enum<?> authType, String username) {
        return username + "@" + authTypeParser.parse(authType);
    }
}
