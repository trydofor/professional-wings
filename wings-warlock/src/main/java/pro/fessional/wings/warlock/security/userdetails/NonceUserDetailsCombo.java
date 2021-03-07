package pro.fessional.wings.warlock.security.userdetails;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.www.NonceExpiredException;
import pro.fessional.mirana.best.StateAssert;
import pro.fessional.mirana.cast.EnumConvertor;
import pro.fessional.wings.slardar.security.PasssaltEncoder;
import pro.fessional.wings.warlock.constants.WarlockOrderConst;
import pro.fessional.wings.warlock.event.auth.WarlockNonceSendEvent;
import pro.fessional.wings.warlock.service.auth.WarlockAuthnService.Details;
import pro.fessional.wings.warlock.service.auth.impl.DefaultUserDetailsCombo;

import java.util.Collections;
import java.util.Set;

/**
 * 一次性凭证登录，使用后立即失效，不论验证通过与否。
 *
 * @author trydofor
 * @see WarlockNonceSendEvent
 * @since 2021-02-22
 */
@Slf4j
@Setter
@Getter
public class NonceUserDetailsCombo extends DefaultUserDetailsCombo {

    public static final int ORDER = WarlockOrderConst.UserDetailsCombo + 1_000;

    private CacheManager cacheManager;
    private String cacheName;
    private Set<Enum<?>> acceptNonceType = Collections.emptySet();

    @Setter(onMethod_ = {@Autowired})
    private PasswordEncoder passwordEncoder;
    @Setter(onMethod_ = {@Autowired})
    private PasssaltEncoder passsaltEncoder;

    public NonceUserDetailsCombo() {
        setOrder(ORDER);
    }

    @Override
    protected boolean accept(Enum<?> authType) {
        return acceptNonceType.contains(authType);
    }

    @Override
    protected Details doLoad(@NotNull Enum<?> authType, String username, @Nullable Object authDetail) {
        final Cache cache = getCache();
        final String key = cacheKey(authType, username);
        final WarlockNonceSendEvent event = cache.get(key, WarlockNonceSendEvent.class);
        if (event == null) return null;
        //
        cache.evict(key);
        if (event.getExpired() < System.currentTimeMillis()) {
            throw new NonceExpiredException("nonce expired username=" + username);
        }

        final Details details = super.doLoad(authType, username, authDetail);
        if (details != null) {
            String plainPass = passsaltEncoder.salt(event.getNonce(), details.getPasssalt());
            details.setPassword(passwordEncoder.encode(plainPass));
        }
        return details;
    }

    @EventListener
    public void handleNonceSendEvent(WarlockNonceSendEvent event) {
        final Cache cache = getCache();
        String key = cacheKey(event.getAuthType(), event.getUsername());
        cache.put(key, event);
        log.info("put WarlockNonceSendEvent to cache={}, key={}", cacheName, key);
    }

    private String cacheKey(Enum<?> authType, String username) {
        return username + "@" + EnumConvertor.enum2Str(authType);
    }

    private Cache getCache() {
        final Cache cache = cacheManager.getCache(cacheName);
        StateAssert.notNull(cache, "can not find cache={}", cacheName);
        return cache;
    }
}
