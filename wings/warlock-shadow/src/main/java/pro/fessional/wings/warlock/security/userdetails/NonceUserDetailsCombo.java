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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import pro.fessional.mirana.best.StateAssert;
import pro.fessional.mirana.data.Null;
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
 * 一次性凭证登录，使用后立即失效，不论验证通过与否。
 *
 * @author trydofor
 * @see WarlockNonceSendEvent
 * @since 2021-02-22
 */
@Slf4j
@Setter @Getter
public class NonceUserDetailsCombo extends DefaultUserDetailsCombo {

    private CacheManager cacheManager;
    private String cacheName;
    private Set<Enum<?>> acceptNonceType = Collections.emptySet();

    @Setter(onMethod_ = {@Autowired})
    protected PasswordEncoder passwordEncoder;
    @Setter(onMethod_ = {@Autowired})
    protected PasssaltEncoder passsaltEncoder;
    @Setter(onMethod_ = {@Autowired})
    protected WingsAuthTypeParser authTypeParser;

    @Override
    @Nullable
    public UserDetails postAudit(@NotNull UserDetails useDetail, String username, @NotNull Enum<?> authType, @Nullable WingsAuthDetails authDetail) {
        if (authType != Null.Enm && !acceptNonceType.contains(authType)) {
            return useDetail;
        }

        final Cache cache = getCache();
        final String key = cacheKey(authType, useDetail.getUsername());
        final WarlockNonceSendEvent event = cache.get(key, WarlockNonceSendEvent.class);
        if (event == null) return useDetail;
        //
        cache.evict(key);
        if (event.getExpired() < System.currentTimeMillis()) {
            log.info("nonce expired username={}", useDetail.getUsername());
            return useDetail;
        }

        if (useDetail instanceof DefaultWingsUserDetails) {
            DefaultWingsUserDetails details = (DefaultWingsUserDetails) useDetail;
            PasswordHelper helper = new PasswordHelper(passwordEncoder, passsaltEncoder);
            details.setPassword(helper.hash(event.getNonce(), details.getPasssalt()));
        }

        return useDetail;
    }

    @EventListener
    public void handleNonceSendEvent(WarlockNonceSendEvent event) {
        final Cache cache = getCache();
        String key = cacheKey(event.getAuthType(), event.getUsername());
        cache.put(key, event);
        log.info("put WarlockNonceSendEvent to cache={}, key={}", cacheName, key);
    }

    private String cacheKey(Enum<?> authType, String username) {
        return username + "@" + authTypeParser.parse(authType);
    }

    private Cache getCache() {
        final Cache cache = cacheManager.getCache(cacheName);
        StateAssert.notNull(cache, "can not find cache={}", cacheName);
        return cache;
    }
}
