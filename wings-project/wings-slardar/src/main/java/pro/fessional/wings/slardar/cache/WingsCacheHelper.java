package pro.fessional.wings.slardar.cache;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import pro.fessional.mirana.best.StateAssert;

/**
 * @author trydofor
 * @since 2021-03-08
 */
public class WingsCacheHelper {

    private static CacheManager memory;
    private static CacheManager server;

    protected WingsCacheHelper(CacheManager mem, CacheManager ser) {
        memory = mem;
        server = ser;
    }

    @Nullable
    public static CacheManager getCacheManager(String name) {
        if (WingsCache.Manager.Memory.equalsIgnoreCase(name)) {
            return memory;
        }
        if (WingsCache.Manager.Server.equalsIgnoreCase(name)) {
            return server;
        }
        return null;
    }

    @Nullable
    public static Cache getCache(String manager, String cache) {
        final CacheManager cm = getCacheManager(manager);
        return cm == null ? null : cm.getCache(cache);
    }

    @NotNull
    public static Cache getMemoryCache(String name) {
        final Cache cache = memory.getCache(name);
        StateAssert.notNull(cache, "memory cache is null, name={}", name);
        return cache;
    }

    @NotNull
    public static Cache getServerCache(String name) {
        final Cache cache = server.getCache(name);
        StateAssert.notNull(cache, "server cache is null, name={}", name);
        return cache;
    }


    @NotNull
    public static CacheManager getMemory() {
        StateAssert.notNull(memory, "Memory CacheManager is null");
        return memory;
    }

    @NotNull
    public static CacheManager getServer() {
        StateAssert.notNull(server, "Server CacheManager is null");
        return server;
    }
}
