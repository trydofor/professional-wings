package pro.fessional.wings.warlock.service.auth.impl;

import lombok.Data;
import org.cache2k.Cache;
import pro.fessional.wings.slardar.cache.cache2k.WingsCache2k;
import pro.fessional.wings.slardar.context.Now;
import pro.fessional.wings.warlock.service.auth.WarlockDangerService;

/**
 * @author trydofor
 * @since 2023-07-10
 */
public class WarlockDangerServiceImpl implements WarlockDangerService {

    @Data
    protected static class Ck {
        private final Enum<?> authType;
        private final String username;
    }


    protected final Cache<Ck, Long> cache;

    public WarlockDangerServiceImpl(int size, int ttl) {
        cache = WingsCache2k.builder(this.getClass(), "WarlockDangerService",
                size, ttl, ttl, Ck.class, Long.class).build();

    }

    @Override
    public void block(Enum<?> authType, String username, int seconds) {
        cache.put(new Ck(authType, username), Now.millis() + seconds * 1000L);
    }

    @Override
    public int check(Enum<?> authType, String username) {
        final Long block = cache.get(new Ck(authType, username));
        if (block == null) return -1;
        return (int) ((block - Now.millis()) / 1000);
    }

    @Override
    public void allow(Enum<?> authType, String username) {
        cache.remove(new Ck(authType, username));
    }
}
