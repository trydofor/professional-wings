package pro.fessional.wings.slardar.cache;

import org.jetbrains.annotations.NotNull;
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

    public static void setMemory(CacheManager cm) {
        memory = cm;
    }

    public static void setServer(CacheManager cm) {
        server = cm;
    }
}
